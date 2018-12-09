package de.lebk.thirtyone.game;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Round
{
    protected static final int MAX_PLAYERS = 4;
    protected Set<Player> players;

    public Round()
    {
        players = Collections.synchronizedSet(new HashSet<>());
    }

    public int playerCount()
    {
        return players.size();
    }
}
