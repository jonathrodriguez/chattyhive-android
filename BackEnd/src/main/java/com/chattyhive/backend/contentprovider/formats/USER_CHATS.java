package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class USER_CHATS extends Format {
	ArrayList<CHAT_SYNC> USER_CHAT_LIST;
    

    public USER_CHATS() {
        super();
    }

    public USER_CHATS(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.USER_CHAT_LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (CHAT_SYNC element : this.USER_CHAT_LIST) {
                JsonElement jsonElement = element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("USER_CHAT_LIST",jsonArray);
        }
        
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("USER_CHATS",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("USER_CHATS");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an USER_CHATS object.");
        }

        JsonElement property;

	    property = object.get("USER_CHAT_LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.USER_CHAT_LIST = new ArrayList<CHAT_SYNC>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.USER_CHAT_LIST.add(new CHAT_SYNC(jsonElement));
        }
        
      
    }
}