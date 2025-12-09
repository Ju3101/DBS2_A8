package de.hsh.dbs2.imdb.logic;

import de.hsh.dbs2.imdb.entities.Genre;
import de.hsh.dbs2.imdb.util.EMFSingleton;
import jakarta.persistence.EntityManager;

import java.util.List;

public class GenreManager {

	/**
	 * Ermittelt eine vollstaendige Liste aller in der Datenbank abgelegten Genres
	 * Die Genres werden alphabetisch sortiert zurueckgeliefert.
	 * @return Alle Genre-Namen als String-Liste
	 * @throws Exception error describing e.g. the database problem
	 */
	public List<String> getGenres() throws Exception {

        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {

            try {
                em.getTransaction().begin();
                List<String> genres = em.createQuery("SELECT g.genre FROM Genre AS g").getResultList();
                em.getTransaction().commit();
                return genres;
            } catch (Exception e) {
                e.printStackTrace();
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
}
