package de.hsh.inform.dbs2.entities;

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

    @OneToMany(mappedBy = "player", cascade = CascadeType.PERSIST)
    private Set<MovieCharacter> movieCharacters = new HashSet<>();
}
