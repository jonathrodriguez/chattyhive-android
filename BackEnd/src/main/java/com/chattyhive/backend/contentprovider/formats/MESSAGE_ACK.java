package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

 import java.util.Date;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 18/08/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class MESSAGE_ACK extends Format {
	public String ID;
    public Date SERVER_TIMESTAMP;
    

    public MESSAGE_ACK() {
        super();
    }

    public MESSAGE_ACK(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.ID != null) && (!this.ID.isEmpty()))
            jsonObject.addProperty("ID",this.ID);
            
        if ((this.SERVER_TIMESTAMP != null) && (!TimestampFormatter.toString(this.SERVER_TIMESTAMP).isEmpty()))
            jsonObject.addProperty("SERVER_TIMESTAMP", TimestampFormatter.toString(this.SERVER_TIMESTAMP));
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE_ACK",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE_ACK");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE_ACK object.");
        }

        JsonElement property;

	    property = object.get("ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.ID = property.getAsString();
            
        property = object.get("SERVER_TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SERVER_TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
      
    }
}