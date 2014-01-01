package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by Jonathan on 11/12/13.
 */
public class User {
    String _username;

    public User (String username) {
        this._username = username;
    }

    public User (JsonElement jsonUser) {
        this.fromJson(jsonUser);
    }

    public String getUsername() {
        return this._username;
    }

    public void setUsername(String username) {
        this._username = username;
    }

    public JsonElement toJson() {
        return new JsonPrimitive(this._username);
    }
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            this._username = json.getAsString();
        } else {
            this._username = "";
        }
    }
}
