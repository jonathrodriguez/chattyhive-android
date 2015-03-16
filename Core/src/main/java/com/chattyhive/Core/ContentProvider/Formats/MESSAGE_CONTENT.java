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
public class MESSAGE_CONTENT extends Format {
	public String CONTENT_TYPE;
    public String CONTENT;
    

    public MESSAGE_CONTENT() {
        super();
    }

    public MESSAGE_CONTENT(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.CONTENT_TYPE != null) && (!this.CONTENT_TYPE.isEmpty()))
            jsonObject.addProperty("CONTENT_TYPE",this.CONTENT_TYPE);
      else
            jsonObject.add("CONTENT_TYPE", JsonNull.INSTANCE);            
        if ((this.CONTENT != null) && (!this.CONTENT.isEmpty()))
            jsonObject.addProperty("CONTENT",this.CONTENT);
      else
            jsonObject.add("CONTENT", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_CONTENT",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_CONTENT");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_CONTENT object.");
        }

        JsonElement property;

	    property = object.get("CONTENT_TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CONTENT_TYPE = property.getAsString();
            
        property = object.get("CONTENT");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CONTENT = property.getAsString();
            
      
    }
}