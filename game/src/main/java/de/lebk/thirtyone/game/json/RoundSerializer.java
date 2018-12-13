package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;

import java.lang.reflect.Type;

public class RoundSerializer implements JsonSerializer<Round>
{

    @Override
    public JsonElement serialize(Round round, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject result = new JsonObject();
        JsonArray players = new JsonArray();

        for (Player p : round.getPlayers()) {
            JsonObject playerObject = new JsonObject();

            playerObject.add("uuid", new JsonPrimitive(p.getUuid().toString()));
            playerObject.add("deck", p.getDeck().toJson());
            playerObject.add("lifes", new JsonPrimitive(p.getLifes()));

            players.add(playerObject);
        }

        result.add("players", players);
        result.add("middle", new Gson().toJsonTree(round.getMiddle()));
        result.add("started", new JsonPrimitive(round.isStarted()));

        return result;
    }
}
