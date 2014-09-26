package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 26/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class BASIC_PRIVATE_PROFILE extends Format {
	public String USER_ID;
    public String FIRST_NAME;
    public String LAST_NAME;
    public String IMAGE_URL;
    

    public BASIC_PRIVATE_PROFILE() {
        super();
    }

    public BASIC_PRIVATE_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.USER_ID != null) && (!this.USER_ID.isEmpty()))
            jsonObject.addProperty("USER_ID",this.USER_ID);
      else
            jsonObject.addProperty("USER_ID", JsonNull.INSTANCE);            
        if ((this.FIRST_NAME != null) && (!this.FIRST_NAME.isEmpty()))
            jsonObject.addProperty("FIRST_NAME",this.FIRST_NAME);
      else
            jsonObject.addProperty("FIRST_NAME", JsonNull.INSTANCE);            
        if ((this.LAST_NAME != null) && (!this.LAST_NAME.isEmpty()))
            jsonObject.addProperty("LAST_NAME",this.LAST_NAME);
      else
            jsonObject.addProperty("LAST_NAME", JsonNull.INSTANCE);            
        if ((this.IMAGE_URL != null) && (!this.IMAGE_URL.isEmpty()))
            jsonObject.addProperty("IMAGE_URL",this.IMAGE_URL);
      else
            jsonObject.addProperty("IMAGE_URL", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("BASIC_PRIVATE_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("BASIC_PRIVATE_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an BASIC_PRIVATE_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("USER_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_ID = property.getAsString();
            
        property = object.get("FIRST_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.FIRST_NAME = property.getAsString();
            
        property = object.get("LAST_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.LAST_NAME = property.getAsString();
            
        property = object.get("IMAGE_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.IMAGE_URL = property.getAsString();
            
      
    }
}