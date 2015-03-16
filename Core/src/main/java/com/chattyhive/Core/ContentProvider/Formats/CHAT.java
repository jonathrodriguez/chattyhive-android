package com.chattyhive.Core.ContentProvider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.util.ArrayList;
     import java.util.Date;
import com.chattyhive.Core.Util.Formatters.TimestampFormatter;


/*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class CHAT extends Format {
	public String CHANNEL_UNICODE;
    public String PUSHER_CHANNEL;
    public ArrayList<PROFILE_ID> MEMBERS;
    public String CHAT_TYPE;
    public Date CREATION_DATE;
    public HIVE_ID PARENT_HIVE;
    public String NAME;
    public String DESCRIPTION;
    

    public CHAT() {
        super();
    }

    public CHAT(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.CHANNEL_UNICODE != null) && (!this.CHANNEL_UNICODE.isEmpty()))
            jsonObject.addProperty("CHANNEL_UNICODE",this.CHANNEL_UNICODE);
      else
            jsonObject.add("CHANNEL_UNICODE", JsonNull.INSTANCE);            
        if ((this.PUSHER_CHANNEL != null) && (!this.PUSHER_CHANNEL.isEmpty()))
            jsonObject.addProperty("PUSHER_CHANNEL",this.PUSHER_CHANNEL);
      else
            jsonObject.add("PUSHER_CHANNEL", JsonNull.INSTANCE);            
        if (this.MEMBERS != null) {
            JsonArray jsonArray = new JsonArray();
            for (PROFILE_ID element : this.MEMBERS) {
                JsonElement jsonElement =   element.toJSON()
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("MEMBERS",jsonArray);
            else
                jsonObject.add("MEMBERS", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("MEMBERS", JsonNull.INSTANCE);        
        if ((this.CHAT_TYPE != null) && (!this.CHAT_TYPE.isEmpty()))
            jsonObject.addProperty("CHAT_TYPE",this.CHAT_TYPE);
      else
            jsonObject.add("CHAT_TYPE", JsonNull.INSTANCE);            
        if ((this.CREATION_DATE != null) && (!TimestampFormatter.toString(this.CREATION_DATE).isEmpty()))
            jsonObject.addProperty("CREATION_DATE", TimestampFormatter.toString(this.CREATION_DATE));
      else
            jsonObject.add("CREATION_DATE", JsonNull.INSTANCE);
            
        if (this.PARENT_HIVE != null) {
            JsonElement jsonElement = this.PARENT_HIVE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("PARENT_HIVE",jsonElement);
        }
      else
            jsonObject.add("PARENT_HIVE", JsonNull.INSTANCE);        
        if ((this.NAME != null) && (!this.NAME.isEmpty()))
            jsonObject.addProperty("NAME",this.NAME);
      else
            jsonObject.add("NAME", JsonNull.INSTANCE);            
        if ((this.DESCRIPTION != null) && (!this.DESCRIPTION.isEmpty()))
            jsonObject.addProperty("DESCRIPTION",this.DESCRIPTION);
      else
            jsonObject.add("DESCRIPTION", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("CHAT",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("CHAT");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an CHAT object.");
        }

        JsonElement property;

	    property = object.get("CHANNEL_UNICODE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHANNEL_UNICODE = property.getAsString();
            
        property = object.get("PUSHER_CHANNEL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUSHER_CHANNEL = property.getAsString();
            
        property = object.get("MEMBERS");
        if ((property != null) && (property.isJsonArray())) {
            this.MEMBERS = new ArrayList<PROFILE_ID>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.MEMBERS.add(  new PROFILE_ID(jsonElement)
      );
        }
        
        property = object.get("CHAT_TYPE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CHAT_TYPE = property.getAsString();
            
        property = object.get("CREATION_DATE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CREATION_DATE = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("PARENT_HIVE");
        if ((property != null) && (property.isJsonObject())) {
            this.PARENT_HIVE = new HIVE_ID(property);
        }
        
        property = object.get("NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.NAME = property.getAsString();
            
        property = object.get("DESCRIPTION");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.DESCRIPTION = property.getAsString();
            
      
    }
}