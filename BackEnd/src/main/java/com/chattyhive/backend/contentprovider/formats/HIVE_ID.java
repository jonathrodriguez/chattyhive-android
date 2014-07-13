package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

 

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 13/07/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class HIVE_ID extends Format {
	public String NAME_URL;
    

    public HIVE_ID() {
        super();
    }

    public HIVE_ID(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.NAME_URL != null) && (!this.NAME_URL.isEmpty()))
            jsonObject.addProperty("NAME_URL",this.NAME_URL);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("HIVE_ID",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("HIVE_ID");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an HIVE_ID object.");
        }

        JsonElement property;

	    property = object.get("NAME_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME_URL = property.getAsString();
            
      
    }
}