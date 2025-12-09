package de.hsh.dbs2.imdb.logic;

import de.hsh.dbs2.imdb.util.EMFSingleton;
import jakarta.persistence.EntityManager;

import java.util.List;

public class PersonManager {

	/**
	 * Liefert eine Liste aller Personen, deren Name den Suchstring enthaelt.
	 * @param name Suchstring
	 * @return Liste mit passenden Personennamen, die in der Datenbank eingetragen sind.
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public List<String> getPersonList(String name) throws Exception {
		try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {

			try {
				em.getTransaction().begin();

				boolean hasSearch = name != null && !name.isEmpty();
				List<String> result;
				if (hasSearch) {
					result = em.createQuery(
							"SELECT p.name FROM Person AS p WHERE p.name LIKE :name",
							String.class
					).setParameter("name", "%" + name + "%").getResultList();
				} else {
					result = em.createQuery("SELECT p.name FROM Person AS p", String.class).getResultList();
				}

				em.getTransaction().commit();
				return result;

			} catch (Exception e) {
				em.getTransaction().rollback();
				throw e;
			}

		}
	}

	/**
	 * Liefert die ID einer Person, deren Name genau name ist. Wenn die Person nicht existiert,
	 * wird eine Exception geworfen.
	 * @param name Exakter Name der Person
	 * @return ID der Person
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public int getPerson(String name) throws Exception {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Name darf nicht leer sein");
		}

		try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {

			int result;
			try {
				em.getTransaction().begin();
				result = em.createQuery("SELECT p.id FROM Person AS p WHERE p.name = :name", Integer.class)
						.setParameter("name", name).getSingleResult();
				em.getTransaction().commit();
				return result;
			} catch (Exception e) {
				em.getTransaction().rollback();
				throw e;
			}

		}
	}
}
