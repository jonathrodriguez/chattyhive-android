package com.chattyhive.backend.businessobjects;

import com.chattyhive.backend.util.formatters.DateFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive {
    private String name;
    private String name_url;
    private String description;
    private String category;
    private Date creation_date;

    // Setters
    public void setName (String value) { this.name = value; }
    public void setNameURL (String value) {this.name_url = value; }
    public void setDescription (String value) {this.description = value;}
    public void setCategory (String value) {this.category = value;}
    public void setCreationDate (Date value) {this.creation_date = value;}

    // Getters
    public String getName() { return this.name; }
    public String getNameURL() { return this.name_url; }
    public String getDescription() {return  this.description;}
    public String getCategory() {return this.category;}
    public Date getCreationDate() {return this.creation_date;}

    // Constructor
    public Hive(String name, String name_url) {
        this.name = name;
        this.name_url = name_url;
        this.category = "";
        this.description = "";
        this.creation_date = new Date();
    }

    public Hive(String name, String name_url, String category) {
        this.name = name;
        this.name_url = name_url;
        this.category = category;
        this.description = "";
        this.creation_date = new Date();
    }

    public Hive(String name, String name_url, String category, String description) {
        this.name = name;
        this.name_url = name_url;
        this.category = category;
        this.description = description;
        this.creation_date = new Date();
    }

    public Hive(JsonElement json) {
        this.fromJSON(json);
    }

    public JsonElement toJSON() {
        JsonObject jsonHive = new JsonObject();
        jsonHive.addProperty("name", this.name);
        jsonHive.addProperty("name_url", this.name_url);
        jsonHive.addProperty("description", this.description);
        jsonHive.addProperty("category", this.category);
        jsonHive.addProperty("creation_date", DateFormatter.toString(this.creation_date));
        return jsonHive;
    }

    public void fromJSON(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jsonHive = json.getAsJsonObject(); 
            this.name = jsonHive.get("name").getAsString();
            this.name_url = jsonHive.get("name_url").getAsString();
            this.description = jsonHive.get("description").getAsString();
            this.category = jsonHive.get("category").getAsString();
            this.creation_date = DateFormatter.toDate(jsonHive.get("creation_date").getAsString());
        } else {
            this.name = null;
            this.name_url = null;
            this.description = null;
            this.category = null;
            this.creation_date = null;
        }
    }
}
