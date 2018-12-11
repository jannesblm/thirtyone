package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.network.exception.ConnectError;

public class NetworkRound extends Round
{
    protected static final int MAX_PLAYERS = 4;

    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("Round size limit reached!");
        }

        players.add(player);
    }

    public void leave(NetworkPlayer player)
    {
        players.remove(player);
    }
}
