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
public class PROFILE_ID extends Format {
	public String USER_ID;
    public String PUBLIC_NAME;
    

    public PROFILE_ID() {
        super();
    }

    public PROFILE_ID(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.USER_ID != null) && (!this.USER_ID.isEmpty()))
            jsonObject.addProperty("USER_ID",this.USER_ID);
            
        if ((this.PUBLIC_NAME != null) && (!this.PUBLIC_NAME.isEmpty()))
            jsonObject.addProperty("PUBLIC_NAME",this.PUBLIC_NAME);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("PROFILE_ID",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("PROFILE_ID");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an PROFILE_ID object.");
        }

        JsonElement property;

	    property = object.get("USER_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_ID = property.getAsString();
            
        property = object.get("PUBLIC_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUBLIC_NAME = property.getAsString();
            
      
    }
}