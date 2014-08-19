package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
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

    public static Event<EventArgs> HiveListChanged;

    public static void Initialize(Controller controller, HiveLocalStorageInterface hiveLocalStorageInterface) {
        HiveListChanged = new Event<EventArgs>();

        if (Hive.Hives == null) {
            Hive.Hives = new TreeMap<String, Hive>();
        }

        Hive.localStorage = hiveLocalStorageInterface;

        try {
            DataProvider.GetDataProvider().onHiveProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(Hive.class, "onFormatReceived", FormatReceivedEventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //Local recovering of hives
        String[] hives = Hive.localStorage.RecoverHives();
        if (hives != null) {
            for (String hive : hives) {
                Format[] formats = Format.getFormat((new JsonParser()).parse(hive));
                for (Format format : formats)
                    if (format instanceof HIVE)
                        Hive.Hives.put(((HIVE) format).NAME_URL, new Hive((HIVE) format));
            }
            if ((Hives.size() > 0) && (HiveListChanged != null))
                HiveListChanged.fire(null, EventArgs.Empty());
        }
        //Remote recovering of hives.
        /* This will be recovered with local user profile.*/
    }

    /***********************************/
    /*        STATIC LIST SUPPORT      */
    /***********************************/

    public static Hive getHiveByIndex(int index) {
        return Hives.values().toArray(new Hive[Hives.size()])[index];
    }

    public static int getHiveCount() {
        return Hives.size();
    }

    /***********************************/
    /*        STATIC CALLBACKS         */
    /***********************************/

    public static void onFormatReceived(Object sender, FormatReceivedEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if (format instanceof HIVE) {
                    Hive.getHive(((HIVE) format).NAME_URL).fromFormat(format);
                } else if (format instanceof HIVE_ID) {
                    Hive.getHive(((HIVE_ID) format).NAME_URL).fromFormat(format);
                }
            }
        }
    }

    /*****************************************
     Constructor
     *****************************************/
    public Hive(HIVE data) {
        this.category = data.CATEGORY;
        this.creationDate = data.CREATION_DATE;
        this.description = data.DESCRIPTION;

        this.name = data.NAME;
        this.nameUrl = data.NAME_URL;

        this.publicChat = null;

        if (data.PUBLIC_CHAT != null) {
            this.publicChat = Group.getGroup(data.PUBLIC_CHAT.CHANNEL_UNICODE, false);
            if (this.publicChat == null) {
                this.publicChat = new Group(data.PUBLIC_CHAT);
            }
        }
    }
    private Hive(String nameUrl) {

        String localHive = Hive.localStorage.RecoverHive(nameUrl);
        if ((localHive != null) && (!localHive.isEmpty())) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(localHive));
            for (Format format : formats)
                if (format instanceof HIVE) {
                    HIVE data = (HIVE) format;
                    if (data.NAME_URL.equals(nameUrl)) {
                        this.category = data.CATEGORY;
                        this.creationDate = data.CREATION_DATE;
                        this.description = data.DESCRIPTION;

                        this.name = data.NAME;
                        this.nameUrl = data.NAME_URL;

                        if (data.PUBLIC_CHAT != null) {
                            this.publicChat = Group.getGroup(data.PUBLIC_CHAT.CHANNEL_UNICODE, false);
                            if (this.publicChat == null) {
                                this.publicChat = new Group(data.PUBLIC_CHAT);
                            }
                        } else {
                            this.publicChat = Group.getGroup(String.format("presence-%s",this.nameUrl));
                        }
                        break;
                    }
                }
        }
        if ((this.nameUrl == null) || (!this.nameUrl.equals(nameUrl))) {
            this.nameUrl = nameUrl;
            DataProvider.GetDataProvider().InvokeServerCommand(ServerCommand.AvailableCommands.HiveInfo,this.toFormat(new HIVE_ID()));
        }
    }

    public static Hive getHive(String nameUrl) {
        if (Hive.Hives == null) throw new IllegalStateException("Hives must be initialized.");
        else if (nameUrl == null) throw new NullPointerException("NameUrl must not be null.");
        else if (nameUrl.isEmpty()) throw  new IllegalArgumentException("NameUrl must not be empty.");

        if (Hive.Hives.containsKey(nameUrl))
            return Hive.Hives.get(nameUrl);
        else {
            Hive h = new Hive(nameUrl);
            Hive.Hives.put(nameUrl,h);
            if (HiveListChanged != null)
                HiveListChanged.fire(h,EventArgs.Empty());
            return h;
        }
    }

    public static Hive createHive(String name, String category, String description) {
        //TODO: implement server communication (NOT AVAILABLE)
        String hiveName = ""; //Recovered from server.

        Hive h = new Hive(hiveName);
        Hive.Hives.put(hiveName,h);

        if (HiveListChanged != null)
            HiveListChanged.fire(h,EventArgs.Empty());

        //Local storage
        Hive.localStorage.StoreHive(h.nameUrl,h.toJson(new HIVE()).toString());

        return h;
    }

    /*****************************************
     users list
     *****************************************/
    public void requestUsers() {
        //TODO: implement server request (NOT AVAILABLE)
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
    public void setPublicChat(Group value) { this.publicChat = value; }

    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/
    public Format toFormat(Format format) {
        if (format instanceof HIVE) {
            ((HIVE) format).NAME_URL = this.nameUrl;
            ((HIVE) format).NAME = this.name;
            ((HIVE) format).CATEGORY = this.category;
            ((HIVE) format).CREATION_DATE = this.creationDate;
            ((HIVE) format).DESCRIPTION = this.description;
            if (this.publicChat != null)
                ((HIVE) format).PUBLIC_CHAT = (CHAT)this.publicChat.toFormat(new CHAT());
            else
                ((HIVE) format).PUBLIC_CHAT = null;
        } else if (format instanceof HIVE_ID) {
            ((HIVE_ID) format).NAME_URL = this.nameUrl;
        }

        return format;
    }
    public Boolean fromFormat(Format format) {
        if (format instanceof HIVE) {
            this.name = ((HIVE) format).NAME;
            this.nameUrl = ((HIVE) format).NAME_URL;
            this.category = ((HIVE) format).CATEGORY;
            this.description = ((HIVE) format).DESCRIPTION;
            this.creationDate = ((HIVE) format).CREATION_DATE;
            this.publicChat = null;

            if (((HIVE) format).PUBLIC_CHAT != null) {
                this.publicChat = Group.getGroup(((HIVE) format).PUBLIC_CHAT);
                if (this.publicChat == null) {
                    this.publicChat = new Group(((HIVE) format).PUBLIC_CHAT);
                }
            } else {
                this.publicChat = Group.getGroup(String.format("presence-%s",this.nameUrl));
            }
            return true;
        } else if (format instanceof HIVE_ID) {
            this.nameUrl = ((HIVE_ID) format).NAME_URL;

            return true;
        }

        return false;
    }

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }
    public void fromJson(JsonElement jsonElement) {
        Format[] formats = Format.getFormat(jsonElement);
        for (Format format : formats)
            if (this.fromFormat(format)) return;

        throw  new IllegalArgumentException("Expected HIVE or HIVE_ID formats.");
    }
}
