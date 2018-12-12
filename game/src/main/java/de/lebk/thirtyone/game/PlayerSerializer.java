package de.lebk.thirtyone.game;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PlayerSerializer implements JsonSerializer<Player>
{
    @Override
    public JsonElement serialize(Player player, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject result = new JsonObject();

        result.add("uuid", new JsonPrimitive(player.getUuid().toString()));
        result.add("deck", new Gson().toJsonTree(player.getDeck()));
        /*result.add("round", player.getRound().toJson());*/
        result.add("lifes", new JsonPrimitive(player.getLifes()));

        return result;
    }
}
