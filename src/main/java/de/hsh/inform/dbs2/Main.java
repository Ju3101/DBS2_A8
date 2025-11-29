package de.hsh.inform.dbs2;

import de.hsh.inform.dbs2.entities.Movie;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class Main {

    public static void main(String [] args) {
        createMovie();
        printMovies();
    }

    public static void createMovie() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                Movie movie = new Movie("Star Wars", "C", 1977);
                em.persist(movie);
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void printMovies() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                List<Movie> results = em.createQuery("SELECT m FROM Movie m WHERE m.year = :year", Movie.class).setParameter("year", 1977).getResultList();
                for (Movie movie : results) {
                    System.out.println(movie.getId() + ":" + movie.getTitle());
                }
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }



}
