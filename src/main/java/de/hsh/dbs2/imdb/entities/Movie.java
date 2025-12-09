package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity()
@Table(name = "UE08_MOVIE")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String title;

    private String type;

    private int year;

    @ManyToMany(mappedBy = "movies", cascade = CascadeType.MERGE)
    private Set<Genre> genres = new HashSet<>();

    @OneToMany (mappedBy = "movie", cascade = CascadeType.MERGE)
    private Set<MovieCharacter> movieCharacters = new HashSet<>();

    public Movie(String title, String type, int year) {
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Movie() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
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
