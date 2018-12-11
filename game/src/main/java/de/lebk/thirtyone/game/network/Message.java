package de.lebk.thirtyone.game.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Pattern;

public class Message implements Serializable
{
    private static final Pattern DELIMITER;

    static {
        DELIMITER = Pattern.compile("\u241F", Pattern.LITERAL);
    }

    private final String command;
    private final JsonElement json;

    public Message(String command)
    {
        this.command = command;
        this.json = new JsonObject();

    }

    public Message(String command, JsonElement json)
    {
        this.command = command;
        this.json = json;

    }

    public static ByteBuf prepare(String command)
    {
        return new Message(command, new JsonObject()).toByteBuf();
    }

    public static ByteBuf prepare(String command, JsonElement json)
    {
        return new Message(command, json).toByteBuf();
    }

    public static Optional<Message> parse(String data)
    {
        String[] tokens = DELIMITER.split(data, 2);

        if (tokens.length != 2) {
            return Optional.empty();
        }

        JsonObject element;

        try {
            element = new JsonParser().parse(tokens[1]).getAsJsonObject();
        } catch (JsonSyntaxException exception) {
            return Optional.empty();
        }

        return Optional.of(new Message(tokens[0].toUpperCase(), element));
    }

    public String getCommand()
    {
        return this.command;
    }

    public JsonElement getJSON()
    {
        return this.json;
    }

    public Optional<JsonElement> get(String memberName)
    {
        if (!json.isJsonObject()) {
            return Optional.empty();
        }

        JsonElement member = json.getAsJsonObject().get(memberName);

        if (member == null) {
            return Optional.empty();
        }

        return Optional.of(member);
    }

    public String toString()
    {
        return this.getCommand() + DELIMITER + this.getJSON() + System.lineSeparator();
    }

    public ByteBuf toByteBuf()
    {
        return Unpooled.copiedBuffer(this.toString(), StandardCharsets.UTF_8);
    }
}
