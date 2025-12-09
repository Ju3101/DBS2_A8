package de.hsh.dbs2.imdb.logic;

import DBS2.Aufgabe6.Entity.Person;
import DBS2.Aufgabe6.Entity.PersonFactory;
import java.util.ArrayList;
import java.util.List;

public class PersonManager {

	/**
	 * Liefert eine Liste aller Personen, deren Name den Suchstring enthaelt.
	 * @param name Suchstring
	 * @return Liste mit passenden Personennamen, die in der Datenbank eingetragen sind.
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public List<String> getPersonList(String name) throws Exception {
        ArrayList<String> names = new ArrayList<>();

        ArrayList<Person> people = PersonFactory.findAll();

        boolean hasSearch = name != null && !name.isEmpty();
        for (Person person : people) {
            if (hasSearch) {
                if (person.getName().toLowerCase().contains(name.toLowerCase())) {
                    names.add(person.getName());
                }
            } else {
                names.add(person.getName());
            }
        }

        return names;
	}

	/**
	 * Liefert die ID einer Person, deren Name genau name ist. Wenn die Person nicht existiert,
	 * wird eine Exception geworfen.
	 * @param name Exakter Name der Person
	 * @return ID der Person
	 * @throws Exception Beschreibt evtl. aufgetretenen Fehler
	 */
	public int getPerson(String name) throws Exception {
        return PersonFactory.findByName(name).getPersonID();
    }
}
