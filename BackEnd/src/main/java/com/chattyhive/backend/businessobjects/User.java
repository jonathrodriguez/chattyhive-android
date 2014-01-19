package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    String _username;

    /**
     * Public constructor.
     * @param username a string with the user's name.
     */
    public User (String username) {
        this._username = username;
    }

    /**
     * Public constructor.
     * @param jsonUser a JSONElement which represents the user.
     */
    public User (JsonElement jsonUser) {
        this.fromJson(jsonUser);
    }

    /**
     * Returns the user's name
     * @return
     */
    public String getUsername() {
        return this._username;
    }

    /**
     * Changes the user's name
     * @param username
     */
    public void setUsername(String username) {
        this._username = username;
    }

    /**
     * Retrieves the JSON representation of this user object.
     * @return
     */
    public JsonElement toJson() {
        return new JsonPrimitive(this._username);
    }

    /**
     * Sets the data of this user object from a JSON representation.
     * @param json
     */
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            this._username = json.getAsString();
        } else {
            this._username = "";
        }
    }
}
