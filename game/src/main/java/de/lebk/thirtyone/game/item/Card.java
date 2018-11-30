package de.lebk.thirtyone.game.item;

public class Card implements Comparable<Card>
{
    protected static final int CARD_VALUE_MAX = 11;
    protected static final int CARD_VALUE_MIN = 7;
    
    protected int value;
    protected Suit suit;
    protected Symbol symbol;
    
    public Card(Suit suit, int value, Symbol symbol)
    {
        if (value > CARD_VALUE_MAX || value < CARD_VALUE_MIN) {
            throw new IllegalArgumentException("Card value " + value + " out of range");
        }

        this.value = value;
        this.suit = suit;
        this.symbol = symbol;
    }

    public int getValue()
    {
        return value;
    }

    public Suit getSuit()
    {
        return suit;
    }

    @Override
    public int compareTo(Card card)
    {
        if (this.getValue() == card.getValue()) {
            return (int) Math.signum(this.getSuit().getRank() - card.getSuit().getRank());
        }

        return (int) Math.signum(this.getValue() - card.getValue());
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof Card && ((Card) object).compareTo(this) == 0;
    }
    
    @Override
    public String toString()
    {
        return symbol + " (" + value + ") " + " of " + suit; 
    }
}
