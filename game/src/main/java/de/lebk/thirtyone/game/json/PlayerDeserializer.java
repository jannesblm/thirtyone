package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Deck;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerDeserializer implements JsonDeserializer<Player>
{
    private static final Logger LOG = LogManager.getLogger();

    @Override
    public Player deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject playerObject = jsonElement.getAsJsonObject();
        Player player = new Player();

        if (playerObject.has("uuid")) {
            player.setUuid(UUID.fromString(playerObject.get("uuid").getAsString()));
        }

        if (playerObject.has("deck")) {
            player.setDeck(new Gson().fromJson(playerObject.get("deck"), Deck.class));
        }

        if (playerObject.has("lifes")) {
            player.setLifes(playerObject.get("lifes").getAsInt());
        }

        if (playerObject.has("passed")) {
            player.setPassed(playerObject.get("passed").getAsBoolean());
        }

        if (playerObject.has("round")) {
            player.setRound(
                    new GsonBuilder()
                            .registerTypeAdapter(Round.class, new RoundDeserializer())
                            .create()
                            .fromJson(playerObject.get("round"), Round.class)
            );
        }

        return player;
    }
}
