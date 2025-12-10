package de.hsh.dbs2.imdb;

import de.hsh.dbs2.imdb.entities.Movie;
import de.hsh.dbs2.imdb.entities.MovieCharacter;
import de.hsh.dbs2.imdb.entities.Person;
import de.hsh.dbs2.imdb.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class TestClient {

    public static void insertMovieCharacter(int movieId, String characterName,
                                            String alias, String person) throws Exception {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {

            try {
                em.getTransaction().begin();
                MovieCharacter mc = new MovieCharacter();

                TypedQuery<Movie> tqm = em.createQuery("SELECT m FROM Movie AS m WHERE m.id = :movieId", Movie.class);
                tqm.setParameter("movieId", movieId);
                Movie movie = tqm.getSingleResult();


                TypedQuery<Person> tqp = em.createQuery("SELECT p FROM Person AS p WHERE p.name LIKE :person", Person.class);
                tqp.setParameter("person", "%" + person + "%");
                Person p = tqp.getSingleResult();

                movie.addMovieCharacter(mc);
                mc.setMovie(movie);
                mc.setActor(p);
                mc.setPosition(1);
                mc.setCharacter(characterName);
                mc.setAlias(alias);
                p.addMovieCharacter(mc);
                em.persist(mc);

                em.getTransaction().commit();

            } catch (Exception e) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {

            throw new Exception("Error performing MovieCharacter insertion");
        }
    }
}
