package de.lebk.thirtyone.game.json;

import com.google.gson.*;
import de.lebk.thirtyone.game.item.Deck;

import java.lang.reflect.Type;

public class DeckSerializer implements JsonSerializer<Deck>
{
    @Override
    public JsonElement serialize(Deck deck, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();

        result.add("cards", new Gson().toJsonTree(deck.getCards()));
        result.add("limit", new JsonPrimitive(deck.getLimit()));

        return result;
    }
}
