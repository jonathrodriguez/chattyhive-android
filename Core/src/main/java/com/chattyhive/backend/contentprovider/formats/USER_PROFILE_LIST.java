package com.chattyhive.backend.ContentProvider.formats;

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
public class USER_PROFILE_LIST extends Format {
	public ArrayList<USER_PROFILE> LIST;
    

    public USER_PROFILE_LIST() {
        super();
    }

    public USER_PROFILE_LIST(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (USER_PROFILE element : this.LIST) {
                JsonElement jsonElement = element.toJSON();
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("LIST",jsonArray);
            else
                jsonObject.add("LIST", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("LIST", JsonNull.INSTANCE);        
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("USER_PROFILE_LIST",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("USER_PROFILE_LIST");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an USER_PROFILE_LIST object.");
        }

        JsonElement property;

	    property = object.get("LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.LIST = new ArrayList<USER_PROFILE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.LIST.add(new USER_PROFILE(jsonElement));
        }
    }
}