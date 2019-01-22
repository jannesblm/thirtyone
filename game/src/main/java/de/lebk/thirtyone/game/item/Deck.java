package de.lebk.thirtyone.game.item;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.json.DeckSerializer;
import de.lebk.thirtyone.game.json.JsonSerializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class Deck extends JsonSerializable<Deck> implements Iterable<Card>, Comparable<Deck>
{
    private static final Logger LOG = LogManager.getLogger();

    private final int limit;
    private final Random randomizer;
    private List<Card> cards;

    Deck(List<Card> cards)
    {
        this(cards.size());
        addAll(cards);
    }

    public Deck()
    {
        this(-1);
    }

    public Deck(int limit)
    {
        if (limit < -1) {
            throw new IllegalArgumentException("Invalid range for limit argument");
        }

        this.limit = limit;
        randomizer = new Random(Double.doubleToLongBits(Math.random()));
        cards = Collections.synchronizedList(new ArrayList<>());
    }

    /**
     * Creates a new, shuffled Skat deck
     *
     * @return A new, shuffled Skat deck
     */
    public static Deck newSkat()
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

        deck.shuffle();
        return deck;
    }

    public Iterator<Card> iterator()
    {
        return cards.iterator();
    }

    private void addAll(Collection<Card> cards)
    {
        cards.forEach(this::add);
    }

    /**
     * Adds a card to the deck. If the deck's limits will be exceeded by this action or the card already is present in
     * the deck this method will simply return.
     *
     * @param card The card to add
     */
    public void add(Card card)
    {
        if (violatesBounds(1)) {
            return;
        }

        if (cards.contains(card)) {
            return;
        }

        cards.add(card);
    }

    /**
     * Removes a card from the deck. If the deck's limits will be exceeded this method will simply return.
     *
     * @param card The card to add
     */
    public void remove(Card card)
    {
        if (violatesBounds(-1)) {
            return;
        }

        cards.remove(card);
    }

    /**
     * Determines if a change in size will violate the deck's limits
     *
     * @param change A positive or negative integer representing the change
     * @return boolean
     */
    private boolean violatesBounds(int change)
    {
        return (limit != -1 && cards.size() + change > limit) || cards.size() + change < 0;
    }

    /**
     * Deck point calculation conforming with the rules of Thirtyone
     *
     * @return int
     */
    public float getPoints()
    {
        // Group by value to check for three of a kind
        Map<Symbol, List<Card>> valueGroups = cards.stream()
                .collect(Collectors.groupingBy(Card::getSymbol));

        for (Map.Entry<Symbol, List<Card>> entry : valueGroups.entrySet()) {
            if (entry.getValue().size() < 3) {
                continue;
            }

            // This symbol set contains three cards. Check if their distinct values are equal to 1.
            int[] values = entry.getValue().stream()
                    .mapToInt(Card::getValue)
                    .distinct()
                    .toArray();

            if (values.length == 1) {
                // If they are, we have three of a kind. If it is three aces return 33, otherwise 30.5.
                return (values[0] == 11) ? 33f : 30.5f;
            }
        }

        // We did not find three of a kind
        // So map each Suit group to the sum of its cards values
        return cards.stream()
                .collect(Collectors.groupingBy(Card::getSuit))
                .entrySet()
                .stream()
                .mapToInt(e -> e.getValue()
                        .stream()
                        .mapToInt(Card::getValue)
                        .reduce(0, Integer::sum))
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

    private void shuffle()
    {
        Collections.shuffle(cards, randomizer);
    }

    private Card pop() throws DeckEmptyException
    {
        if (violatesBounds(-1)) {
            throw new DeckEmptyException();
        }

        return cards.remove(0);
    }

    public Deck deal(int amount) throws DeckEmptyException
    {
        if (this.size() < amount) {
            throw new DeckEmptyException();
        }

        Deck deal = new Deck(amount);

        for (int i = 0; i < amount; i++) {
            deal.add(pop());
        }

        return deal;
    }

    public boolean swap(Card c1, Card c2)
    {
        if (cards.contains(c2)) {
            return false;
        }

        return Collections.replaceAll(cards, c1, c2);
    }

    public List<Card> getCards()
    {
        return cards;
    }

    public int getLimit()
    {
        return limit;
    }

    public JsonElement toJson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(this.getClass(), new DeckSerializer())
                .create()
                .toJsonTree(this);
    }

    public Optional<Card> get(int index)
    {
        if (cards.size() > index) {
            return Optional.of(cards.get(index));
        }

        return Optional.empty();
    }
}
