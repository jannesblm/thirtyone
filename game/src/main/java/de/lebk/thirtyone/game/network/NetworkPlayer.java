package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.UUID;

public class NetworkPlayer extends Player
{
    public NetworkPlayer(NetworkRound round)
    {
        super(UUID.randomUUID(), round, new Deck(3), DEFAULT_LIFE_COUNT);

        joined = false;
    }

    public void join(Channel channel) throws ConnectError
    {
        this.channel = channel;
        getRound().join(this);

        update();

        joined = true;
    }

    public void leave()
    {
        channel = null;
        getRound().leave(this);
        joined = false;
    }

    public NetworkRound getRound()
    {
        return (NetworkRound) round;
    }

    public void disconnect(String reason)
    {
        send(new Message("BYE", Map.of("reason", reason)));

        getChannel().ifPresent(ch -> {
            try {
                ch.close().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void update()
    {
        send(new Message("PLAYER", this.toJson()));
    }
}
