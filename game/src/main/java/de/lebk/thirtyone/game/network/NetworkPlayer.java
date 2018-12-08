package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;

import java.util.UUID;

public class NetworkPlayer extends Player
{
    private UUID uuid;
    private boolean connected;
    private boolean isLeader;

    public NetworkPlayer()
    {
        uuid = UUID.randomUUID();
        connected = false;
        isLeader = false;
    }

    public boolean isConnected()
    {
        return connected;
    }

    void setLeader(boolean status)
    {
        isLeader = status;
    }

    void setConnected()
    {
        this.connected = true;
    }

    public UUID getUuid()
    {
        return uuid;
    }
}
