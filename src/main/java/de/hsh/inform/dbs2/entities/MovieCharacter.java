package de.hsh.inform.dbs2.entities;

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
    private Person player;

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

    public Person getPlayer() {
        return player;
    }
}
