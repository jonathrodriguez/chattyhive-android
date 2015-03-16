package com.chattyhive.Core.ContentProvider.formats;

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
public class COMMON extends Format {
	public String STATUS;
    public Integer ERROR;
    

    public COMMON() {
        super();
    }

    public COMMON(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.STATUS != null) && (!this.STATUS.isEmpty()))
            jsonObject.addProperty("STATUS",this.STATUS);
      else
            jsonObject.add("STATUS", JsonNull.INSTANCE);            
        if (this.ERROR != null)
            jsonObject.addProperty("ERROR",this.ERROR);
      else
            jsonObject.add("ERROR", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("COMMON",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("COMMON");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an COMMON object.");
        }

        JsonElement property;

	    property = object.get("STATUS");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.STATUS = property.getAsString();
            
        property = object.get("ERROR");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isNumber()))
            this.ERROR = property.getAsInt();
            
      
    }
}