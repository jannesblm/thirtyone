package de.lebk.thirtyone.game.network;

import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.network.exception.ConnectError;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class NetworkPlayer extends Player
{
    private boolean joined;
    private Channel channel;

    public NetworkPlayer(NetworkRound round)
    {
        super(UUID.randomUUID(), round, new Deck(3), DEFAULT_LIFE_COUNT);

        joined = false;
    }

    public boolean isJoined()
    {
        return joined;
    }

    public void join(Channel channel) throws ConnectError
    {
        this.channel = channel;
        getRound().join(this);

        channel.writeAndFlush(Message.prepare("HELLO"));

        joined = true;
    }

    public void leave()
    {
        this.channel = null;
        getRound().leave(this);
        joined = false;
    }

    public NetworkRound getRound()
    {
        return (NetworkRound) round;
    }

    public void send(Message message)
    {
        getChannel().ifPresent(ch -> ch.writeAndFlush(message.toByteBuf()));
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

    public Optional<Channel> getChannel()
    {
        if (channel != null && channel.isWritable()) {
            return Optional.of(channel);
        }

        return Optional.empty();
    }
}
