package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.network.exception.ConnectError;

public class NetworkRound extends Round
{
    public void join(NetworkPlayer player) throws ConnectError
    {
        if (players.size() >= MAX_PLAYERS) {
            throw new ConnectError("NetworkRound size limit reached!");
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
