package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.Player;
import de.lebk.thirtyone.game.Round;
import de.lebk.thirtyone.game.item.Deck;

import java.lang.reflect.Type;
import java.util.UUID;

public class PlayerDeserializer implements JsonDeserializer<Player> {


    @Override
    public Player deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject playerObject = jsonElement.getAsJsonObject();

        if (! playerObject.has("uuid")) {
            throw new JsonParseException("UUID member not present");
        }


    }
}
