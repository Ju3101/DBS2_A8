package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UE08_PERSON")
public class Person {

    @Id  @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int personID;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "actor", cascade = CascadeType.ALL)
    private Set<MovieCharacter> movieCharacters = new HashSet<>();

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public void addMovieCharacter(MovieCharacter mc) {
        movieCharacters.add(mc);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return personID;
    }
}
