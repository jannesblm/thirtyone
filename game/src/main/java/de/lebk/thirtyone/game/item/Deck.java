package de.lebk.thirtyone.game.item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Deck implements Iterable<Card>
{
    private List<Card> cards;
    private int limit;

    public Deck()
    {
        this(0);
    }

    public Deck(int limit)
    {
        cards = new ArrayList<>();

        if (limit < 0) {
            throw new IllegalArgumentException("Invalid range for limit argument");
        }

        this.limit = limit;
    }

    public Iterator<Card> iterator()
    {
        return cards.iterator();
    }

    public void add(Card card) throws DeckOutOfBoundsException
    {
        if (violatesBounds(1)) {
            throw new DeckOutOfBoundsException("Modification violates deck's bounds: Size should be <= " + limit);
        }

        cards.add(card);
    }


    public void remove(Card card) throws DeckOutOfBoundsException
    {
        if (violatesBounds(-1)) {
            throw new DeckOutOfBoundsException("Modification violates deck's bounds: Size should be > 0");
        }

        cards.remove(card);
    }

    private boolean violatesBounds(int change)
    {
        return cards.size() + change > limit || cards.size() + change <= 0;
    }

    public static Deck newDeck()
    {
        Deck deck = new Deck(32);

        for (Suit suit : Suit.values()) {
            for (int value = Card.CARD_VALUE_MIN; value <= Card.CARD_VALUE_MAX; value++) {
                deck.cards.add(new Card(suit, value));
            }
        }

        return deck;
    }
}
