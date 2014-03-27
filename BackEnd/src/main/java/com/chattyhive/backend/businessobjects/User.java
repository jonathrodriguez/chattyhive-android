package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    String _username;
    Boolean _me = false;
    public String _color;

    /**
     * Public constructor.
     * @param username a string with the user's name.
     */
    public User (String username) {
        this(username,false);
    }

    public User (String username, Boolean me) {
        this._username = username;
        this._me = me;
        this._color = "#".concat(Integer.toHexString(((int)Math.floor((Math.random()* Math.pow(2,24))))));
    }

    /**
     * Public constructor.
     * @param jsonUser a JSONElement which represents the user.
     */
    public User (JsonElement jsonUser) {
        this.fromJson(jsonUser);
        this._me = false;
        this._color = "#".concat(Integer.toHexString(((int)Math.floor((Math.random()* Math.pow(2,24))))));
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

    public Boolean isMe() { return this._me; }

    public void setIsMe(Boolean value) { this._me = value; }
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
