package com.chattyhive.Core.BusinessObjects.Hives;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.ChatKind;
import com.chattyhive.Core.BusinessObjects.Chats.IContextualizable;
import com.chattyhive.Core.BusinessObjects.Subscriptions.ISubscribable;
import com.chattyhive.Core.BusinessObjects.Subscriptions.SubscriberList;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;

import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.COMMON;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.HIVE;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_ID;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_USERS_FILTER;
import com.chattyhive.Core.ContentProvider.Formats.INTERVAL;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE_LIST;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by Jonathan on 6/03/14.
 * This class represents a hive. A hive is one of the most basic business objects.
 */

public class Hive implements IContextualizable,ISubscribable {
    protected Controller controller;

    // Members
    protected String category;
    protected Date creationDate;
    protected String description;
    protected String name;
    protected String nameUrl;
    protected Chat publicChat;
    protected Integer subscribedUsersCount;
    protected String[] chatLanguages;
    protected String[] tags;
    protected SubscriberList<Hive> subscribedUsers;

    protected String imageURL;
    protected Image hiveImage;

    private CallbackDelegate createHiveCallback;

    // Events


    // Constructors
    private Hive() {
        this.OnSubscribedUsersListUpdated = new Event<EventArgs>();
        this.controller = Controller.GetRunningController();
    }

    public Hive(HIVE data) {
        this();
        this.category = data.CATEGORY;
        this.creationDate = data.CREATION_DATE;
        this.description = data.DESCRIPTION;

        this.name = data.NAME;
        this.nameUrl = data.NAME_URL;

        this.setImageURL(data.IMAGE_URL);

        this.publicChat = null;

        this.subscribedUsersCount = 0;
        this.tags = new String[0];
        this.chatLanguages = new String[0];

        if (data.TAGS != null) {
            this.tags = data.TAGS.toArray(new String[data.TAGS.size()]);
        }

        if (data.CHAT_LANGUAGES != null) {
            this.chatLanguages = data.CHAT_LANGUAGES.toArray(new String[data.CHAT_LANGUAGES.size()]);
        }

        if (data.SUBSCRIBED_USERS != null) {
            this.subscribedUsersCount = data.SUBSCRIBED_USERS;
        }

        if (data.PUBLIC_CHAT != null) {
            this.publicChat = Chat.getChat(data.PUBLIC_CHAT.CHANNEL_UNICODE, false);
            if (this.publicChat == null) {
                this.publicChat = new Chat(data.PUBLIC_CHAT,this);
            }
        }
    }
    public Hive(HIVE_ID hiveId, String accountID) {
        this(hiveId.NAME_URL,true,accountID);
    }
    private Hive(String nameUrl,Boolean internal, String accountID) {
        this();
        if (!internal) {
            this.name = nameUrl;
            this.creationDate = new Date();
        } else {
            this.nameUrl = nameUrl;
            Command command = new Command(AvailableCommands.HiveInfo,this.toFormat(new HIVE_ID()));
            command.addCallbackDelegate(new CallbackDelegate(this,"onLoadHiveCallback",CommandCallbackEventArgs.class));
            this.controller.getDataProvider().runCommand(accountID,command, CommandQueue.Priority.RealTime);
        }
    }
    public Hive(String name, String nameUrl) {
        this(name,false,null);
        this.nameUrl = nameUrl;
    }
    public Hive(String name) {
        this(name,false,null);
    }

    // Simple Getters/Setters
    public Boolean isLoaded () {
        return ((this.category != null) && (this.creationDate != null) && (this.nameUrl != null) && (this.name != null));
    }

    public Date getCreationDate() { return this.creationDate; }

    public String getID() { return this.nameUrl; }
    public void setID(String nameUrl) {
        this.nameUrl = nameUrl;
    }

    public Chat getPublicChat() { return this.publicChat; }
    public void setPublicChat(Chat value) { this.publicChat = value; }


    public String getImageURL() {
        return this.imageURL;
    }
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

    // Complex Getters/Setters

    public void setCategory (String value) { this.category = value; }
    public String getCategory() { return this.category; }

    public void setDescription (String value) { this.description = value; }
    public String getDescription() { return this.description; }

    public String getName() { return this.name; }
    public void setName(String name) {
        if ((this.nameUrl == null) || (this.nameUrl.isEmpty()))
            this.name = name;
        else
            throw new UnsupportedOperationException("It is not allowed to change a hive's name if hive is already created.");
    }

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

    // Methods


    // Callbacks
    public void onLoadHiveCallback(CommandCallbackEventArgs eventArgs) {
        //TODO: load from received formats
    }
    public void OnHiveCreated(CommandCallbackEventArgs eventArgs) {
        HIVE_ID hive_id = null;
        CHAT chat = null;
        Boolean joinOK = false;

        ArrayList<Format> received = eventArgs.getReceivedFormats();
        for (Format format : received) {
            if ((format instanceof COMMON) && (((COMMON) format).STATUS.equalsIgnoreCase("OK")))
                joinOK = true;
            else if (format instanceof HIVE_ID)
                hive_id = (HIVE_ID)format;
            else if (format instanceof CHAT)
                chat = (CHAT)format;
        }

        if ((joinOK) && (hive_id != null) && (chat != null)) {
            this.nameUrl = hive_id.NAME_URL;
            this.publicChat = new Chat(chat, this);
            this.subscribedUsersCount = 1;

            if (this.createHiveCallback != null)
                this.createHiveCallback.Run(this,eventArgs);
        }
    }

    /*************************************/
    /*     COMMUNICATION METHODS         */
    /*************************************/
    public void createHive(CallbackDelegate callback, String accountID) {
        this.createHiveCallback = callback;
        Command command = new Command(AvailableCommands.CreateHive,this.toFormat(new HIVE()));
        command.addCallbackDelegate(new CallbackDelegate(this, "OnHiveCreated", CommandCallbackEventArgs.class));
        this.controller.getDataProvider().runCommand(accountID,command, CommandQueue.Priority.RealTime);
    }
    /*************************************/

    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/


    /*************************************/
    /*     AUXILIAR FUNCTIONALITIES      */
    /*************************************/
    // User list
    public enum HiveUsersType {
        OUTSTANDING,
        LOCATION,
        RECENTLY_ONLINE
    }
    private HiveUsersType lastRequestedUserList;
    public Event<EventArgs> OnSubscribedUsersListUpdated;
    public void requestUsers(int start, int count, HiveUsersType listType, String accountID) {
        if ((this.lastRequestedUserList == null) || (this.lastRequestedUserList.compareTo(listType) != 0)) {
            this.subscribedUsers = new ArrayList<User>();
            this.lastRequestedUserList = listType;
        }
        HIVE_USERS_FILTER hive_users_filter = new HIVE_USERS_FILTER();
        hive_users_filter.RESULT_INTERVAL = new INTERVAL();

        hive_users_filter.TYPE = this.lastRequestedUserList.name();
        hive_users_filter.RESULT_INTERVAL.START_INDEX = (start > 0)?String.valueOf(start):"FIRST";
        hive_users_filter.RESULT_INTERVAL.COUNT = count;

        Command command = new Command(AvailableCommands.HiveUsers,hive_users_filter,this.toFormat(new HIVE_ID()));
        command.addCallbackDelegate(new CallbackDelegate(this, "requestUsersCallback", CommandCallbackEventArgs.class));
        this.controller.getDataProvider().runCommand(accountID, command, CommandQueue.Priority.RealTime);
    }
    public void requestUsersCallback (Object sender, CommandCallbackEventArgs eventArgs) {
        USER_PROFILE_LIST user_profile_list = null;
        Boolean requestOK = false;

        ArrayList<Format> received = eventArgs.getReceivedFormats();
        for (Format format : received) {
            if ((format instanceof COMMON) && (((COMMON) format).STATUS.equalsIgnoreCase("OK")))
                requestOK = true;
            else if (format instanceof USER_PROFILE_LIST)
                user_profile_list = (USER_PROFILE_LIST)format;
        }


        if ((requestOK) && (user_profile_list != null)) {
            if (user_profile_list.LIST != null) {
                boolean listChanged = false;
                for (USER_PROFILE user_profile : user_profile_list.LIST) {
                    try {
                        User u = new User(this.controller,user_profile);
                        listChanged = this.subscribedUsers.add(u) || listChanged;
                    } catch (Exception e) { }
                }
            }

            if (OnSubscribedUsersListUpdated != null)
                this.OnSubscribedUsersListUpdated.fire(this, EventArgs.Empty());
        }
    }

    public List<User> getSubscribedUsers() {
        if (this.subscribedUsers != null)
            return Collections.unmodifiableList(this.subscribedUsers);
        else
            return null;
    }

    public int getSubscribedUsersCount() {
        if (this.subscribedUsersCount != null)
            return this.subscribedUsersCount;
        else
            return 0;
    }
    public int incSubscribedUsers(int quantity) {
        if (this.subscribedUsersCount == null)
            this.subscribedUsersCount = 0;
        return this.subscribedUsersCount += quantity;
    }
    /*****************************************
     context (category, ...)
     *****************************************/








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
            ((HIVE) format).SUBSCRIBED_USERS = this.subscribedUsersCount;

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
            this.subscribedUsersCount = 0;
            this.tags = new String[0];
            this.chatLanguages = new String[0];

            if (((HIVE) format).TAGS != null) {
                this.tags = ((HIVE) format).TAGS.toArray(new String[((HIVE) format).TAGS.size()]);
            }

            if (((HIVE) format).CHAT_LANGUAGES != null) {
                this.chatLanguages = ((HIVE) format).CHAT_LANGUAGES.toArray(new String[((HIVE) format).CHAT_LANGUAGES.size()]);
            }

            if (((HIVE) format).SUBSCRIBED_USERS != null) {
                this.subscribedUsersCount = ((HIVE) format).SUBSCRIBED_USERS;
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
