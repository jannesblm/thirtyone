package de.lebk.thirtyone.game.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

public abstract class JsonSerializable<T extends Object> {

    public JsonElement toJson()
    {
        return new Gson().toJsonTree(this);
    }

    public abstract T fromJson(String json);

    public String toString()
    {
        return this.toJson().toString();
    }
}
