package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

  

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class CHAT_ID extends Format {
	String CHANNEL_UNICODE;
    String PUSHER_CHANNEL;
    

    public CHAT_ID() {
        super();
    }

    public CHAT_ID(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.CHANNEL_UNICODE != null) && (!this.CHANNEL_UNICODE.isEmpty()))
            jsonObject.addProperty("CHANNEL_UNICODE",this.CHANNEL_UNICODE);
            
        if ((this.PUSHER_CHANNEL != null) && (!this.PUSHER_CHANNEL.isEmpty()))
            jsonObject.addProperty("PUSHER_CHANNEL",this.PUSHER_CHANNEL);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("CHAT_ID",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("CHAT_ID");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an CHAT_ID object.");
        }

        JsonElement property;

	    property = object.get("CHANNEL_UNICODE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL_UNICODE = property.getAsString();
            
        property = object.get("PUSHER_CHANNEL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUSHER_CHANNEL = property.getAsString();
            
      
    }
}