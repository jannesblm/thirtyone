package de.lebk.thirtyone.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.item.Card;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.json.JsonSerializable;
import de.lebk.thirtyone.game.json.RoundSerializer;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class Round extends JsonSerializable
{
    protected Set<Player> players;
    protected Deck middle;
    protected boolean started;

    public Round()
    {
        players = Collections.synchronizedSet(new HashSet<>());
    }

    public Optional<Player> getPlayer(Player player)
    {
        return players.stream().filter(p -> p.equals(player)).findFirst();
    }

    public int playerCount()
    {
        return players.size();
    }

    public void swap(Player p, Card card, Card middleCard) throws GameException
    {
        Optional<Player> player = getPlayer(p);

        if (!player.isPresent()) {
            throw new GameException("Swapping player was not found in this round!");
        }

        Deck deck = player.get().getDeck();

        deck.swap(card, middleCard);
        middle.swap(middleCard, card);
    }

    public Set<Player> getPlayers()
    {
        return players;
    }

    public JsonElement toJson()
    {
        return new GsonBuilder().registerTypeAdapter(this.getClass(), new RoundSerializer()).create().toJsonTree(this);
    }

    public Deck getMiddle()
    {
        return middle;
    }

    public boolean isStarted()
    {
        return started;
    }
}
