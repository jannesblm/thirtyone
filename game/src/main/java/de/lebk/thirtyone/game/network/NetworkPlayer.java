package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.network.exception.ConnectError;

import java.util.UUID;

public class NetworkPlayer extends Player
{
    private boolean joined;

    public NetworkPlayer(NetworkRound round)
    {
        super(UUID.randomUUID(), round, new Deck(3), DEFAULT_LIFE_COUNT);

        joined = false;
    }

    public boolean isJoined()
    {
        return joined;
    }

    public void join() throws ConnectError
    {
        getCurrentRound().join(this);
        joined = true;
    }

    public void leave()
    {
        getCurrentRound().leave(this);
        joined = false;
    }

    public NetworkRound getCurrentRound()
    {
        return (NetworkRound) round;
    }

}
