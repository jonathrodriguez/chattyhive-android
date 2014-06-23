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
    private Hive(String hiveName) {
        super(hiveName);

        //TODO: Implement server and local information recovering
    }

    public static Hive getHive(String hiveName) {
        if ((Hive.Hives == null) || (Hive.Hives.isEmpty())) throw new NullPointerException("There are no hives.");
        else if (hiveName == null) throw new NullPointerException("hiveName must not be null.");
        else if (hiveName.isEmpty()) throw  new IllegalArgumentException("hiveName must not be empty.");

        if (Hive.Hives.containsKey(hiveName))
            return Hive.Hives.get(hiveName);
        else {
            Hive h = new Hive(hiveName);
            Hive.Hives.put(hiveName,h);
            return h;
        }
    }

    public static Hive createHive(String name, String categroy, String description) {
        //TODO: implement server communication
        String hiveName = ""; //Recovered from server.
        //TODO: implement local storage

        Hive h = new Hive(hiveName);
        Hive.Hives.put(hiveName,h);
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


    public JsonElement toJSON() {
        JsonObject jsonHive = new JsonObject();
        jsonHive.addProperty("name", this.name);
        jsonHive.addProperty("name_url", this.groupName);
        jsonHive.addProperty("description", this.description);
        jsonHive.addProperty("category", this.category);
        jsonHive.addProperty("creation_date", DateFormatter.toString(this.creationDate));
        return jsonHive;
    }

    public void fromJSON(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jsonHive = json.getAsJsonObject(); 
            this.name = jsonHive.get("name").getAsString();
            this.groupName = jsonHive.get("name_url").getAsString();
            this.description = jsonHive.get("description").getAsString();
            this.category = jsonHive.get("category").getAsString();
            this.creationDate = DateFormatter.toDate(jsonHive.get("creation_date").getAsString());
        } else {
            this.name = null;
            this.groupName = null;
            this.description = null;
            this.category = null;
            this.creationDate = null;
        }
    }
}
