package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity()
@Table(name = "UE08_MOVIE")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String type;

    private int year;

    @ManyToMany(mappedBy = "movies", cascade = CascadeType.PERSIST)
    private Set<Genre> genres = new HashSet<>();

    @OneToMany (mappedBy = "movie", cascade = CascadeType.PERSIST)
    private Set<MovieCharacter> movieCharacters = new HashSet<>();

    public Movie(String title, String type, int year) {
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void addGenre(Genre genre) {
        genres.add(genre);
    }

    public void addMovieCharacter(MovieCharacter mc) {
        movieCharacters.add(mc);
    }

    public void setMovieCharacterSet(HashSet<MovieCharacter> mcs) {
        movieCharacters = mcs;
    }

    public Set<MovieCharacter> getMovieCharacters() {
        return movieCharacters;
    }
}
