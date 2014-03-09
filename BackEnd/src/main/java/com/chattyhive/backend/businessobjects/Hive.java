package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive {
    private String _name;
    private String _pusher_channel_name;

    // Setters
    public void set_name (String value) { this._name = value; }
    public void set_pusher_channel_name (String value) {this._pusher_channel_name = value; }

    // Getters
    public String get_name() { return this._name; }
    public String get_pusher_channel_name() { return this._pusher_channel_name; }

    // Constructor
    public Hive(String name, String pusher_channel_name) {
        this._name = name;
        this._pusher_channel_name = pusher_channel_name;
    }

    public Hive(JsonElement json) {
        this.fromJSON(json);
    }

    public JsonElement toJSON() {
        JsonObject jsonHive = new JsonObject();
        jsonHive.addProperty("name", this._name);
        jsonHive.addProperty("pusher_channel", this._pusher_channel_name);
        return jsonHive;
    }

    public void fromJSON(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jsonHive = json.getAsJsonObject(); 
            this._name = jsonHive.get("name").getAsString();
            this._pusher_channel_name = jsonHive.get("pusher_channel").getAsString();
        } else {
            this._name = null;
            this._pusher_channel_name = null;
        }
    }
}
