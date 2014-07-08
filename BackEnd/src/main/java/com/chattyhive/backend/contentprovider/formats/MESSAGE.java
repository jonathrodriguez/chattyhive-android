package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import java.util.Date;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 30/06/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class MESSAGE extends Format {
     public String ID;
     public PROFILE_ID PROFILE;
     public Date SERVER_TIMESTAMP;
     public String CHANNEL_UNICODE;
     public Boolean CONFIRMED;
     public MESSAGE_CONTENT CONTENT;
     public Date TIMESTAMP;
    

    public MESSAGE() {
        super();
    }

    public MESSAGE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.ID != null) && (!this.ID.isEmpty()))
            jsonObject.addProperty("ID",this.ID);
            
        if (this.PROFILE != null) {
            JsonElement jsonElement = this.PROFILE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("PROFILE",jsonElement);
        }
        
        if ((this.SERVER_TIMESTAMP != null) && (!TimestampFormatter.toString(this.SERVER_TIMESTAMP).isEmpty()))
            jsonObject.addProperty("SERVER_TIMESTAMP", TimestampFormatter.toString(this.SERVER_TIMESTAMP));
            
        if ((this.CHANNEL_UNICODE != null) && (!this.CHANNEL_UNICODE.isEmpty()))
            jsonObject.addProperty("CHANNEL_UNICODE",this.CHANNEL_UNICODE);
            
        if (this.CONFIRMED != null)
            jsonObject.addProperty("CONFIRMED",this.CONFIRMED);
            
        if (this.CONTENT != null) {
            JsonElement jsonElement = this.CONTENT.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("CONTENT",jsonElement);
        }
        
        if ((this.TIMESTAMP != null) && (!TimestampFormatter.toString(this.TIMESTAMP).isEmpty()))
            jsonObject.addProperty("TIMESTAMP", TimestampFormatter.toString(this.TIMESTAMP));
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("MESSAGE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("MESSAGE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an MESSAGE object.");
        }

        JsonElement property;

	    property = object.get("ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.ID = property.getAsString();
            
        property = object.get("PROFILE");
        if ((property != null) && (property.isJsonObject())) {
            this.PROFILE = new PROFILE_ID(property);
        }
        
        property = object.get("SERVER_TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SERVER_TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("CHANNEL_UNICODE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL_UNICODE = property.getAsString();
            
        property = object.get("CONFIRMED");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.CONFIRMED = property.getAsBoolean();
            
        property = object.get("CONTENT");
        if ((property != null) && (property.isJsonObject())) {
            this.CONTENT = new MESSAGE_CONTENT(property);
        }
        
        property = object.get("TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
      
    }
}