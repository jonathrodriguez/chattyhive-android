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
public class PROFILE_ID extends Format {
	public String USER_ID;
    public String PROFILE_TYPE;
    

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
      else
            jsonObject.add("USER_ID", JsonNull.INSTANCE);            
        if ((this.PROFILE_TYPE != null) && (!this.PROFILE_TYPE.isEmpty()))
            jsonObject.addProperty("PROFILE_TYPE",this.PROFILE_TYPE);
      else
            jsonObject.add("PROFILE_TYPE", JsonNull.INSTANCE);            
      

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
            
        property = object.get("PROFILE_TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PROFILE_TYPE = property.getAsString();
            
      
    }
}