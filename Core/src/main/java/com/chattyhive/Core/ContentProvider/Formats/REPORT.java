package com.chattyhive.Core.ContentProvider.Formats;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;


/*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
  * Be careful to not modify this file since your changes will not be included in future
  * versions of this file.
  *
  * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
 */
public class REPORT extends Format {
	public String SUBJECT;
    public String REPORT_MESSAGE;
    

    public REPORT() {
        super();
    }

    public REPORT(JsonElement data) {
        this();
        this.fromJSON(data);
    }

    @Override
    public JsonElement toJSON() {
        JsonObject jsonObject = new JsonObject();

	    if ((this.SUBJECT != null) && (!this.SUBJECT.isEmpty()))
            jsonObject.addProperty("SUBJECT",this.SUBJECT);
      else
            jsonObject.add("SUBJECT", JsonNull.INSTANCE);            
        if ((this.REPORT_MESSAGE != null) && (!this.REPORT_MESSAGE.isEmpty()))
            jsonObject.addProperty("REPORT_MESSAGE",this.REPORT_MESSAGE);
      else
            jsonObject.add("REPORT_MESSAGE", JsonNull.INSTANCE);            
      

        if (jsonObject.entrySet().isEmpty())
            return JsonNull.INSTANCE;

        JsonObject result = new JsonObject();
        result.add("REPORT",jsonObject);

        return result;
    }

    @Override
    public void fromJSON(JsonElement data) {
        JsonObject object = data.getAsJsonObject().getAsJsonObject("REPORT");
        if ((object == null) || (!object.isJsonObject())) {
            object = data.getAsJsonObject();
        }
        if ((object == null) || (!object.isJsonObject())) {
            throw new IllegalArgumentException("Data is not an REPORT object.");
        }

        JsonElement property;

	    property = object.get("SUBJECT");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.SUBJECT = property.getAsString();
            
        property = object.get("REPORT_MESSAGE");
        if ((property != null) && (property.isJsonPrimitive()) && (property.getAsString() != null) && (!property.getAsString().isEmpty()))
            this.REPORT_MESSAGE = property.getAsString();
            
      
    }
}