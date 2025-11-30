package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "UE08_GENRE")
public class Genre {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int genreID;

    private String genre;

    @ManyToMany(mappedBy = "genres, cascade = CascadeType.PERSIST")
    private Set<Movie> movies = new HashSet<>();

}
