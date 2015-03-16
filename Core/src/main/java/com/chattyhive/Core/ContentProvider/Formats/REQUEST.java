package com.chattyhive.Core.ContentProvider.formats;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.Date;
import com.chattyhive.Core.Util.Formatters.TimestampFormatter;


/*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class REQUEST extends Format {
	public String ID;
    public String REQUEST_TYPE;
    public String REQUEST_STATUS;
    public String REQUESTING_USER;
    public String REQUESTED_USER;
    public String CHANNEL_UNICODE;
    public String NAME_URL;
    public String REQUEST_MESSAGE;
    public Date TIMESTAMP;
    public Date SERVER_TIMESTAMP;
    public Date STATUS_TIMESTAMP;
    

    public REQUEST() {
        super();
    }

    public REQUEST(JsonElement data) {
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
        if ((this.REQUEST_TYPE != null) && (!this.REQUEST_TYPE.isEmpty()))
            jsonObject.addProperty("REQUEST_TYPE",this.REQUEST_TYPE);
      else
            jsonObject.add("REQUEST_TYPE", JsonNull.INSTANCE);            
        if ((this.REQUEST_STATUS != null) && (!this.REQUEST_STATUS.isEmpty()))
            jsonObject.addProperty("REQUEST_STATUS",this.REQUEST_STATUS);
      else
            jsonObject.add("REQUEST_STATUS", JsonNull.INSTANCE);            
        if ((this.REQUESTING_USER != null) && (!this.REQUESTING_USER.isEmpty()))
            jsonObject.addProperty("REQUESTING_USER",this.REQUESTING_USER);
      else
            jsonObject.add("REQUESTING_USER", JsonNull.INSTANCE);            
        if ((this.REQUESTED_USER != null) && (!this.REQUESTED_USER.isEmpty()))
            jsonObject.addProperty("REQUESTED_USER",this.REQUESTED_USER);
      else
            jsonObject.add("REQUESTED_USER", JsonNull.INSTANCE);            
        if ((this.CHANNEL_UNICODE != null) && (!this.CHANNEL_UNICODE.isEmpty()))
            jsonObject.addProperty("CHANNEL_UNICODE",this.CHANNEL_UNICODE);
      else
            jsonObject.add("CHANNEL_UNICODE", JsonNull.INSTANCE);            
        if ((this.NAME_URL != null) && (!this.NAME_URL.isEmpty()))
            jsonObject.addProperty("NAME_URL",this.NAME_URL);
      else
            jsonObject.add("NAME_URL", JsonNull.INSTANCE);            
        if ((this.REQUEST_MESSAGE != null) && (!this.REQUEST_MESSAGE.isEmpty()))
            jsonObject.addProperty("REQUEST_MESSAGE",this.REQUEST_MESSAGE);
      else
            jsonObject.add("REQUEST_MESSAGE", JsonNull.INSTANCE);            
        if ((this.TIMESTAMP != null) && (!TimestampFormatter.toString(this.TIMESTAMP).isEmpty()))
            jsonObject.addProperty("TIMESTAMP", TimestampFormatter.toString(this.TIMESTAMP));
      else
            jsonObject.add("TIMESTAMP", JsonNull.INSTANCE);
            
        if ((this.SERVER_TIMESTAMP != null) && (!TimestampFormatter.toString(this.SERVER_TIMESTAMP).isEmpty()))
            jsonObject.addProperty("SERVER_TIMESTAMP", TimestampFormatter.toString(this.SERVER_TIMESTAMP));
      else
            jsonObject.add("SERVER_TIMESTAMP", JsonNull.INSTANCE);
            
        if ((this.STATUS_TIMESTAMP != null) && (!TimestampFormatter.toString(this.STATUS_TIMESTAMP).isEmpty()))
            jsonObject.addProperty("STATUS_TIMESTAMP", TimestampFormatter.toString(this.STATUS_TIMESTAMP));
      else
            jsonObject.add("STATUS_TIMESTAMP", JsonNull.INSTANCE);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("REQUEST",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("REQUEST");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an REQUEST object.");
        }

        JsonElement property;

	    property = object.get("ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.ID = property.getAsString();
            
        property = object.get("REQUEST_TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REQUEST_TYPE = property.getAsString();
            
        property = object.get("REQUEST_STATUS");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REQUEST_STATUS = property.getAsString();
            
        property = object.get("REQUESTING_USER");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REQUESTING_USER = property.getAsString();
            
        property = object.get("REQUESTED_USER");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REQUESTED_USER = property.getAsString();
            
        property = object.get("CHANNEL_UNICODE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL_UNICODE = property.getAsString();
            
        property = object.get("NAME_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME_URL = property.getAsString();
            
        property = object.get("REQUEST_MESSAGE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REQUEST_MESSAGE = property.getAsString();
            
        property = object.get("TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("SERVER_TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SERVER_TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("STATUS_TIMESTAMP");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.STATUS_TIMESTAMP = TimestampFormatter.toDate(property.getAsString());
            
      
    }
}