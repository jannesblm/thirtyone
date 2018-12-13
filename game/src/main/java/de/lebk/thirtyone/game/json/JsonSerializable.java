package de.lebk.thirtyone.game.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public abstract class JsonSerializable<T>
{
    public JsonElement toJson()
    {
        return new Gson().toJsonTree(this);
    }

    public String toString()
    {
        return this.toJson().toString();
    }
}
