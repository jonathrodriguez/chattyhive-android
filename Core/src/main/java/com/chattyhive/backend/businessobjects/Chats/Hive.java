package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive implements IContextualizable {

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

        DataProvider.GetDataProvider().onHiveProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(Hive.class, "onFormatReceived", FormatReceivedEventArgs.class));

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

    public static Boolean HiveIsLoaded (Hive hive) {
        return ((hive.category != null) && (hive.creationDate != null) && (hive.nameUrl != null) && (hive.name != null));
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

    public static Collection<Hive> getHives() {
        return Hives.values();
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

        this.setImageURL(data.IMAGE_URL);

        this.publicChat = null;

        this.subscribedUsers = 0;
        this.tags = new String[0];
        this.chatLanguages = new String[0];

        if (data.TAGS != null) {
            this.tags = data.TAGS.toArray(new String[data.TAGS.size()]);
        }

        if (data.CHAT_LANGUAGES != null) {
            this.chatLanguages = data.CHAT_LANGUAGES.toArray(new String[data.CHAT_LANGUAGES.size()]);
        }

        if (data.SUBSCRIBED_USERS != null) {
            this.subscribedUsers = data.SUBSCRIBED_USERS;
        }

        if (data.PUBLIC_CHAT != null) {
            this.publicChat = Chat.getChat(data.PUBLIC_CHAT.CHANNEL_UNICODE, false);
            if (this.publicChat == null) {
                this.publicChat = new Chat(data.PUBLIC_CHAT,this);
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

                        this.setImageURL(data.IMAGE_URL);

                        this.name = data.NAME;
                        this.nameUrl = data.NAME_URL;

                        if (data.PUBLIC_CHAT != null) {
                            this.publicChat = Chat.getChat(data.PUBLIC_CHAT.CHANNEL_UNICODE, false);
                            if (this.publicChat == null) {
                                this.publicChat = new Chat(data.PUBLIC_CHAT,this);
                            }
                        } else {
                            this.publicChat = Chat.getChat(String.format("presence-%s", this.nameUrl));
                        }
                        break;
                    }
                }
        }
        if ((this.nameUrl == null) || (!this.nameUrl.equals(nameUrl))) {
            this.nameUrl = nameUrl;
            DataProvider.GetDataProvider().InvokeServerCommand(AvailableCommands.HiveInfo,this.toFormat(new HIVE_ID()));
        }
    }

    public Hive(String name, String nameUrl) {
        this.name = name;
        this.nameUrl = nameUrl;
        this.creationDate = new Date();
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
    protected Chat publicChat;
    protected Integer subscribedUsers;
    protected String[] chatLanguages;
    protected String[] tags;

    protected String imageURL;
    public String getImageURL() {
        return this.imageURL;
    }
    protected Image hiveImage;
    public void setImageURL(String value) {
        this.imageURL = value;
        if (this.hiveImage != null)
            this.hiveImage.freeMemory();
        if (value != null)
            this.hiveImage = new Image(value);
        else
            this.hiveImage = null;
    }
    public Image getHiveImage() {
        return this.hiveImage;
    }

    public void setCategory (String value) { this.category = value; }
    public String getCategory() { return this.category; }

    public Date getCreationDate() { return this.creationDate; }

    public void setDescription (String value) { this.description = value; }
    public String getDescription() { return this.description; }

    public String getName() { return this.name; }

    public String getNameUrl() { return this.nameUrl; }

    public Chat getPublicChat() { return this.publicChat; }
    public void setPublicChat(Chat value) { this.publicChat = value; }

    public String[] getChatLanguages() {
        return this.chatLanguages;
    }
    public void setChatLanguages(String[] chatLanguages) {
        this.chatLanguages = chatLanguages;
    }

    public String[] getTags() {
        return this.tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public int getSubscribedUsers() {
        if (this.subscribedUsers != null)
            return this.subscribedUsers;
        else
            return 0;
    }
    public int incSubscribedUsers(int quantity) {
        if (this.subscribedUsers == null)
            this.subscribedUsers = 0;
        return this.subscribedUsers += quantity;
    }

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
            ((HIVE) format).IMAGE_URL = this.imageURL;
            ((HIVE) format).SUBSCRIBED_USERS = this.subscribedUsers;

            if (this.tags != null) {
                ((HIVE) format).TAGS = new ArrayList<String>(Arrays.asList(this.tags));
            }

            if (this.chatLanguages != null) {
                ((HIVE) format).CHAT_LANGUAGES = new ArrayList<String>(Arrays.asList(this.chatLanguages));
            }

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
            this.setImageURL(((HIVE) format).IMAGE_URL);
            this.publicChat = null;
            this.subscribedUsers = 0;
            this.tags = new String[0];
            this.chatLanguages = new String[0];

            if (((HIVE) format).TAGS != null) {
                this.tags = ((HIVE) format).TAGS.toArray(new String[((HIVE) format).TAGS.size()]);
            }

            if (((HIVE) format).CHAT_LANGUAGES != null) {
                this.chatLanguages = ((HIVE) format).CHAT_LANGUAGES.toArray(new String[((HIVE) format).CHAT_LANGUAGES.size()]);
            }

            if (((HIVE) format).SUBSCRIBED_USERS != null) {
                this.subscribedUsers = ((HIVE) format).SUBSCRIBED_USERS;
            }

            if (((HIVE) format).PUBLIC_CHAT != null) {
                this.publicChat = Chat.getChat(((HIVE) format).PUBLIC_CHAT);
                if (this.publicChat == null) {
                    this.publicChat = new Chat(((HIVE) format).PUBLIC_CHAT,this);
                }
            } else {
                this.publicChat = Chat.getChat(String.format("presence-%s", this.nameUrl));
                this.publicChat.parentHive = this;
                this.publicChat.chatKind = ChatKind.HIVE;
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
