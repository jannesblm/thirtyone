package de.lebk.thirtyone.game;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.lebk.thirtyone.game.item.Deck;
import de.lebk.thirtyone.game.json.JsonSerializable;
import de.lebk.thirtyone.game.json.PlayerDeserializer;
import de.lebk.thirtyone.game.json.PlayerSerializer;
import io.netty.channel.Channel;

import java.util.Optional;
import java.util.UUID;

public class Player extends JsonSerializable<Player>
{
    public static final int DEFAULT_LIFE_COUNT = 3;

    private final UUID uuid;
    protected Channel channel;
    protected final Round round;
    protected Deck deck;
    protected int lifes;

    private Player()
    {
        uuid = UUID.randomUUID();
        round = new Round();
    }

    public Player(UUID uuid)
    {
        this.uuid = uuid;
        deck = new Deck(3);
        round = new Round();
        lifes = DEFAULT_LIFE_COUNT;
    }

    public Player(UUID uuid, Round round, Deck deck, int lifes)
    {
        this.uuid = uuid;
        this.deck = deck;
        this.lifes = lifes;
        this.round = round;
    }

    public UUID getUuid()
    {
        return uuid;
    }

    public Deck getDeck()
    {
        return deck;
    }

    public boolean equals(Object object)
    {
        return object instanceof Player && ((Player) object).uuid.equals(this.uuid);
    }

    public Optional<Channel> getChannel()
    {
        if (channel != null && channel.isWritable()) {
            return Optional.of(channel);
        }

        return Optional.empty();
    }

    public int getLifes()
    {
        return lifes;
    }

    public JsonElement toJson()
    {
        return new GsonBuilder()
                .registerTypeAdapter(this.getClass(), new PlayerSerializer())
                .create()
                .toJsonTree(this);
    }

    @Override
    public Player fromJson(String json) {
        return new GsonBuilder()
                .registerTypeAdapter(this.getClass(), new PlayerDeserializer())
                .create()
                .fromJson(json, this.getClass());
    }

    public Round getRound()
    {
        return round;
    }
}
