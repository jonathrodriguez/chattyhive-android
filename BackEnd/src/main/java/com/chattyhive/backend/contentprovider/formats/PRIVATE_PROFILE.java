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
  * Automatically generated code by ChattyHive API Manager Code Generator on 18/08/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class PRIVATE_PROFILE extends Format {
	public String USER_ID;
    public String FIRST_NAME;
    public String LAST_NAME;
    public String LOCATION;
    public String SEX;
    public Date BIRTHDATE;
    public ArrayList<String> LANGUAGE;
    public Boolean PRIVATE_SHOW_AGE;
    public String USER_COLOR;
    public String IMAGE_URL;
    

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
            
        if ((this.FIRST_NAME != null) && (!this.FIRST_NAME.isEmpty()))
            jsonObject.addProperty("FIRST_NAME",this.FIRST_NAME);
            
        if ((this.LAST_NAME != null) && (!this.LAST_NAME.isEmpty()))
            jsonObject.addProperty("LAST_NAME",this.LAST_NAME);
            
        if ((this.LOCATION != null) && (!this.LOCATION.isEmpty()))
            jsonObject.addProperty("LOCATION",this.LOCATION);
            
        if ((this.SEX != null) && (!this.SEX.isEmpty()))
            jsonObject.addProperty("SEX",this.SEX);
            
        if ((this.BIRTHDATE != null) && (!TimestampFormatter.toString(this.BIRTHDATE).isEmpty()))
            jsonObject.addProperty("BIRTHDATE", TimestampFormatter.toString(this.BIRTHDATE));
            
        if (this.LANGUAGE != null) {
            JsonArray jsonArray = new JsonArray();
            for (String element : this.LANGUAGE) {
                JsonElement jsonElement =   new JsonPrimitive(element)
  ;
                if (!jsonElement.isJsonNull())
                    jsonArray.add(jsonElement);
            }

            if (jsonArray.size() > 0)
                jsonObject.add("LANGUAGE",jsonArray);
        }
        
        if (this.PRIVATE_SHOW_AGE != null)
            jsonObject.addProperty("PRIVATE_SHOW_AGE",this.PRIVATE_SHOW_AGE);
            
        if ((this.USER_COLOR != null) && (!this.USER_COLOR.isEmpty()))
            jsonObject.addProperty("USER_COLOR",this.USER_COLOR);
            
        if ((this.IMAGE_URL != null) && (!this.IMAGE_URL.isEmpty()))
            jsonObject.addProperty("IMAGE_URL",this.IMAGE_URL);
            
      

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
            
        property = object.get("FIRST_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.FIRST_NAME = property.getAsString();
            
        property = object.get("LAST_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.LAST_NAME = property.getAsString();
            
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
            
        property = object.get("USER_COLOR");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_COLOR = property.getAsString();
            
        property = object.get("IMAGE_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.IMAGE_URL = property.getAsString();
            
      
    }
}