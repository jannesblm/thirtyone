package de.lebk.thirtyone.game.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class Message
{
    private String command;
    private JsonElement argument;

    /**
     * Creates an empty message
     */
    public Message()
    {
        command = "";
        argument = new JsonObject();
    }

    private Message(String command, JsonElement argument)
    {
        this.command = command;
        this.argument = argument;
    }

    public static Message parse(String data) throws MessageParseException
    {
        String[] tokens = data.split(" ");

        if (tokens.length > 2) {
            throw new MessageParseException("Message is malformed");
        }

        JsonObject element;

        try {
            element = new JsonParser().parse(tokens.length == 2 ? tokens[1] : "").getAsJsonObject();
        } catch (JsonSyntaxException exception) {
            throw new MessageParseException("Cannot parse JSON data", exception);
        }

        return new Message(tokens[0].toUpperCase(), element);
    }
}
