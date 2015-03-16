package com.chattyhive.Core.ContentProvider.formats;

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
public class BASIC_PUBLIC_PROFILE extends Format {
	public String USER_ID;
    public String PUBLIC_NAME;
    public String USER_COLOR;
    public String IMAGE_URL;
    public String STATUS_MESSAGE;
    

    public BASIC_PUBLIC_PROFILE() {
        super();
    }

    public BASIC_PUBLIC_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.USER_ID != null) && (!this.USER_ID.isEmpty()))
            jsonObject.addProperty("USER_ID",this.USER_ID);
      else
            jsonObject.add("USER_ID", JsonNull.INSTANCE);            
        if ((this.PUBLIC_NAME != null) && (!this.PUBLIC_NAME.isEmpty()))
            jsonObject.addProperty("PUBLIC_NAME",this.PUBLIC_NAME);
      else
            jsonObject.add("PUBLIC_NAME", JsonNull.INSTANCE);            
        if ((this.USER_COLOR != null) && (!this.USER_COLOR.isEmpty()))
            jsonObject.addProperty("USER_COLOR",this.USER_COLOR);
      else
            jsonObject.add("USER_COLOR", JsonNull.INSTANCE);            
        if ((this.IMAGE_URL != null) && (!this.IMAGE_URL.isEmpty()))
            jsonObject.addProperty("IMAGE_URL",this.IMAGE_URL);
      else
            jsonObject.add("IMAGE_URL", JsonNull.INSTANCE);
        if ((this.STATUS_MESSAGE != null) && (!this.STATUS_MESSAGE.isEmpty()))
            jsonObject.addProperty("STATUS_MESSAGE",this.STATUS_MESSAGE);
        else
            jsonObject.add("STATUS_MESSAGE", JsonNull.INSTANCE);

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("BASIC_PUBLIC_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("BASIC_PUBLIC_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an BASIC_PUBLIC_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("USER_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_ID = property.getAsString();
            
        property = object.get("PUBLIC_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUBLIC_NAME = property.getAsString();
            
        property = object.get("USER_COLOR");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_COLOR = property.getAsString();
            
        property = object.get("IMAGE_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.IMAGE_URL = property.getAsString();

        property = object.get("STATUS_MESSAGE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.STATUS_MESSAGE = property.getAsString();
    }
}