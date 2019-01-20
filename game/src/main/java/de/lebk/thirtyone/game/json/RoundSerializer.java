package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;

public class RoundSerializer implements JsonSerializer<Round>
{
    private static final Logger LOG = LogManager.getLogger();

    @Override
    public JsonElement serialize(Round round, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject result = new JsonObject();
        JsonArray players = new JsonArray();

        for (Player player : round.getPlayers()) {
            JsonObject playerObject = new JsonObject();

            playerObject.add("uuid", new JsonPrimitive(player.getUuid().toString()));
            playerObject.add("deck", player.getDeck().toJson());
            playerObject.add("passed", new JsonPrimitive(player.isPassed()));
            playerObject.add("lifes", new JsonPrimitive(player.getLifes()));

            players.add(playerObject);
        }

        result.add("players", players);

        JsonObject currentPlayerObject = new JsonObject();

        if (round.getCurrentPlayer().isPresent()) {
            currentPlayerObject.add("uuid",
                    new JsonPrimitive(round.getCurrentPlayer().get().getUuid().toString()));
        }

        result.add("currentPlayer", currentPlayerObject);

        result.add("middle", round.getMiddle().toJson());
        result.add("started", new JsonPrimitive(round.isStarted()));

        return result;
    }
}
