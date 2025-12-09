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

    @ManyToMany
    private Set<Movie> movies = new HashSet<>();

    public Genre() {}

    public Genre(String genre) {
        this.genre = genre;
    }

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public int getId() {
        return genreID;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

}
