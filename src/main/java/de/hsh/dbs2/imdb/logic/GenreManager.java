package de.hsh.dbs2.imdb.logic;

import DBS2.Aufgabe6.Entity.Genre;
import DBS2.Aufgabe6.Entity.GenreFactory;

import java.util.ArrayList;
import java.util.List;

public class GenreManager {

	/**
	 * Ermittelt eine vollstaendige Liste aller in der Datenbank abgelegten Genres
	 * Die Genres werden alphabetisch sortiert zurueckgeliefert.
	 * @return Alle Genre-Namen als String-Liste
	 * @throws Exception error describing e.g. the database problem
	 */
	public List<String> getGenres() throws Exception {
        ArrayList<String> genreStrs = new ArrayList<>();

        List<Genre> genres = GenreFactory.findAll();
        for (Genre genre : genres) {
            genreStrs.add(genre.getGenre());
        }

        return genreStrs;
	}
}
