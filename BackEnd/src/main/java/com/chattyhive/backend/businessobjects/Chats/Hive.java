package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive extends Group {

    /**************************
       Static hive management
     **************************/
    protected static MessageLocalStorageInterface messageLocalStorageInterface;

    private static TreeMap<String,Hive> Hives;

    public static void Initialize(Controller controller, MessageLocalStorageInterface messageLocalStorageInterface) {
        Group.Initialize(controller,messageLocalStorageInterface);
        if (Hive.Hives == null) {
            Hive.Hives = new TreeMap<String, Hive>();
        }

        Hive.localStorage = messageLocalStorageInterface;

        //TODO: Implement local and remote recovering of hives.
    }

    /*****************************************
     Constructor
     *****************************************/
    private Hive(String channelName) {
        super(channelName);

        //TODO: Implement server and local information recovering
    }

    public static Hive getHive(String channelName) {
        if ((Hive.Hives == null) || (Hive.Hives.isEmpty())) throw new NullPointerException("There are no hives.");
        else if (channelName == null) throw new NullPointerException("channelName must not be null.");
        else if (channelName.isEmpty()) throw  new IllegalArgumentException("channelName must not be empty.");

        if (Hive.Hives.containsKey(channelName))
            return Hive.Hives.get(channelName);
        else {
            Hive h = new Hive(channelName);
            Hive.Hives.put(channelName,h);
            return h;
        }
    }

    public static Hive createHive(String name, String categroy, String description) {
        //TODO: implement server communication
        String channelName = ""; //Recovered from server.
        //TODO: implement local storage

        Hive h = new Hive(channelName);
        Hive.Hives.put(channelName,h);
        return h;
    }

    /*****************************************
     users list
     *****************************************/
    @Override
    public void requestUsers() {
        //TODO: implement server request
        //TODO: implement local update
    }

    /*****************************************
     context (category, ...)
     *****************************************/
    protected String category;
    public void setCategory (String value) { this.category = value; }
    public String getCategory() { return this.category; }




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
