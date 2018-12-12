package de.lebk.thirtyone.game.item;

import de.lebk.thirtyone.game.Player;

class DeckTest
{

    @org.junit.jupiter.api.BeforeEach
    void setUp()
    {
    }

    @org.junit.jupiter.api.Test
    void add()
    {
    }

    @org.junit.jupiter.api.Test
    void remove()
    {

    }
    @org.junit.jupiter.api.Test
    void newDeck()
    {
    }

    @org.junit.jupiter.api.Test
    void swapCards()
    {
        Deck mainDeck = Deck.newDeck();

        Deck player = new Deck(3);
        player.add(mainDeck.get(1));
        player.add(mainDeck.get(2));
        player.add(mainDeck.get(3));


        Deck middle = new Deck(3);
        middle.add(mainDeck.get(4));
        middle.add(mainDeck.get(5));
        middle.add(mainDeck.get(6));

        System.out.println("PlayerDeck before");
        System.out.println(player.get(0).toString());
        System.out.println(player.get(1).toString());
        System.out.println(player.get(2).toString());
        System.out.println("MiddleDeck before");
        System.out.println(middle.get(0).toString());
        System.out.println(middle.get(1).toString());
        System.out.println(middle.get(2).toString());

        Card middleCard = middle.get(0);
        Card playerCard = player.get(0);


        player.swap(playerCard,middleCard);
        middle.swap(middleCard,playerCard);

        System.out.println("PlayerDeck after");
        System.out.println(player.get(0).toString());
        System.out.println(player.get(1).toString());
        System.out.println(player.get(2).toString());
        System.out.println("MIddleDeck after");
        System.out.println(middle.get(0).toString());
        System.out.println(middle.get(1).toString());
        System.out.println(middle.get(2).toString());
    }
}