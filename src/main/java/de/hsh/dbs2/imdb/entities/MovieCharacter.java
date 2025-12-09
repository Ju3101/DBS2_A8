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

    public MovieCharacter() {

    }

    public MovieCharacter(String character, String alias, int position) {
        this.character = character;
        this.alias = alias;
        this.position = position;
    }

    @ManyToOne
    private Person actor;

    @ManyToOne
    private Movie movie;

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setActor(Person actor) {
        this.actor = actor;
    }

    public int getMovCharID() {
        return movCharID;
    }

    public String getCharacter() {
        return character;
    }

    public String getAlias() {
        return alias;
    }

    public int getPosition() {
        return position;
    }

    public Person getActor() {
        return actor;
    }
}
