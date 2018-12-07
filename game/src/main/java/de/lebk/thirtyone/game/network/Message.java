package de.lebk.thirtyone.game.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.util.Optional;

public class Message
{
    private String command;
    private JsonElement json;

    /**
     * Creates an empty message
     */
    public Message()
    {
        command = "";
        json = new JsonObject();
    }

    private Message(String command, JsonElement json)
    {
        this.command = command;
        this.json = json;
    }

    public static Optional<Message> parse(String data)
    {
        String[] tokens = data.split(" ");

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
}
