package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive {

    /**************************
       Static hive management
     **************************/
    protected static HiveLocalStorageInterface localStorage;
    private static TreeMap<String,Hive> Hives;

    public static void Initialize(Controller controller, HiveLocalStorageInterface hiveLocalStorageInterface) {
        if (Hive.Hives == null) {
            Hive.Hives = new TreeMap<String, Hive>();
        }

        Hive.localStorage = hiveLocalStorageInterface;

        String[] hives = Hive.localStorage.RecoverHives();
        for(String hive : hives) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(hive));
            for (Format format : formats)
                if (format instanceof HIVE)
                    Hive.Hives.put(((HIVE)format).NAME_URL,new Hive((HIVE)format));
        }
        //TODO: Implement remote recovering of hives.
    }

    /*****************************************
     Constructor
     *****************************************/
    private Hive(HIVE data) {
        this.category = data.CATEGORY;
        this.creationDate = data.CREATION_DATE;
        this.description = data.DESCRIPTION;

        this.name = data.NAME;
        this.nameUrl = data.NAME_URL;

        this.publicChat = Group.getGroup(data.PUBLIC_CHAT.CHANNEL_UNICODE,false);
        if (this.publicChat == null) {
            this.publicChat = new Group(data.PUBLIC_CHAT);
        }
    }
    private Hive(String nameUrl) {

        Format[] formats = Format.getFormat((new JsonParser()).parse(Hive.localStorage.RecoverHive(nameUrl)));
        for(Format format : formats)
            if (format instanceof HIVE) {
                HIVE data = (HIVE)format;
                if (data.NAME_URL.equals(nameUrl)) {
                    this.category = data.CATEGORY;
                    this.creationDate = data.CREATION_DATE;
                    this.description = data.DESCRIPTION;

                    this.name = data.NAME;
                    this.nameUrl = data.NAME_URL;

                    this.publicChat = Group.getGroup(data.PUBLIC_CHAT.CHANNEL_UNICODE,false);
                    if (this.publicChat == null) {
                        this.publicChat = new Group(data.PUBLIC_CHAT);
                    }
                    break;
                }
            }

        if ((this.nameUrl == null) || (!this.nameUrl.equals(nameUrl))) {
            //TODO: Implement server information recovering
        }
    }

    public static Hive getHive(String nameUrl) {
        if ((Hive.Hives == null) || (Hive.Hives.isEmpty())) throw new NullPointerException("There are no hives.");
        else if (nameUrl == null) throw new NullPointerException("NameUrl must not be null.");
        else if (nameUrl.isEmpty()) throw  new IllegalArgumentException("NameUrl must not be empty.");

        if (Hive.Hives.containsKey(nameUrl))
            return Hive.Hives.get(nameUrl);
        else {
            Hive h = new Hive(nameUrl);
            Hive.Hives.put(nameUrl,h);
            return h;
        }
    }

    public static Hive createHive(String name, String category, String description) {
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
    public void requestUsers() {
        //TODO: implement server request
        //TODO: implement local update
    }

    /*****************************************
     context (category, ...)
     *****************************************/
    protected String category;
    protected Date creationDate;
    protected String description;
    protected String name;
    protected String nameUrl;
    protected Group publicChat;



    public void setCategory (String value) { this.category = value; }
    public String getCategory() { return this.category; }

    public Date getCreationDate() { return this.creationDate; }

    public void setDescription (String value) { this.description = value; }
    public String getDescription() { return this.description; }

    public String getName() { return this.name; }

    public String getNameUrl() { return this.nameUrl; }

    public Group getPublicChat() { return this.publicChat; }
}
