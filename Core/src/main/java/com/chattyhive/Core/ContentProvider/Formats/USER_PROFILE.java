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
public class USER_PROFILE extends Format {
	public BASIC_PUBLIC_PROFILE USER_BASIC_PUBLIC_PROFILE;
    public PUBLIC_PROFILE USER_PUBLIC_PROFILE;
    public BASIC_PRIVATE_PROFILE USER_BASIC_PRIVATE_PROFILE;
    public PRIVATE_PROFILE USER_PRIVATE_PROFILE;
    

    public USER_PROFILE() {
        super();
    }

    public USER_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if (this.USER_BASIC_PUBLIC_PROFILE != null) {
            JsonElement jsonElement = this.USER_BASIC_PUBLIC_PROFILE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("USER_BASIC_PUBLIC_PROFILE",jsonElement);
        }
      else
            jsonObject.add("USER_BASIC_PUBLIC_PROFILE", JsonNull.INSTANCE);        
        if (this.USER_PUBLIC_PROFILE != null) {
            JsonElement jsonElement = this.USER_PUBLIC_PROFILE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("USER_PUBLIC_PROFILE",jsonElement);
        }
      else
            jsonObject.add("USER_PUBLIC_PROFILE", JsonNull.INSTANCE);        
        if (this.USER_BASIC_PRIVATE_PROFILE != null) {
            JsonElement jsonElement = this.USER_BASIC_PRIVATE_PROFILE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("USER_BASIC_PRIVATE_PROFILE",jsonElement);
        }
      else
            jsonObject.add("USER_BASIC_PRIVATE_PROFILE", JsonNull.INSTANCE);        
        if (this.USER_PRIVATE_PROFILE != null) {
            JsonElement jsonElement = this.USER_PRIVATE_PROFILE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("USER_PRIVATE_PROFILE",jsonElement);
        }
      else
            jsonObject.add("USER_PRIVATE_PROFILE", JsonNull.INSTANCE);        
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("USER_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("USER_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an USER_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("USER_BASIC_PUBLIC_PROFILE");
        if ((property != null) && (property.isJsonObject())) {
            this.USER_BASIC_PUBLIC_PROFILE = new BASIC_PUBLIC_PROFILE(property);
        }
        
        property = object.get("USER_PUBLIC_PROFILE");
        if ((property != null) && (property.isJsonObject())) {
            this.USER_PUBLIC_PROFILE = new PUBLIC_PROFILE(property);
        }
        
        property = object.get("USER_BASIC_PRIVATE_PROFILE");
        if ((property != null) && (property.isJsonObject())) {
            this.USER_BASIC_PRIVATE_PROFILE = new BASIC_PRIVATE_PROFILE(property);
        }
        
        property = object.get("USER_PRIVATE_PROFILE");
        if ((property != null) && (property.isJsonObject())) {
            this.USER_PRIVATE_PROFILE = new PRIVATE_PROFILE(property);
        }
        
      
    }
}