package de.hsh.dbs2.imdb.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "UE08_MOVIECHARACTER")
public class MovieCharacter {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int movCharID;

    private String character;
    private String alias;
    private int position;

    @ManyToOne
    private Person actor;

    @ManyToOne
    private Movie movie;

    public MovieCharacter() {

    }

    public MovieCharacter(String character, String alias, int position) {
        this.character = character;
        this.alias = alias;
        this.position = position;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setActor(Person actor) {
        this.actor = actor;
    }

    public int getMovCharID() {
        return movCharID;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getCharacter() {
        return character;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Person getActor() {
        return actor;
    }

    public Movie getMovie() {
        return movie;
    }
}
