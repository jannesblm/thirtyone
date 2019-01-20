package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;

import java.lang.reflect.Type;

public class PlayerSerializer implements JsonSerializer<Player>
{
    @Override
    public JsonElement serialize(Player player, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject result = new JsonObject();

        result.add("uuid", new JsonPrimitive(player.getUuid().toString()));
        result.add("deck", player.getDeck().toJson());
        result.add("round", player.getRound().toJson());
        result.add("passed", new JsonPrimitive(player.isPassed()));
        result.add("lifes", new JsonPrimitive(player.getLifes()));

        return result;
    }
}
