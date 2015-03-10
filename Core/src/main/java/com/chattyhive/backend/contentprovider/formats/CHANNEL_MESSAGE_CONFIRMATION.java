package com.chattyhive.backend.ContentProvider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

 import java.util.ArrayList;
     

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class CHANNEL_MESSAGE_CONFIRMATION extends Format {
	public String CHANNEL;
    public ArrayList<Integer> MESSAGE_ID_LIST;
    public String LAST_CONFIRMED_MESSAGE_ID;
    

    public CHANNEL_MESSAGE_CONFIRMATION() {
        super();
    }

    public CHANNEL_MESSAGE_CONFIRMATION(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.CHANNEL != null) && (!this.CHANNEL.isEmpty()))
            jsonObject.addProperty("CHANNEL",this.CHANNEL);
      else
            jsonObject.add("CHANNEL", JsonNull.INSTANCE);            
        if (this.MESSAGE_ID_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (Integer element : this.MESSAGE_ID_LIST) {
                JsonElement jsonElement =   new JsonPrimitive(element)
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("MESSAGE_ID_LIST",jsonArray);
            else
                jsonObject.add("MESSAGE_ID_LIST", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("MESSAGE_ID_LIST", JsonNull.INSTANCE);        
        if ((this.LAST_CONFIRMED_MESSAGE_ID != null) && (!this.LAST_CONFIRMED_MESSAGE_ID.isEmpty()))
            jsonObject.addProperty("LAST_CONFIRMED_MESSAGE_ID",this.LAST_CONFIRMED_MESSAGE_ID);
      else
            jsonObject.add("LAST_CONFIRMED_MESSAGE_ID", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("CHANNEL_MESSAGE_CONFIRMATION",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("CHANNEL_MESSAGE_CONFIRMATION");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an CHANNEL_MESSAGE_CONFIRMATION object.");
        }

        JsonElement property;

	    property = object.get("CHANNEL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL = property.getAsString();
            
        property = object.get("MESSAGE_ID_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.MESSAGE_ID_LIST = new ArrayList<Integer>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.MESSAGE_ID_LIST.add(  jsonElement.getAsInt()
      );
        }
        
        property = object.get("LAST_CONFIRMED_MESSAGE_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.LAST_CONFIRMED_MESSAGE_ID = property.getAsString();
            
      
    }
}