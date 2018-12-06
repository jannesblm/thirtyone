package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.item.*;

public class Game
{
    public static void main(String[] args)
    {
        Deck deck2 = Deck.newDeck();

        for (Card c : deck2) {
            System.out.println(c);
        }

        System.out.println(deck2.size());
    }
}
