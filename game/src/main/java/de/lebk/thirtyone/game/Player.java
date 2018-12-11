package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.item.Deck;

import java.util.UUID;

public class Player
{
    protected static final int DEFAULT_LIFE_COUNT = 3;

    protected final UUID uuid;
    protected final Round round;
    protected Deck deck;
    protected int lifes;

    protected Player(UUID uuid, Round round, Deck deck, int lifes)
    {
        this.uuid = uuid;
        this.deck = deck;
        this.lifes = lifes;
        this.round = round;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public Deck getDeck()
    {
        return deck;
    }

    public boolean equals(Object object)
    {
        return object instanceof Player && ((Player) object).uuid.equals(this.uuid);
    }
}
