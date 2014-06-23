package com.chattyhive.backend.contentprovider.formats.response;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Created by Jonathan on 23/06/2014.
 */
public class AnonymousUserResponseFormat extends Format {

    String PUBLIC_NAME;

    public AnonymousUserResponseFormat() {
        super();
    }

    public AnonymousUserResponseFormat(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if ((this.PUBLIC_NAME != null) && (!this.PUBLIC_NAME.isEmpty()))
            jsonObject.addProperty("PUBLIC_NAME",this.PUBLIC_NAME);

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("ANONYMOUS_USER",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("ANONYMOUS_USER");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an ANONYMOUS_USER object.");
        }

        JsonElement property;

        property = object.get("PUBLIC_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUBLIC_NAME = property.getAsString();
    }
}
