package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.network.exception.ConnectError;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Round
{
    private static final int MAX_PLAYERS = 4;

    private Set<Player> players;

    public Round()
    {
        players = Collections.synchronizedSet(new HashSet<>());
    }

    public int playerCount()
    {
        return players.size();
    }

    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("Round size limit reached!");
        }

        player.setConnected();

        if (players.size() == 0) {
            player.setLeader(true);
        }

        players.add(player);
    }

    public void leave(NetworkPlayer player)
    {
        players.remove(player);
    }
}
