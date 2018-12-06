package de.lebk.thirtyone.game.item;

import com.google.gson.Gson;

import java.util.TreeSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class Deck implements Iterable<Card>, Comparable<Deck>
{
    private Set<Card> cards;
    private final int limit;

    public Deck()
    {
        this(0);
    }

    public Deck(int limit)
    {
        cards = new TreeSet<>();

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
            throw new DeckOutOfBoundsException("Modification violates decks bounds: Size should be <= " + limit);
        }

        cards.add(card);
    }


    public void remove(Card card) throws DeckOutOfBoundsException
    {
        if (violatesBounds(-1)) {
            throw new DeckOutOfBoundsException("Modification violates decks bounds: Size should be > 0");
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
                if (value == 10) {
                    for (Symbol symbol : Symbol.values()) {
                        deck.cards.add(new Card(suit, value, symbol));
                    }
                } else {
                    deck.cards.add(new Card(suit, value, Symbol.NUMBER));
                }
            }
        }

        return deck;
    }

    /**
     * Deck point calculation conforming with the rules of Thirtyone
     *
     * @return int
     */
    private int getPoints()
    {
        // Map each Suit group to the sum of its cards values
        return cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit))
                .entrySet()
                .stream()
                .mapToInt(e -> e.getValue().stream().mapToInt(Card::getValue).reduce(0, Integer::sum))
                .max()
                .orElse(0);
    }

    @Override
    public int compareTo(Deck deck)
    {
        return (int) Math.signum(this.getPoints() - deck.getPoints());
    }

    public String toString()
    {
        return new Gson().toJson(this);
    }

    public int size()
    {
        return cards.size();
    }
}
