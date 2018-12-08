package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.item.Deck;

public class Player
{
    private static final int DEFAULT_LIFE_COUNT = 3;

    private Deck deck;
    private int lifes;

    protected Player()
    {
        lifes = DEFAULT_LIFE_COUNT;
        deck = new Deck(3);
    }

}
