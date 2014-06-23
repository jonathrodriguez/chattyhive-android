package com.chattyhive.backend.contentprovider.formats.response;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Jonathan on 23/06/2014.
 */
public class AnonymousChatInfoResponseFormat extends Format {

    String PUSHER_CHANNEL;
    List<AnonymousUserResponseFormat> MEMBERS;
    String NAME;
    String NAME_URL;
    String DESCRIPTION;
    Date CREATION_DATE;

    public AnonymousChatInfoResponseFormat() {
        super();
    }

    public AnonymousChatInfoResponseFormat(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if ((this.PUSHER_CHANNEL != null) && (!this.PUSHER_CHANNEL.isEmpty()))
            jsonObject.addProperty("PUSHER_CHANNEL",this.PUSHER_CHANNEL);

        if ((this.NAME != null) && (!this.NAME.isEmpty()))
            jsonObject.addProperty("NAME",this.NAME);

        if ((this.NAME_URL != null) && (!this.NAME_URL.isEmpty()))
            jsonObject.addProperty("NAME_URL",this.NAME_URL);

        if ((this.DESCRIPTION != null) && (!this.DESCRIPTION.isEmpty()))
            jsonObject.addProperty("DESCRIPTION",this.DESCRIPTION);

        if ((this.CREATION_DATE != null) && (!TimestampFormatter.toString(this.CREATION_DATE).isEmpty()))
            jsonObject.addProperty("CREATION_DATE", TimestampFormatter.toString(this.CREATION_DATE));

        if (this.MEMBERS != null) {
            JsonArray jsonArray = new JsonArray();
            for (AnonymousUserResponseFormat element : this.MEMBERS) {
                JsonElement jsonElement = element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("MEMBERS",jsonArray);
        }

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("ANONYMOUS_CHAT_INFO",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("ANONYMOUS_CHAT_INFO");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an ANONYMOUS_CHAT_INFO object.");
        }

        JsonElement property;

        property = object.get("PUSHER_CHANNEL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUSHER_CHANNEL = property.getAsString();

        property = object.get("NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME = property.getAsString();

        property = object.get("NAME_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME_URL = property.getAsString();

        property = object.get("DESCRIPTION");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.DESCRIPTION = property.getAsString();

        property = object.get("CREATION_DATE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CREATION_DATE = TimestampFormatter.toDate(property.getAsString());

        property = object.get("MEMBERS");
        if ((property != null) && (property.isJsonArray())) {
            this.MEMBERS = new ArrayList<AnonymousUserResponseFormat>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.MEMBERS.add(new AnonymousUserResponseFormat(jsonElement));
        }
    }


}
