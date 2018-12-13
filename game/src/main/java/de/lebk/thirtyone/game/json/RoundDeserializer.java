package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Deck;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RoundDeserializer implements JsonDeserializer<Round>
{
    @Override
    public Round deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject roundObject = jsonElement.getAsJsonObject();
        Round round = new Round();

        if (roundObject.has("middle")) {
            round.setMiddle(new Gson().fromJson(roundObject.get("middle"), Deck.class));
        }

        if (roundObject.has("started")) {
            round.setStarted(roundObject.get("started").getAsBoolean());
        }

        if (roundObject.has("players")) {
            Set<Player> players = new HashSet<>();

            for (JsonElement playerElement : roundObject.get("players").getAsJsonArray()) {
                JsonObject playerObject = playerElement.getAsJsonObject();
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

                players.add(player);
            }

            round.setPlayers(players);
        }

        return round;
    }
}
