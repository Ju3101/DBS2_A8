package de.hsh.dbs2.imdb.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import DBS2.Aufgabe6.Entity.*;
import de.hsh.dbs2.imdb.logic.dto.CharacterDTO;
import de.hsh.dbs2.imdb.logic.dto.MovieDTO;
import de.hsh.dbs2.imdb.util.DBConnection;

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
        ArrayList<Movie> movieList;
        boolean hasSearch = search != null && !search.isEmpty();

        if (hasSearch) {
            movieList = MovieFactory.findByTitle(search);
		} else {
            movieList = MovieFactory.getAll();
		}

        ArrayList<MovieDTO> filteredMovies = new ArrayList<>();
        for (Movie m : movieList) {
            filteredMovies.add(buildMovieDTO(m));
        }
        return filteredMovies;
	}

	/**
	 * Speichert die uebergebene Version des Films neu in der Datenbank oder aktualisiert den
	 * existierenden Film.
	 * Dazu werden die Daten des Films selbst (Titel, Jahr, Typ) beruecksichtigt,
	 * aber auch alle Genres, die dem Film zugeordnet sind und die Liste der Charaktere
	 * auf den neuen Stand gebracht.
	 * @param movieDTO Film-Objekt mit Genres und Charakteren.
     */

    public void insertUpdateMovie(MovieDTO movieDTO) throws Exception {

        Movie movie;
        HashSet<String> dtoGenres = (HashSet<String>) movieDTO.getGenres();
        ArrayList<CharacterDTO> dtoCharacters = (ArrayList<CharacterDTO>) movieDTO.getCharacters();

        //FILM NICHT VORHANDEN --> NEUEN ANLEGEN
        if (movieDTO.getId() == null) {

            try {

                //MOVIE OBJEKT ANLEGEN
                movie = new Movie();
                movie.setMovieID(IDGen.getNextIdBySeqName("seq_movie"));
                movie.setTitle(movieDTO.getTitle());
                movie.setYear(movieDTO.getYear());
                movie.setType(movieDTO.getType());
                movie.insert();

                //DTO-GENRES MIT NEUEM FILM VERKNÜPFEN (MovieGenre-Tabelle)
                connectGenresToMovie(dtoGenres, movie.getMovieID());

                //DTO-FILMCHARAKTERE MIT FILM VERKNÜPFEN (MovieCharacter-Tabelle)
                connectMovieCharactersToMovie(dtoCharacters, movie.getMovieID());

            } catch (Exception e) {
                e.printStackTrace();
                DBConnection.getConnection().rollback();
            }

            // Fall: FILM VORHANDEN --> UPDATEN
        } else {

            try {
                movie = MovieFactory.findById(movieDTO.getId());

                //MOVIE OBJEKT UPDATEN
                movie.setTitle(movieDTO.getTitle());
                movie.setType(movieDTO.getTitle());
                movie.setYear(movieDTO.getYear());
                movie.update();

                //MOVIE-GENRES LÖSCHEN, UM NEUE VERSION ANZULEGEN
                HashSet<MovieGenre> currentMgs = MovieGenreFactory.getByMovieId(movie.getMovieID());
                for (MovieGenre mg : currentMgs) {
                    mg.delete();
                }

                //MOVIE-GENRES mit Movie verknüpfen
                connectGenresToMovie(dtoGenres, movie.getMovieID());

                //MOVIE-CHARACTERS LÖSCHEN, UM NEUE VERSION ANZULEGEN
                ArrayList<MovieCharacter> mcs = MovieCharacterFactory.getMovieCharactersByMovieID(movie.getMovieID());
                for (MovieCharacter mc : mcs) {
                    mc.delete();
                }

                //MOVIE-CHARACTERS NEU ANLEGEN
                connectMovieCharactersToMovie(dtoCharacters, movie.getMovieID());


            } catch (Exception e) {
                DBConnection.getConnection().rollback();
                e.printStackTrace();
            }
        }

        DBConnection.getConnection().commit();
    }

    /**
     * Erstelle anhand der ArrayList an Charakter-Namen und dem angegebenen Film MovieCharacter-Tupel
     * auf der DB
     * @param dtoCharacters
     * @param movieId
     * @throws Exception
     */
    private void connectMovieCharactersToMovie(ArrayList<CharacterDTO> dtoCharacters, int movieId) throws Exception {
        int pos = 1;

        for (CharacterDTO cdto : dtoCharacters) {
            MovieCharacter mc = new MovieCharacter();
            mc.setMovCharID(IDGen.getNextIdBySeqName("seq_moviecharacter"));
            mc.setMovieID(movieId);
            Person p = PersonFactory.findByName(cdto.getPlayer());
            mc.setPersonID(p.getPersonID());
            mc.setCharacter(cdto.getCharacter());
            mc.setAlias(cdto.getAlias());
            mc.setPosition(pos);
            pos++;
            mc.insert();
        }
    }

    /**
     * Verknüpfe in der Relation MovieGenre das HashSet mit den Genre-Namen mit dem angegebenen
     * Film
     * @param dtoGenres
     * @param movieId
     * @throws Exception
     */
    private void connectGenresToMovie(HashSet<String> dtoGenres, int movieId) throws Exception {

        for (String genreName : dtoGenres) {
            Genre currentGenre = GenreFactory.findByGenreText(genreName);
            MovieGenre mg = new MovieGenre();
            mg.setMovieID(movieId);
            mg.setGenreID(currentGenre.getGenreID());
            mg.insert();
        }
    }

	/**
	 * Loescht einen Film aus der Datenbank. Es werden auch alle abhaengigen Objekte geloescht,
	 * d.h. alle Charaktere und alle Genre-Zuordnungen.
	 * @param movieId id des zu löschenden Films
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public void deleteMovie(int movieId) throws Exception {
        //Löscht Film
        try {
            Movie movie = MovieFactory.findById(movieId);
            movie.delete();
        } catch (Exception e) {
            DBConnection.getConnection().rollback();
            throw new Exception("Fehler beim Löschen des Movies");
        }

        //Löscht alle MovieGenre einträge des Films
        try {
            HashSet<MovieGenre> movieGenre = MovieGenreFactory.getByMovieId(movieId);
            for(MovieGenre mg : movieGenre) {
                mg.delete();
            }
        } catch (Exception e) {
            DBConnection.getConnection().rollback();
            throw new Exception("Fehler beim Löschen der MovieGenres");
        }

        //Löscht alle MovieCharacters des Filmes
        try {
            ArrayList<MovieCharacter> movieCharacters = MovieCharacterFactory.getMovieCharactersByMovieID(movieId);
            for (MovieCharacter mc : movieCharacters) {
                mc.delete();
            }
        } catch (Exception e) {
            DBConnection.getConnection().rollback();
            throw new Exception("Fehler beim Löschen der MovieCharacters");
        }

        DBConnection.getConnection().commit();
	}

	/**
	 * Ermittelt alle Daten zu einem Movie (d.h. auch Genres und Charaktere) und
	 * trägt diese Daten in einem MovieDTO-Objekt ein.
	 * @param movieId ID des Films der eingelesen wird.
	 * @return MovieDTO-Objekt mit allen Informationen zu dem Film
	 * @throws Exception Z.B. bei Datenbank-Fehlern oder falls der Movie nicht existiert.
	 */
	public MovieDTO getMovie(int movieId) throws Exception {
        return buildMovieDTO(MovieFactory.findById(movieId));
	}

    /**
     * Erstellt ein `MovieDTO` für den gegebenen Film mit seinen Genres und Charakteren.
     *
     * @param movie Movie für den ein `MovieDTO` erstellt werden soll.
     * @return Initialisiertes MovieDTO
     * @throws Exception
     */
    private MovieDTO buildMovieDTO(Movie movie) throws Exception {
        // Initialisiere MovieDTO-Objekt.
        MovieDTO movieDTO = new MovieDTO();
        movieDTO.setId(movie.getMovieID());
        movieDTO.setTitle(movie.getTitle());
        movieDTO.setYear(movie.getYear());
        movieDTO.setType(movie.getType());

        // Frage Genres des Films ab und füge sie dem DTO hinzu.
        HashSet<MovieGenre> movieGenres = MovieGenreFactory.getByMovieId(movie.getMovieID());
        for (MovieGenre movieGenre : movieGenres) {
            // Frage Genre-Daten aus Genre-Tabelle ab.
            Genre genre = GenreFactory.findById(movieGenre.getGenreID());
            movieDTO.addGenre(genre.getGenre());
        }

        // Frage Charaktere des Films ab und füge sie dem DTO hinzu.
        ArrayList<MovieCharacter> movieCharacters = MovieCharacterFactory.getMovieCharactersByMovieID(movie.getMovieID());
        for (MovieCharacter movieCharacter : movieCharacters) {
            CharacterDTO characterDTO = new CharacterDTO();
            characterDTO.setCharacter(movieCharacter.getCharacter());
            characterDTO.setAlias(movieCharacter.getAlias());

            // Frage Personendaten des Players aus Personen-Tabelle ab.
            Person player = PersonFactory.findByID(movieCharacter.getPersonID());
            characterDTO.setPlayer(player.getName());

            movieDTO.addCharacter(characterDTO);
        }

        return movieDTO;
    }
	
}
