package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

  import java.util.ArrayList;
    import java.util.Date;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
    import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
    

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 18/08/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class CHAT extends Format {
	public String CHANNEL_UNICODE;
    public String PUSHER_CHANNEL;
    public ArrayList<PROFILE_ID> MEMBERS;
    public Date CREATION_DATE;
    public HIVE_ID PARENT_HIVE;
    

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
            
        if ((this.PUSHER_CHANNEL != null) && (!this.PUSHER_CHANNEL.isEmpty()))
            jsonObject.addProperty("PUSHER_CHANNEL",this.PUSHER_CHANNEL);
            
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
        }
        
        if ((this.CREATION_DATE != null) && (!TimestampFormatter.toString(this.CREATION_DATE).isEmpty()))
            jsonObject.addProperty("CREATION_DATE", TimestampFormatter.toString(this.CREATION_DATE));
            
        if (this.PARENT_HIVE != null) {
            JsonElement jsonElement = this.PARENT_HIVE.toJSON();
            if (!jsonElement.isJsonNull())
                jsonObject.add("PARENT_HIVE",jsonElement);
        }
        
      

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
        
        property = object.get("CREATION_DATE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.CREATION_DATE = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("PARENT_HIVE");
        if ((property != null) && (property.isJsonObject())) {
            this.PARENT_HIVE = new HIVE_ID(property);
        }
        
      
    }
}