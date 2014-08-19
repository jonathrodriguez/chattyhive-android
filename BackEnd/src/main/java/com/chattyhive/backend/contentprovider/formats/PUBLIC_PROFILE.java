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
public class PUBLIC_PROFILE extends Format {
	public String PUBLIC_NAME;
    public String LOCATION;
    public String SEX;
    public Date BIRTHDATE;
    public ArrayList<String> LANGUAGE;
    public Boolean PUBLIC_SHOW_SEX;
    public Boolean PUBLIC_SHOW_AGE;
    public Boolean PUBLIC_SHOW_LOCATION;
    public String USER_COLOR;
    public String IMAGE_URL;
    

    public PUBLIC_PROFILE() {
        super();
    }

    public PUBLIC_PROFILE(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.PUBLIC_NAME != null) && (!this.PUBLIC_NAME.isEmpty()))
            jsonObject.addProperty("PUBLIC_NAME",this.PUBLIC_NAME);
            
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
        
        if (this.PUBLIC_SHOW_SEX != null)
            jsonObject.addProperty("PUBLIC_SHOW_SEX",this.PUBLIC_SHOW_SEX);
            
        if (this.PUBLIC_SHOW_AGE != null)
            jsonObject.addProperty("PUBLIC_SHOW_AGE",this.PUBLIC_SHOW_AGE);
            
        if (this.PUBLIC_SHOW_LOCATION != null)
            jsonObject.addProperty("PUBLIC_SHOW_LOCATION",this.PUBLIC_SHOW_LOCATION);
            
        if ((this.USER_COLOR != null) && (!this.USER_COLOR.isEmpty()))
            jsonObject.addProperty("USER_COLOR",this.USER_COLOR);
            
        if ((this.IMAGE_URL != null) && (!this.IMAGE_URL.isEmpty()))
            jsonObject.addProperty("IMAGE_URL",this.IMAGE_URL);
            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("PUBLIC_PROFILE",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("PUBLIC_PROFILE");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an PUBLIC_PROFILE object.");
        }

        JsonElement property;

	    property = object.get("PUBLIC_NAME");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.PUBLIC_NAME = property.getAsString();
            
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
        
        property = object.get("PUBLIC_SHOW_SEX");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_SEX = property.getAsBoolean();
            
        property = object.get("PUBLIC_SHOW_AGE");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_AGE = property.getAsBoolean();
            
        property = object.get("PUBLIC_SHOW_LOCATION");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_LOCATION = property.getAsBoolean();
            
        property = object.get("USER_COLOR");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.USER_COLOR = property.getAsString();
            
        property = object.get("IMAGE_URL");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.IMAGE_URL = property.getAsString();
            
      
    }
}