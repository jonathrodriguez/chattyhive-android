package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;

/**
 * Created by Jonathan on 01/08/2014.
 */
public class CHAT_LIST extends Format {
    public ArrayList<CHAT_SYNC> CHAT_SYNCS;

    public CHAT_LIST() {
        super();
    }

    public CHAT_LIST(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if (this.CHAT_SYNCS != null) {
            JsonArray jsonArray = new JsonArray();
            for (CHAT_SYNC element : this.CHAT_SYNCS) {
                JsonElement jsonElement =   element.toJSON()
                        ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("CHAT_SYNC",jsonArray);
        }

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("CHAT_LIST",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonArray array = data.getAsJsonObject().getAsJsonArray("CHAT_LIST");
/*        if ((array == null) || (!array.isJsonArray())) {
            //array = data.getAsJsonObject();
            return;
        }*/
        if ((array == null) || (!array.isJsonArray())) {
            throw new IllegalArgumentException("Data is not an CHAT_LIST object.");
        }

        JsonElement property;

        this.CHAT_SYNCS = new ArrayList<CHAT_SYNC>();
        for (JsonElement jsonElement : array)
            this.CHAT_SYNCS.add(new CHAT_SYNC(jsonElement));
    }
}
