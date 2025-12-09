package de.hsh.dbs2.imdb.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.entities.Movie;
import de.hsh.dbs2.imdb.entities.MovieCharacter;
import de.hsh.dbs2.imdb.logic.dto.CharacterDTO;
import de.hsh.dbs2.imdb.logic.dto.MovieDTO;
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
			Movie movie = new Movie();
			movie.setTitle(movieDTO.getTitle());
			movie.setYear(movieDTO.getYear());
			movie.setType(movieDTO.getType());

			TypedQuery<Genre> query = em.createQuery("SELECT g  FROM Genre g WHERE g.genre IN :genreParam", Genre.class);
			Set<String> genreName = movieDTO.getGenres();
			query.setParameter("genreParam", genreName);
			movie.getGenres().addAll(query.getResultList());

			em.persist(movie);
			em.getTransaction().commit();
			em.close();
		} catch (Exception e) {
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
		}
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
			Movie movie = em.find(Movie.class, movieId);

			if(movie != null) {
				em.remove(movie);
			}

			em.getTransaction().commit();
			em.close();
		} catch(Exception e){
			em.getTransaction().rollback();
			em.close();
			e.printStackTrace();
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

        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            try {
                em.getTransaction().begin();
                MovieDTO dto = new MovieDTO();
                TypedQuery<Movie> tq = em.createQuery("SELECT m FROM Movie AS m WHERE m.id = :movieId", Movie.class);
                tq.setParameter("movieId", movieId);
                Movie movie = tq.getSingleResult();

                dto.setId(movie.getId());
                dto.setTitle(movie.getTitle());
                dto.setType(movie.getType());
                Set<String> genreStrings = new HashSet<>();
                for (Genre g : movie.getGenres()) {
                    genreStrings.add(g.getGenre());
                }
                dto.setGenres(genreStrings);
                List<CharacterDTO> characterDTOs = new ArrayList<>();
                for (MovieCharacter mc : movie.getMovieCharacters()) {
                    characterDTOs.add(new CharacterDTO(mc.getCharacter(), mc.getAlias(), mc.getActor().getName()));
                }
                dto.setCharacters(characterDTOs);
                em.getTransaction().commit();
				return dto;

            } catch (Exception e) {
                e.printStackTrace();
                em.getTransaction().rollback();
            }
        }
		return null;
	}

}
