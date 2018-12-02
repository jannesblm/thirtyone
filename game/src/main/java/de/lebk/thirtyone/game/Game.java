package de.lebk.thirtyone.game;

import de.lebk.thirtyone.game.network.Server;

public class Game
{
    public static void main(String[] args)
    {
        /*Deck deck2 = new Deck(3);

        try {
            Card king = new Card(Suit.CLUBS, 10, Symbol.KING);
            Card king2 = new Card(Suit.CLUBS, 10, Symbol.KING);
            Card king3 = new Card(Suit.CLUBS, 10, Symbol.KING);
            deck2.add(king);
            deck2.add(king2);
            deck2.add(king3);
            deck2.add(new Card(Suit.CLUBS, 9, Symbol.NUMBER));
        } catch (DeckOutOfBoundsException exception) {
            exception.printStackTrace();
        }

        System.out.println(deck2);*/

        Server server = new Server(25565);
        try {
            server.run();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
    }
}
