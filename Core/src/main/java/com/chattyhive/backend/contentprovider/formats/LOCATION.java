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
public class LOCATION extends Format {
	public String ID;
    public String NAME;
    

    public LOCATION() {
        super();
    }

    public LOCATION(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.ID != null) && (!this.ID.isEmpty()))
            jsonObject.addProperty("ID",this.ID);
      else
            jsonObject.add("ID", JsonNull.INSTANCE);            
        if ((this.NAME != null) && (!this.NAME.isEmpty()))
            jsonObject.addProperty("NAME",this.NAME);
      else
            jsonObject.add("NAME", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("LOCATION",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("LOCATION");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an LOCATION object.");
        }

        JsonElement property;

	    property = object.get("ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.ID = property.getAsString();
            
        property = object.get("NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME = property.getAsString();
            
      
    }
}