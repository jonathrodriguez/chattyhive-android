package com.chattyhive.backend.contentprovider.formats;

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
public class HIVE_LIST extends Format {
	public ArrayList<HIVE> LIST;
    

    public HIVE_LIST() {
        super();
    }

    public HIVE_LIST(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.LIST != null) {
            JsonArray jsonArray = new JsonArray();
            for (HIVE element : this.LIST) {
                JsonElement jsonElement =   element.toJSON()
  ;
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
        result.add("HIVE_LIST",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("HIVE_LIST");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an HIVE_LIST object.");
        }

        JsonElement property;

	    property = object.get("LIST");
        if ((property != null) && (property.isJsonArray())) {
            this.LIST = new ArrayList<HIVE>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.LIST.add(  new HIVE(jsonElement)
      );
        }
        
      
    }
}