package com.chattyhive.Core.ContentProvider.formats;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

/**
 * Created by Jonathan on 05/02/2015.
 */
public class HIVE_USERS_FILTER extends Format {
    public String TYPE;
    public INTERVAL RESULT_INTERVAL;


    public HIVE_USERS_FILTER() {
        super();
    }

    public HIVE_USERS_FILTER(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if ((this.TYPE != null) && (!this.TYPE.isEmpty()))
            jsonObject.addProperty("TYPE",this.TYPE);
        else
            jsonObject.add("TYPE", JsonNull.INSTANCE);

        if (this.RESULT_INTERVAL != null) {
            JsonElement jsonElement = this.RESULT_INTERVAL.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("RESULT_INTERVAL",jsonElement);
        }
        else
            jsonObject.add("RESULT_INTERVAL", JsonNull.INSTANCE);


        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("EXPLORE_FILTER",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("EXPLORE_FILTER");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an EXPLORE_FILTER object.");
        }

        JsonElement property;

        property = object.get("TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.TYPE = property.getAsString();

        property = object.get("RESULT_INTERVAL");
        if ((property != null) && (property.isJsonObject())) {
            this.RESULT_INTERVAL = new INTERVAL(property);
        }
    }
}