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
public class EXPLORE_FILTER extends Format {
	public String TYPE;
    public String CATEGORY;
    public INTERVAL RESULT_INTERVAL;
    public String SEARCH_PATTERN;
    

    public EXPLORE_FILTER() {
        super();
    }

    public EXPLORE_FILTER(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.TYPE != null) && (!this.TYPE.isEmpty()))
            jsonObject.addProperty("TYPE",this.TYPE);
      else
            jsonObject.add("TYPE", JsonNull.INSTANCE);            
        if ((this.CATEGORY != null) && (!this.CATEGORY.isEmpty()))
            jsonObject.addProperty("CATEGORY",this.CATEGORY);
      else
            jsonObject.add("CATEGORY", JsonNull.INSTANCE);            
        if (this.RESULT_INTERVAL != null) {
            JsonElement jsonElement = this.RESULT_INTERVAL.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("RESULT_INTERVAL",jsonElement);
        }
      else
            jsonObject.add("RESULT_INTERVAL", JsonNull.INSTANCE);        
        if ((this.SEARCH_PATTERN != null) && (!this.SEARCH_PATTERN.isEmpty()))
            jsonObject.addProperty("SEARCH_PATTERN",this.SEARCH_PATTERN);
      else
            jsonObject.add("SEARCH_PATTERN", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("EXPLORE_FILTER",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("EXPLORE_FILTER");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an EXPLORE_FILTER object.");
        }

        JsonElement property;

	    property = object.get("TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.TYPE = property.getAsString();
            
        property = object.get("CATEGORY");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CATEGORY = property.getAsString();
            
        property = object.get("RESULT_INTERVAL");
        if ((property != null) && (property.isJsonObject())) {
            this.RESULT_INTERVAL = new INTERVAL(property);
        }
        
        property = object.get("SEARCH_PATTERN");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SEARCH_PATTERN = property.getAsString();
            
      
    }
}