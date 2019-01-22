package de.lebk.thirtyone.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.json.JsonSerializable;
import de.lebk.thirtyone.game.json.RoundSerializer;

import java.util.*;
import java.util.stream.Collectors;

public class Round extends JsonSerializable<Round>
{
    protected Set<Player> players;
    protected Player currentPlayer;
    protected Deck middle;
    protected boolean started;

    public Round()
    {
        players = Collections.synchronizedSet(new HashSet<>());
        middle = new Deck();
    }

    public Optional<Player> getPlayer(Player player)
    {
        return players.stream().filter(p -> p.equals(player)).findFirst();
    }

    public int playerCount()
    {
        return players.size();
    }

    public Set<Player> getPlayers()
    {
        return players;
    }

    public void setPlayers(Set<Player> players)
    {
        this.players = players;
    }

    public JsonElement toJson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(this.getClass(), new RoundSerializer())
                .create()
                .toJsonTree(this);
    }

    public Deck getMiddle()
    {
        return middle;
    }

    public void setMiddle(Deck middle)
    {
        this.middle = middle;
    }

    public boolean isStarted()
    {
        return started;
    }

    public void setStarted(boolean started)
    {
        this.started = started;
    }

    public Optional<Player> getCurrentPlayer()
    {
        if (currentPlayer == null) {
            return Optional.empty();
        }

        return Optional.of(currentPlayer);
    }

    public List<Player> getWinners()
    {
        final Map<Float, List<Player>> scores = players.stream()
                .collect(Collectors.groupingBy(Player::getPoints));

        if (scores.size() == 1) {
            return new ArrayList<>();
        }

        final float maxScore = Collections.max(scores.keySet());

        return scores.get(maxScore);
    }

    public List<Player> getLosers()
    {
        final Map<Float, List<Player>> scores = players.stream()
                .collect(Collectors.groupingBy(Player::getPoints));

        final float minScore = Collections.min(scores.keySet());

        return scores.get(minScore);
    }

    public void setCurrentPlayer(Player currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }
}
