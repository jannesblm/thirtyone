package de.lebk.thirtyone.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.json.JsonSerializable;
import de.lebk.thirtyone.game.json.PlayerDeserializer;
import de.lebk.thirtyone.game.json.PlayerSerializer;
import de.lebk.thirtyone.game.network.Message;
import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

public class Player extends JsonSerializable<Player>
{
    public static final int DEFAULT_LIFE_COUNT = 3;

    protected UUID uuid;
    protected Channel channel;
    protected Round round;
    protected Deck deck;
    protected int lifes;
    protected boolean joined;
    protected boolean passed;

    public Player()
    {
        uuid = UUID.randomUUID();
        deck = new Deck();
        lifes = DEFAULT_LIFE_COUNT;
        round = new Round();
        joined = false;
    }

    public Player(UUID uuid, Round round, Deck deck, int lifes)
    {
        this.uuid = uuid;
        this.deck = deck;
        this.lifes = lifes;
        this.round = round;
    }

    public static Player fromJson(JsonElement json)
    {
        return new GsonBuilder()
                .registerTypeAdapter(Player.class, new PlayerDeserializer())
                .create()
                .fromJson(json, Player.class);
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public boolean isJoined()
    {
        return joined;
    }

    public Deck getDeck()
    {
        return deck;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public boolean equals(Object object)
    {
        return object instanceof Player
                && ((Player) object).uuid.equals(this.uuid);
    }

    public Optional<Channel> getChannel()
    {
        if (channel != null && channel.isWritable()) {
            return Optional.of(channel);
        }

        return Optional.empty();
    }

    public void setDeck(Deck deck)
    {
        this.deck = deck;
    }

    public int getLifes()
    {
        return lifes;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public JsonElement toJson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(this.getClass(), new PlayerSerializer())
                .create()
                .toJsonTree(this);
    }

    public void setLifes(int lifes)
    {
        this.lifes = lifes;
    }

    public Round getRound()
    {
        return round;
    }

    public void setRound(Round round)
    {
        this.round = round;
    }

    public void send(Message message)
    {
        getChannel().ifPresent(ch -> ch.writeAndFlush(message.toByteBuf()));
    }

    public boolean isPassed()
    {
        return this.passed;
    }

    public void setPassed(boolean passed)
    {
        this.passed = passed;
    }
}
