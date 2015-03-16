package com.chattyhive.backend.ContentProvider.formats;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;


/*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class LOGIN extends Format {
	public String USER;
    public String PASS;
    

    public LOGIN() {
        super();
    }

    public LOGIN(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.USER != null) && (!this.USER.isEmpty()))
            jsonObject.addProperty("USER",this.USER);
      else
            jsonObject.add("USER", JsonNull.INSTANCE);            
        if ((this.PASS != null) && (!this.PASS.isEmpty()))
            jsonObject.addProperty("PASS",this.PASS);
      else
            jsonObject.add("PASS", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("LOGIN",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("LOGIN");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an LOGIN object.");
        }

        JsonElement property;

	    property = object.get("USER");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER = property.getAsString();
            
        property = object.get("PASS");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PASS = property.getAsString();
            
      
    }
}