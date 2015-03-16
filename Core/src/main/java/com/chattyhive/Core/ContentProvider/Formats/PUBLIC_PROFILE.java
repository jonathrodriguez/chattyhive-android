package com.chattyhive.Core.ContentProvider.formats;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

   import java.util.Date;
import com.chattyhive.Core.Util.Formatters.TimestampFormatter;
    import java.util.ArrayList;
        

 /*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class PUBLIC_PROFILE extends Format {
	public String USER_ID;
    public String LOCATION;
    public String SEX;
    public Date BIRTHDATE;
    public ArrayList<String> LANGUAGE;
    public Boolean PUBLIC_SHOW_SEX;
    public Boolean PUBLIC_SHOW_AGE;
    public Boolean PUBLIC_SHOW_LOCATION;
    

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

	    if ((this.USER_ID != null) && (!this.USER_ID.isEmpty()))
            jsonObject.addProperty("USER_ID",this.USER_ID);
      else
            jsonObject.add("USER_ID", JsonNull.INSTANCE);            
        if ((this.LOCATION != null) && (!this.LOCATION.isEmpty()))
            jsonObject.addProperty("LOCATION",this.LOCATION);
      else
            jsonObject.add("LOCATION", JsonNull.INSTANCE);            
        if ((this.SEX != null) && (!this.SEX.isEmpty()))
            jsonObject.addProperty("SEX",this.SEX);
      else
            jsonObject.add("SEX", JsonNull.INSTANCE);            
        if ((this.BIRTHDATE != null) && (!TimestampFormatter.toString(this.BIRTHDATE).isEmpty()))
            jsonObject.addProperty("BIRTHDATE", TimestampFormatter.toString(this.BIRTHDATE));
      else
            jsonObject.add("BIRTHDATE", JsonNull.INSTANCE);
            
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
            else
                jsonObject.add("LANGUAGE", JsonNull.INSTANCE);
        }
      else
            jsonObject.add("LANGUAGE", JsonNull.INSTANCE);        
        if (this.PUBLIC_SHOW_SEX != null)
            jsonObject.addProperty("PUBLIC_SHOW_SEX",this.PUBLIC_SHOW_SEX);
      else
            jsonObject.add("PUBLIC_SHOW_SEX", JsonNull.INSTANCE);            
        if (this.PUBLIC_SHOW_AGE != null)
            jsonObject.addProperty("PUBLIC_SHOW_AGE",this.PUBLIC_SHOW_AGE);
      else
            jsonObject.add("PUBLIC_SHOW_AGE", JsonNull.INSTANCE);            
        if (this.PUBLIC_SHOW_LOCATION != null)
            jsonObject.addProperty("PUBLIC_SHOW_LOCATION",this.PUBLIC_SHOW_LOCATION);
      else
            jsonObject.add("PUBLIC_SHOW_LOCATION", JsonNull.INSTANCE);            

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
        
        property = object.get("PUBLIC_SHOW_SEX");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_SEX = property.getAsBoolean();
            
        property = object.get("PUBLIC_SHOW_AGE");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_AGE = property.getAsBoolean();
            
        property = object.get("PUBLIC_SHOW_LOCATION");
        if ((property != null) && (property.isJsonPrimitive()) && (((JsonPrimitive)property).isBoolean()))
            this.PUBLIC_SHOW_LOCATION = property.getAsBoolean();
      
    }
}