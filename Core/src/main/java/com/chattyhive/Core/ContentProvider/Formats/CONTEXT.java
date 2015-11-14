package com.chattyhive.Core.ContentProvider.Formats;

        import com.google.gson.JsonArray;
        import com.google.gson.JsonElement;
        import com.google.gson.JsonNull;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonPrimitive;

        import java.util.ArrayList;


/*
 * Automatically generated code by ChattyHive API Manager Code Generator on 03/03/2015.
 * Be careful to not modify this file since your changes will not be included in future
 * versions of this file.
 *
 * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
*/
public class CONTEXT extends Format {
    public String CHANNEL_UNICODE;
    public Integer IMAGES_COUNT;
    public Integer NEW_USERS_COUNT;
    public Integer BUZZES_COUNT;

    public ArrayList<MESSAGE> SHARED_IMAGES_LIST;
    public ArrayList<USER_PROFILE> NEW_USERS_LIST;
    public ArrayList<MESSAGE> TOP_BUZZES_LIST;


    public CONTEXT() {
        super();
    }

    public CONTEXT(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

        if ((this.CHANNEL_UNICODE != null) && (!this.CHANNEL_UNICODE.isEmpty()))
            jsonObject.addProperty("CHANNEL_UNICODE",this.CHANNEL_UNICODE);
        else
            jsonObject.add("CHANNEL_UNICODE", JsonNull.INSTANCE);

        if (this.IMAGES_COUNT != null)
            jsonObject.addProperty("IMAGES_COUNT",this.IMAGES_COUNT);
        else
            jsonObject.add("IMAGES_COUNT", JsonNull.INSTANCE);

        if (this.NEW_USERS_COUNT != null)
            jsonObject.addProperty("NEW_USERS_COUNT",this.NEW_USERS_COUNT);
        else
            jsonObject.add("NEW_USERS_COUNT", JsonNull.INSTANCE);

        if (this.BUZZES_COUNT != null)
            jsonObject.addProperty("BUZZES_COUNT",this.BUZZES_COUNT);
        else
            jsonObject.add("BUZZES_COUNT", JsonNull.INSTANCE);

        if (this.SHARED_IMAGES_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (MESSAGE element : this.SHARED_IMAGES_LIST) {
                JsonElement jsonElement =   element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("SHARED_IMAGES_LIST",jsonArray);
            else
                jsonObject.add("SHARED_IMAGES_LIST", JsonNull.INSTANCE);
        }
        else
            jsonObject.add("SHARED_IMAGES_LIST", JsonNull.INSTANCE);

        if (this.NEW_USERS_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (USER_PROFILE element : this.NEW_USERS_LIST) {
                JsonElement jsonElement =   element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("NEW_USERS_LIST",jsonArray);
            else
                jsonObject.add("NEW_USERS_LIST", JsonNull.INSTANCE);
        }
        else
            jsonObject.add("NEW_USERS_LIST", JsonNull.INSTANCE);

        if (this.TOP_BUZZES_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (MESSAGE element : this.TOP_BUZZES_LIST) {
                JsonElement jsonElement =   element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("TOP_BUZZES_LIST",jsonArray);
            else
                jsonObject.add("TOP_BUZZES_LIST", JsonNull.INSTANCE);
        }
        else
            jsonObject.add("TOP_BUZZES_LIST", JsonNull.INSTANCE);

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("CONTEXT",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("CONTEXT");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an CONTEXT object.");
        }

        JsonElement property;

        property = object.get("CHANNEL_UNICODE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL_UNICODE = property.getAsString();

        property = object.get("IMAGES_COUNT");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.IMAGES_COUNT = property.getAsInt();

        property = object.get("NEW_USERS_COUNT");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.NEW_USERS_COUNT = property.getAsInt();

        property = object.get("BUZZES_COUNT");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.BUZZES_COUNT = property.getAsInt();

        property = object.get("SHARED_IMAGES_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.SHARED_IMAGES_LIST = new ArrayList<MESSAGE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.SHARED_IMAGES_LIST.add(new MESSAGE(jsonElement));
        }

        property = object.get("NEW_USERS_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.NEW_USERS_LIST = new ArrayList<USER_PROFILE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.NEW_USERS_LIST.add(new USER_PROFILE(jsonElement));
        }

        property = object.get("TOP_BUZZES_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.TOP_BUZZES_LIST = new ArrayList<MESSAGE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.TOP_BUZZES_LIST.add(new MESSAGE(jsonElement));
        }
    }
}