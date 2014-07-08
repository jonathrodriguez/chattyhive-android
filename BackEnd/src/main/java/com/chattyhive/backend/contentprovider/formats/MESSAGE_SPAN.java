package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

   

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class MESSAGE_SPAN extends Format {
	String START_MESSAGE_ID;
    String START_POSITION;
    Integer COUNT;
    

    public MESSAGE_SPAN() {
        super();
    }

    public MESSAGE_SPAN(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.START_MESSAGE_ID != null) && (!this.START_MESSAGE_ID.isEmpty()))
            jsonObject.addProperty("START_MESSAGE_ID",this.START_MESSAGE_ID);
            
        if ((this.START_POSITION != null) && (!this.START_POSITION.isEmpty()))
            jsonObject.addProperty("START_POSITION",this.START_POSITION);
            
        if (this.COUNT != null)
            jsonObject.addProperty("COUNT",this.COUNT);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_SPAN",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_SPAN");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_SPAN object.");
        }

        JsonElement property;

	    property = object.get("START_MESSAGE_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.START_MESSAGE_ID = property.getAsString();
            
        property = object.get("START_POSITION");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.START_POSITION = property.getAsString();
            
        property = object.get("COUNT");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.COUNT = property.getAsInt();
            
      
    }
}