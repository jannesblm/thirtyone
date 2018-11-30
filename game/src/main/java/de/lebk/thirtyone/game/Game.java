package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;

public class Game
{
    public static void main(String[] args)
    {
        Deck deck = Deck.newDeck();
        for (Card card : deck) {
            System.out.println(card);
        }
    }
}
