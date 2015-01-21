package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

   

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class INTERVAL extends Format {
	public String START_INDEX;
    public Integer COUNT;
    public String END_INDEX;
    

    public INTERVAL() {
        super();
    }

    public INTERVAL(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.START_INDEX != null) && (!this.START_INDEX.isEmpty()))
            jsonObject.addProperty("START_INDEX",this.START_INDEX);
      else
            jsonObject.add("START_INDEX", JsonNull.INSTANCE);            
        if (this.COUNT != null)
            jsonObject.addProperty("COUNT",this.COUNT);
      else
            jsonObject.add("COUNT", JsonNull.INSTANCE);            
        if ((this.END_INDEX != null) && (!this.END_INDEX.isEmpty()))
            jsonObject.addProperty("END_INDEX",this.END_INDEX);
      else
            jsonObject.add("END_INDEX", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("INTERVAL",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("INTERVAL");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an INTERVAL object.");
        }

        JsonElement property;

	    property = object.get("START_INDEX");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.START_INDEX = property.getAsString();
            
        property = object.get("COUNT");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.COUNT = property.getAsInt();
            
        property = object.get("END_INDEX");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.END_INDEX = property.getAsString();
            
      
    }
}