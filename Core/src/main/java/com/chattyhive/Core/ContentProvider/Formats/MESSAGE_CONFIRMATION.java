package com.chattyhive.Core.ContentProvider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class MESSAGE_CONFIRMATION extends Format {
	public ArrayList<CHANNEL_MESSAGE_CONFIRMATION> CHANNEL_LIST;
    

    public MESSAGE_CONFIRMATION() {
        super();
    }

    public MESSAGE_CONFIRMATION(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.CHANNEL_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (CHANNEL_MESSAGE_CONFIRMATION element : this.CHANNEL_LIST) {
                JsonElement jsonElement =   element.toJSON()
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("CHANNEL_LIST",jsonArray);
            else
                jsonObject.add("CHANNEL_LIST", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("CHANNEL_LIST", JsonNull.INSTANCE);        
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_CONFIRMATION",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_CONFIRMATION");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_CONFIRMATION object.");
        }

        JsonElement property;

	    property = object.get("CHANNEL_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.CHANNEL_LIST = new ArrayList<CHANNEL_MESSAGE_CONFIRMATION>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.CHANNEL_LIST.add(  new CHANNEL_MESSAGE_CONFIRMATION(jsonElement)
      );
        }
        
      
    }
}