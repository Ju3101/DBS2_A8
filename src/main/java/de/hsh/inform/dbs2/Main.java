package de.hsh.inform.dbs2;

import de.hsh.inform.dbs2.entities.Genre;
import de.hsh.inform.dbs2.entities.Movie;
import de.hsh.inform.dbs2.entities.MovieCharacter;
import de.hsh.inform.dbs2.entities.Person;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.*;

import java.util.List;

public class Main {

    public static void main(String [] args) {
        createMovieWithGenreAndMovieCharacters();
    }

    public static void createMovieWithGenreAndMovieCharacters() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            try {

                em.getTransaction().begin();

                Movie movie = new Movie("Star Wars IV", "C", 1977);
                MovieCharacter mc = new MovieCharacter("Han-Solo", "Han", 2);
                Genre genre = new Genre("Sci-Fi");
                Person actor = new Person("Harrison Ford");

                movie.addMovieCharacter(mc);
                movie.addGenre(genre);
                genre.addMovie(movie);
                mc.setMovie(movie);
                mc.setActor(actor);
                actor.addMovieCharacter(mc);

                em.persist(movie);
                em.persist(actor);
                em.persist(mc);

                em.getTransaction().commit();
            } catch (Exception e) {
                e.printStackTrace();
                em.getTransaction().rollback();
            }
        }


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

    public static void createMovieCharacter() {

        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            try {
                em.getTransaction().begin();

                MovieCharacter mc = new MovieCharacter("Darth Vader", "test", 1);
                em.persist(mc);
                em.getTransaction().commit();

            } catch (Exception e) {
                e.printStackTrace();
                em.getTransaction().rollback();
                throw e;
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
