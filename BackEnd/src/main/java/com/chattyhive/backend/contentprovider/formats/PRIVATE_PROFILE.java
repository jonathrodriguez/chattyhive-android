package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

   import java.util.Date;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
    import java.util.ArrayList;
      

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 26/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class PRIVATE_PROFILE extends Format {
	public String USER_ID;
    public String LOCATION;
    public String SEX;
    public Date BIRTHDATE;
    public ArrayList<String> LANGUAGE;
    public Boolean PRIVATE_SHOW_AGE;
    public String STATUS_MESSAGE;
    

    public PRIVATE_PROFILE() {
        super();
    }

    public PRIVATE_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.USER_ID != null) && (!this.USER_ID.isEmpty()))
            jsonObject.addProperty("USER_ID",this.USER_ID);
      else
            jsonObject.addProperty("USER_ID", JsonNull.INSTANCE);            
        if ((this.LOCATION != null) && (!this.LOCATION.isEmpty()))
            jsonObject.addProperty("LOCATION",this.LOCATION);
      else
            jsonObject.addProperty("LOCATION", JsonNull.INSTANCE);            
        if ((this.SEX != null) && (!this.SEX.isEmpty()))
            jsonObject.addProperty("SEX",this.SEX);
      else
            jsonObject.addProperty("SEX", JsonNull.INSTANCE);            
        if ((this.BIRTHDATE != null) && (!TimestampFormatter.toString(this.BIRTHDATE).isEmpty()))
            jsonObject.addProperty("BIRTHDATE", TimestampFormatter.toString(this.BIRTHDATE));
      else
            jsonObject.addProperty("BIRTHDATE", JsonNull.INSTANCE);
            
        if (this.LANGUAGE != null) {
            JsonArray jsonArray = new JsonArray();
            for (String element : this.LANGUAGE) {
                sonElement jsonElement =   new JsonPrimitive(element)
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("LANGUAGE",jsonArray);
            else
                jsonObject.add("LANGUAGE", JsonNull.INSTANCE);
        }
      else
            jsonObject.addProperty("LANGUAGE", JsonNull.INSTANCE);        
        if (this.PRIVATE_SHOW_AGE != null)
            jsonObject.addProperty("PRIVATE_SHOW_AGE",this.PRIVATE_SHOW_AGE);
      else
            jsonObject.addProperty("PRIVATE_SHOW_AGE", JsonNull.INSTANCE);            
        if ((this.STATUS_MESSAGE != null) && (!this.STATUS_MESSAGE.isEmpty()))
            jsonObject.addProperty("STATUS_MESSAGE",this.STATUS_MESSAGE);
      else
            jsonObject.addProperty("STATUS_MESSAGE", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("PRIVATE_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("PRIVATE_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an PRIVATE_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("USER_ID");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_ID = property.getAsString();
            
        property = object.get("LOCATION");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.LOCATION = property.getAsString();
            
        property = object.get("SEX");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SEX = property.getAsString();
            
        property = object.get("BIRTHDATE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.BIRTHDATE = TimestampFormatter.toDate(property.getAsString());
            
        property = object.get("LANGUAGE");
        if ((property != null) && (property.isJsonArray())) {
            this.LANGUAGE = new ArrayList<String>();
            JsonArray array = property.getAsJsonArray();
            for (JsonElement jsonElement : array)
                this.LANGUAGE.add(  jsonElement.getAsString()
      );
        }
        
        property = object.get("PRIVATE_SHOW_AGE");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PRIVATE_SHOW_AGE = property.getAsBoolean();
            
        property = object.get("STATUS_MESSAGE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.STATUS_MESSAGE = property.getAsString();
            
      
    }
}