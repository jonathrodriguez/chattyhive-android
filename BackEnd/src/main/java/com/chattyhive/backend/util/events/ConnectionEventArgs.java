package com.chattyhive.backend.util.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by Jonathan on 11/04/2014.
 */
public class ConnectionEventArgs extends EventArgs {
    private JsonElement profile, hivesSubscribed;

    public ConnectionEventArgs() { super(); }
    public ConnectionEventArgs(JsonElement profile, JsonElement hivesSubscribed) {
        super();
        this.profile = profile;
        this.hivesSubscribed = hivesSubscribed;
    }
    public ConnectionEventArgs(JsonElement unparsedJson) {
        super();
        if (unparsedJson.isJsonObject()) {
            JsonObject unparsedJsonObject = unparsedJson.getAsJsonObject();
            this.profile = unparsedJsonObject.get("profile");
            this.hivesSubscribed = unparsedJsonObject.get("hives_subscribed");
        }
    }

    public JsonElement getProfile() { return this.profile; }
    public JsonElement getHivesSubscribed() {return this.hivesSubscribed;}
}
