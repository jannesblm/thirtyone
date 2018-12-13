package de.lebk.thirtyone.game.item;

import de.lebk.thirtyone.game.json.JsonSerializable;

public class Card extends JsonSerializable<Card> implements Comparable<Card>
{
    static final int CARD_VALUE_MAX = 11;
    static final int CARD_VALUE_MIN = 7;
    
    private final int value;
    private final Suit suit;
    private final Symbol symbol;

    public Card(Suit suit, int value)
    {
        this(suit, value, Symbol.NUMBER);
    }

    Card(Suit suit, int value, Symbol symbol)
    {
        if (value > CARD_VALUE_MAX || value < CARD_VALUE_MIN) {
            throw new IllegalArgumentException("Card value " + value + " out of range");
        }

        this.value = value;
        this.suit = suit;
        this.symbol = symbol;
    }

    int getValue()
    {
        return value;
    }

    Suit getSuit()
    {
        return suit;
    }

    private Symbol getSymbol()
    {
        return symbol;
    }

    /**
     * This method does not conform to in-game comparison rules and should not be used to determine a winning deck.
     * The Symbol's rank is considered when both, the value and the Suit's rank, are equal. This is to preserve the
     * uniqueness of the Deck.
     *
     * @param card The compared card object
     * @return int
     */
    @Override
    public int compareTo(Card card)
    {
        if (this.getValue() == card.getValue()) {
            if (this.getSuit().getRank() == card.getSuit().getRank()) {
                return (int) Math.signum(this.getSymbol().getRank() - card.getSymbol().getRank());
            } else {
                return (int) Math.signum(this.getSuit().getRank() - card.getSuit().getRank());
            }
        }

        return (int) Math.signum(this.getValue() - card.getValue());
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof Card && ((Card) object).compareTo(this) == 0;
    }

    public String getImageName()
    {
        StringBuilder url = new StringBuilder();

        switch (symbol) {
            case NUMBER:
                url.append(value);
                break;
            case JACK:
            case KING:
            case QUEEN:
                url.append(symbol.name().toLowerCase());
                break;
            default:
                throw new UnsupportedOperationException();
        }

        url.append("_of_");
        url.append(symbol.name());
        url.append(".png");

        return url.toString();
    }
}
