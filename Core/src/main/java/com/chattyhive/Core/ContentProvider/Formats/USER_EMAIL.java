package com.chattyhive.Core.ContentProvider.Formats;

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
public class USER_EMAIL extends Format {
	public String EMAIL_USER_PART;
    public String EMAIL_SERVER_PART;
    

    public USER_EMAIL() {
        super();
    }

    public USER_EMAIL(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.EMAIL_USER_PART != null) && (!this.EMAIL_USER_PART.isEmpty()))
            jsonObject.addProperty("EMAIL_USER_PART",this.EMAIL_USER_PART);
      else
            jsonObject.add("EMAIL_USER_PART", JsonNull.INSTANCE);            
        if ((this.EMAIL_SERVER_PART != null) && (!this.EMAIL_SERVER_PART.isEmpty()))
            jsonObject.addProperty("EMAIL_SERVER_PART",this.EMAIL_SERVER_PART);
      else
            jsonObject.add("EMAIL_SERVER_PART", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("USER_EMAIL",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("USER_EMAIL");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an USER_EMAIL object.");
        }

        JsonElement property;

	    property = object.get("EMAIL_USER_PART");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.EMAIL_USER_PART = property.getAsString();
            
        property = object.get("EMAIL_SERVER_PART");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.EMAIL_SERVER_PART = property.getAsString();
            
      
    }
}