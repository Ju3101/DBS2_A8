package de.hsh.dbs2.imdb.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.entities.Movie;
import de.hsh.dbs2.imdb.entities.MovieCharacter;
import de.hsh.dbs2.imdb.entities.Person;
import de.hsh.dbs2.imdb.logic.dto.CharacterDTO;
import de.hsh.dbs2.imdb.logic.dto.MovieDTO;
import de.hsh.dbs2.imdb.persistence.DoesNotExistException;
import de.hsh.dbs2.imdb.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class MovieManager {

	/**
	 * Ermittelt alle Filme, deren Filmtitel den Suchstring enthaelt.
	 * Wenn der String leer ist, sollen alle Filme zurueckgegeben werden.
	 * Der Suchstring soll ohne Ruecksicht auf Gross-/Kleinschreibung verarbeitet werden.
	 * @param search Suchstring. 
	 * @return Liste aller passenden Filme als MovieDTO
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public List<MovieDTO> getMovieList(String search) throws Exception {

        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {

			try {
				em.getTransaction().begin();

				// Ergebnisliste.
				ArrayList<MovieDTO> dtos = new ArrayList<>();

				// Alle IDs von passenden Filmen holen.
				List<Integer> movieIds = em.createQuery(
						"SELECT m.id FROM Movie AS m WHERE LOWER(m.title) LIKE :search",
						Integer.class
				).setParameter("search", "%" + search.toLowerCase() + "%").getResultList();

				for (Integer movieId : movieIds) {
					dtos.add(getMovie(movieId));
                    System.out.println(dtos.getLast());
				}

				em.getTransaction().commit();
				return dtos;

			} catch (Exception e) {
				e.printStackTrace();
				em.getTransaction().rollback();
				throw e;
			}
        }
	}

	/**
	 * Speichert die uebergebene Version des Films neu in der Datenbank oder aktualisiert den
	 * existierenden Film.
	 * Dazu werden die Daten des Films selbst (Titel, Jahr, Typ) beruecksichtigt,
	 * aber auch alle Genres, die dem Film zugeordnet sind und die Liste der Charaktere
	 * auf den neuen Stand gebracht.
	 * @param movieDTO Film-Objekt mit Genres und Charakteren.
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public void insertUpdateMovie(MovieDTO movieDTO) throws Exception {
		EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
		try {
			em.getTransaction().begin();
			System.out.println("INSERTUPDATE()");

			Movie movie = movieDTO.getId() != null ? em.find(Movie.class, movieDTO.getId()) : null;

			// Falls Movie schon existiert, verwende managed Objekt von EntityManager.
			if (movie != null) {
				createMovie(movie, movieDTO, em);
			} else {
				// Ansonsten: Erstelle neues Movie-Objekt.
				movie = new Movie();
				createMovie(movie, movieDTO, em);
				em.persist(movie);
			}
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
			throw e;
		}
	}

	private void createMovie(Movie movie, MovieDTO movieDTO, EntityManager em) throws Exception {
		//Einstellen der eifnachen Attribute
		movie.setTitle(movieDTO.getTitle());
		movie.setYear(movieDTO.getYear());
		movie.setType(movieDTO.getType());

		//Übernahme der Genres
		for (Genre g : movie.getGenres()) {
			g.removeMovie(movie);
		}
		movie.getGenres().clear();

		TypedQuery<Genre> genreQuery = em.createQuery("SELECT g FROM Genre g WHERE g.genre IN :genreParam", Genre.class);
		Set<String> genreName = movieDTO.getGenres();
		genreQuery.setParameter("genreParam", genreName);
		movie.getGenres().addAll(genreQuery.getResultList());

		//Übernahme der Charactere
		Set<MovieCharacter> originalCharacters = movie.getMovieCharacters();
		List<CharacterDTO> dtoMovieCharacters = movieDTO.getCharacters();

		System.out.println("Character aktualisieren");
		movie.getMovieCharacters().clear();
		int i = 1;
		for (CharacterDTO cdto : dtoMovieCharacters) {
			MovieCharacter mc = createMovieCharacter(cdto, movie, em, i);
			originalCharacters.add(mc);
			i++;
		}

/*		for (MovieCharacter movieCharacter : originalCharacters) {
			for (CharacterDTO characterDTO : movieCharacters) {
				if(movieCharacter.getCharacter().equals(characterDTO.getCharacter())
						&& movieCharacter.getAlias().equals(characterDTO.getAlias())
						&& movieCharacter.getActor().getName().equals(characterDTO.getPlayer())) {
					continue;
				} else {
					originalCharacters.add(createMovieCharacter(characterDTO, movie, em, originalCharacters.size()+1));
				}
			}
		}*/
	}

	private static MovieCharacter createMovieCharacter(CharacterDTO characterDTO, Movie movie, EntityManager em, int pos) throws Exception {
		TypedQuery<Person> personQuery = em.createQuery("SELECT p  FROM Person p WHERE p.name = :personParam", Person.class);
		personQuery.setParameter("personParam", characterDTO.getPlayer());
		MovieCharacter movieCharacter = new MovieCharacter(characterDTO.getCharacter(), characterDTO.getAlias(), pos);
		movieCharacter.setActor(personQuery.getSingleResult());
		movieCharacter.setMovie(movie);
		return movieCharacter;
	}

	/**
	 * Loescht einen Film aus der Datenbank. Es werden auch alle abhaengigen Objekte geloescht,
	 * d.h. alle Charaktere und alle Genre-Zuordnungen.
	 * @param movieId id des zu löschenden Films
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public void deleteMovie(int movieId) throws Exception {
		EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
		try {
			em.getTransaction().begin();
			Movie movie = em.find(Movie.class, movieId);	//versucht Film zu finden, find gibt entweder null oder den Movie zurück

			if(movie != null) {		//überprüft, ob movie gefunden werden konnte, wenn ja wird er gelöscht
				em.remove(movie);
			}

			em.getTransaction().commit();
			em.close();
		} catch(Exception e){
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Ermittelt alle Daten zu einem Movie (d.h. auch Genres und Charaktere) und
	 * trägt diese Daten in einem MovieDTO-Objekt ein.
	 * @param movieId ID des Films der eingelesen wird.
	 * @return MovieDTO-Objekt mit allen Informationen zu dem Film
	 * @throws Exception Z.B. bei Datenbank-Fehlern oder falls der Movie nicht existiert.
	 */
	public MovieDTO getMovie(int movieId) throws Exception {
		EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
		try {
			em.getTransaction().begin();

			MovieDTO movieDto = new MovieDTO();
			TypedQuery<Movie> tqm = em.createQuery("SELECT m FROM Movie AS m WHERE m.id = :movieId", Movie.class)
					.setParameter("movieId", movieId);

			//Dem MovieDTO einzutragende Attribute vorbereiten
			Movie movie = tqm.getSingleResult();

			if (movie == null) {
				throw new DoesNotExistException("Fehler bei Erzeugung eines MovieDTO: Movie-Objekt nicht gefunden!");
			}

			Set<String> genreStrings = new HashSet<>();
			List<CharacterDTO> characterDTOs = new ArrayList<>();

			//Dem MovieDTO die Attribute zufügen
			for (Genre g : movie.getGenres()) {
				genreStrings.add(g.getGenre());
			}
			for (MovieCharacter mc : movie.getMovieCharacters()) {
				characterDTOs.add(createCharacterDTO(mc));
			}
			movieDto.setCharacters(characterDTOs);
			movieDto.setGenres(genreStrings);
			movieDto.setId(movie.getId());
			movieDto.setTitle(movie.getTitle());
			movieDto.setType(movie.getType());
			movieDto.setYear(movie.getYear());

			em.getTransaction().commit();
			return movieDto;

		} catch (Exception e) {
			em.getTransaction().rollback();
			e.printStackTrace();
			throw new Exception("Fehler bei Erstellung eines MovieDTOs!");
		}
	}

	private CharacterDTO createCharacterDTO(MovieCharacter mc) {
		return new CharacterDTO(mc.getCharacter(), mc.getAlias(), mc.getActor().getName());
	}
}
