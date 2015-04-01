package com.chattyhive.Core.ContentProvider.Formats;

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
public class MESSAGE_LIST extends Format {
	public ArrayList<MESSAGE> MESSAGES;
    public Integer NUMBER_MESSAGES;
    

    public MESSAGE_LIST() {
        super();
    }

    public MESSAGE_LIST(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.MESSAGES != null) {
            JsonArray jsonArray = new JsonArray();
            for (MESSAGE element : this.MESSAGES) {
                JsonElement jsonElement =   element.toJSON()
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("MESSAGES",jsonArray);
            else
                jsonObject.add("MESSAGES", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("MESSAGES", JsonNull.INSTANCE);        
        if (this.NUMBER_MESSAGES != null)
            jsonObject.addProperty("NUMBER_MESSAGES",this.NUMBER_MESSAGES);
      else
            jsonObject.add("NUMBER_MESSAGES", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_LIST",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_LIST");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_LIST object.");
        }

        JsonElement property;

	    property = object.get("MESSAGES");
        if ((property != null) && (property.isJsonArray())) {
            this.MESSAGES = new ArrayList<MESSAGE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.MESSAGES.add(  new MESSAGE(jsonElement)
      );
        }
        
        property = object.get("NUMBER_MESSAGES");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.NUMBER_MESSAGES = property.getAsInt();
            
      
    }
}