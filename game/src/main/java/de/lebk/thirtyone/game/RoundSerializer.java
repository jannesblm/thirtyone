package de.lebk.thirtyone.game;

import com.google.gson.*;

import java.lang.reflect.Type;

public class RoundSerializer implements JsonSerializer<Round>
{

    @Override
    public JsonElement serialize(Round round, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject result = new JsonObject();
        JsonArray players = new JsonArray();

        for (Player p : round.getPlayers()) {
            players.add(p.toJson());
        }

        result.add("players", players);
        result.add("middle", new Gson().toJsonTree(round.middle));
        result.add("started", new JsonPrimitive(round.started));

        return result;
    }
}
