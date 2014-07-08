package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import com.chattyhive.backend.contentprovider.formats.MESSAGE;
    import com.chattyhive.backend.contentprovider.formats.MESSAGE_COUNT;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class LAST_MESSAGE extends Format {
	MESSAGE LAST_MESSAGE;
    MESSAGE_COUNT NUMBER_MESSAGES;
    

    public LAST_MESSAGE() {
        super();
    }

    public LAST_MESSAGE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.LAST_MESSAGE != null) {
            JsonElement jsonElement = this.LAST_MESSAGE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("LAST_MESSAGE",jsonElement);
        }
        
        if (this.NUMBER_MESSAGES != null) {
            JsonElement jsonElement = this.NUMBER_MESSAGES.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("NUMBER_MESSAGES",jsonElement);
        }
        
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("LAST_MESSAGE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("LAST_MESSAGE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an LAST_MESSAGE object.");
        }

        JsonElement property;

	    property = object.get("LAST_MESSAGE");
        if ((property != null) && (property.isJsonObject())) {
            this.LAST_MESSAGE = new MESSAGE(property);
        }
        
        property = object.get("NUMBER_MESSAGES");
        if ((property != null) && (property.isJsonObject())) {
            this.NUMBER_MESSAGES = new MESSAGE_COUNT(property);
        }
        
      
    }
}