package com.chattyhive.Core.ContentProvider.Formats;

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
public class LOCAL_USER_PROFILE extends Format {
	public String EMAIL;
    public String PASS;
    public ArrayList<HIVE_ID> HIVES_SUBSCRIBED;
    public BASIC_PUBLIC_PROFILE USER_BASIC_PUBLIC_PROFILE;
    public PUBLIC_PROFILE USER_PUBLIC_PROFILE;
    public BASIC_PRIVATE_PROFILE USER_BASIC_PRIVATE_PROFILE;
    public PRIVATE_PROFILE USER_PRIVATE_PROFILE;
    

    public LOCAL_USER_PROFILE() {
        super();
    }

    public LOCAL_USER_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.EMAIL != null) && (!this.EMAIL.isEmpty()))
            jsonObject.addProperty("EMAIL",this.EMAIL);
      else
            jsonObject.add("EMAIL", JsonNull.INSTANCE);            
        if ((this.PASS != null) && (!this.PASS.isEmpty()))
            jsonObject.addProperty("PASS",this.PASS);
      else
            jsonObject.add("PASS", JsonNull.INSTANCE);            
        if (this.HIVES_SUBSCRIBED != null) {
            JsonArray jsonArray = new JsonArray();
            for (HIVE_ID element : this.HIVES_SUBSCRIBED) {
                JsonElement jsonElement =   element.toJSON()
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("HIVES_SUBSCRIBED",jsonArray);
            else
                jsonObject.add("HIVES_SUBSCRIBED", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("HIVES_SUBSCRIBED", JsonNull.INSTANCE);        
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
        result.add("LOCAL_USER_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("LOCAL_USER_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an LOCAL_USER_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("EMAIL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.EMAIL = property.getAsString();
            
        property = object.get("PASS");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PASS = property.getAsString();
            
        property = object.get("HIVES_SUBSCRIBED");
        if ((property != null) && (property.isJsonArray())) {
            this.HIVES_SUBSCRIBED = new ArrayList<HIVE_ID>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.HIVES_SUBSCRIBED.add(  new HIVE_ID(jsonElement)
      );
        }
        
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