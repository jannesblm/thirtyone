package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.item.Deck;

import java.util.UUID;

public class Player
{
    public static final int DEFAULT_LIFE_COUNT = 3;

    private UUID uuid;
    protected Deck deck;
    protected int lifes;

    public Player(UUID uuid)
    {
        this.uuid = uuid;
        lifes = DEFAULT_LIFE_COUNT;
        deck = new Deck(3);
    }

    private UUID getUUID()
    {
        return uuid;
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof Player && ((Player) object).getUUID().equals(this.getUUID());
    }
}
