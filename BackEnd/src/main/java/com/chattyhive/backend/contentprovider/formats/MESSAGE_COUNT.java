package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

 

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class MESSAGE_COUNT extends Format {
	Integer NUMBER_MESSAGES;
    

    public MESSAGE_COUNT() {
        super();
    }

    public MESSAGE_COUNT(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.NUMBER_MESSAGES != null)
            jsonObject.addProperty("NUMBER_MESSAGES",this.NUMBER_MESSAGES);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_COUNT",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_COUNT");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_COUNT object.");
        }

        JsonElement property;

	    property = object.get("NUMBER_MESSAGES");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.NUMBER_MESSAGES = property.getAsInt();
    }
}