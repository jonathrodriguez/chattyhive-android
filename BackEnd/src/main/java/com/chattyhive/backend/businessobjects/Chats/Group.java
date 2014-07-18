package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Jonathan on 16/06/2014.
 */
public class Group {
    /**************************
       Static group management
     **************************/
    protected static GroupLocalStorageInterface localStorage;
    protected static Controller controller;

    private static TreeMap<String,Group> Groups;
    public static void Initialize(Controller controller, GroupLocalStorageInterface groupLocalStorageInterface) {
        if (Group.Groups == null) {
            Group.Groups = new TreeMap<String, Group>();
        }

        Group.controller = controller;
        Group.localStorage = groupLocalStorageInterface;

        try {
            DataProvider.GetDataProvider().onChatProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(Group.class, "onFormatReceived", FormatReceivedEventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        //Local recovering of groups.
        String[] groups = groupLocalStorageInterface.RecoverGroups();
        for(String group : groups) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(group));
            for (Format format : formats) {
                if (format instanceof CHAT) {
                    Group.Groups.put(((CHAT)format).CHANNEL_UNICODE,new Group((CHAT)format));
                }
            }
        }

        //Remote recovering of groups
        if (DataProvider.isConnectionAvailable()) {
            DataProvider.GetDataProvider().InvokeServerCommand(ServerCommand.AvailableCommands.ChatList,null);
        }
    }

    /*****************************************
                 Constructor
     *****************************************/
    public Group(Format format) {
        this.members = new TreeMap<String, User>();
        this.chat = new Chat(this);
        this.fromFormat(format);
    }

    protected Group(String channelUnicode) {
        this.members = new TreeMap<String, User>();

        Format[] formats = Format.getFormat((new JsonParser()).parse(Group.localStorage.RecoverGroup(channelUnicode)));
        for(Format format : formats)
            if (format instanceof CHAT) {
                CHAT data = (CHAT)format;
                if (data.CHANNEL_UNICODE.equals(channelUnicode)) {
                    this.channelUnicode = data.CHANNEL_UNICODE;
                    this.creationDate = data.CREATION_DATE;
                    this.description = ""; //TODO: There is no description.
                    this.name = ""; //TODO: There is no name;
                    if ((data.PARENT_HIVE != null) && (data.PARENT_HIVE.NAME_URL != null) && (!data.PARENT_HIVE.NAME_URL.isEmpty()))
                        this.parentHive = Hive.getHive(data.PARENT_HIVE.NAME_URL);
                    else
                        this.parentHive = null;
                    this.pusherChannel = data.PUSHER_CHANNEL;
                    break;
                }
            }

        if ((this.channelUnicode == null) || (!this.channelUnicode.equals(channelUnicode))) {
            //TODO: Implement server information recovering
        }
    }

    public static Group getGroup(String channelUnicode) {
        return Group.getGroup(channelUnicode,true);
    }

    public static Group getGroup(String channelUnicode, Boolean addToList) {
        if (Group.Groups == null) throw new IllegalStateException("Groups must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Group.Groups.containsKey(channelUnicode))
            return Group.Groups.get(channelUnicode);
        else if (addToList) {
            Group g = new Group(channelUnicode);
            Group.Groups.put(channelUnicode,g);
            return g;
        } else {
            return null;
        }
    }

    public static Group getGroup(Format format) {
        Group g = new Group(format);
        if ((g.channelUnicode != null) && (!g.channelUnicode.isEmpty())) {
            Group existent = Group.getGroup(g.channelUnicode,false);
            if (existent == null) {
                Group.Groups.put(g.channelUnicode,g);
                return g;
            } else {
                return existent;
            }
        } else {
            throw new IllegalArgumentException("Specified format is not correct.");
        }
    }

    public static Group createGroup(Collection<User> users,String parentGroup) {
        //TODO: implement server communication
        String groupName = ""; //Recovered from server.
        //TODO: implement local storage

        Group g = new Group(groupName);
        Group.Groups.put(groupName,g);
        return g;
    }

    public static void removeGroup(String channelUnicode) {
        if (Group.Groups == null) throw new IllegalStateException("Groups must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Group.Groups.containsKey(channelUnicode)) {
            Group.Groups.get(channelUnicode).chat.clearAllMessages();
            Group.Groups.remove(channelUnicode);
        }

        Group.localStorage.RemoveGroup(channelUnicode);
    }
    public static void clearGroups() {
        if (Group.Groups == null) throw new IllegalStateException("Groups must be initialized.");

        for (Group group : Group.Groups.values())
            group.chat.clearAllMessages();

        Group.Groups.clear();
        Group.localStorage.ClearGroups();
    }

    /***********************************/
    /*        STATIC CALLBACKS         */
    /***********************************/

    public static void onFormatReceived(Object sender, FormatReceivedEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if (format instanceof CHAT) {
                    Group.getGroup(((CHAT) format).CHANNEL_UNICODE).fromFormat(format);
                } else if (format instanceof CHAT_ID) {
                    Group.getGroup(((CHAT_ID) format).CHANNEL_UNICODE).fromFormat(format);
                } else if  (format instanceof CHAT_SYNC) {
                    Group.getGroup(((CHAT_SYNC) format).CHANNEL_UNICODE).fromFormat(format);
                }
            }
        }
    }

    /*****************************************
                users list
     *****************************************/
    protected TreeMap<String,User> members;

    public ArrayList<User> getMembers() {
        if ((this.members == null) || (this.members.isEmpty())) throw new NullPointerException("There are no members for this group.");

        return new ArrayList<User>(this.members.values());
    }
    public User getMember(String identifier) {
        if ((this.members == null) || (this.members.isEmpty())) throw new NullPointerException("There are no members for this group.");
        else if (identifier == null) throw new NullPointerException("Identifier must not be null.");
        else if (identifier.isEmpty()) throw  new IllegalArgumentException("Identifier must not be empty.");

        return members.get(identifier);
    }
    public void addMember(User user) {
        if (user == null) throw new NullPointerException("User must not be null.");

        members.put(user.getUserID(), user);
    }

    public void requestMembers() {
        //TODO: implement server request (NOT AVAILABLE)
    }
    public void inviteMember(String identifier) {
        //TODO: implement server request (NOT AVAILABLE)
    }

    /*****************************************
           context (group shared files, ...)
     *****************************************/

    protected String channelUnicode;
    protected Chat chat;
    protected Date creationDate;
    protected String description;
    protected GroupKind groupKind;
    protected String name;
    protected Hive parentHive;
    protected String pusherChannel;



    public String getChannelUnicode() { return this.channelUnicode; }
    public void setChannelUnicode(String value) { this.channelUnicode = value; }

    public Chat getChat() { return this.chat; }

    public Date getCreationDate() { return this.creationDate; }
    public void setCreationDate(Date value) { this.creationDate = value; }

    public String getDescription() { return this.description; }
    public void setDescription(String value) { this.description = value; }
    public void requestDescriptionChange (String value) {
        Boolean hasBeenAccepted = true;
        //TODO: implement server communication

        if (hasBeenAccepted) this.setDescription(value);
        //TODO: implement local update
    }

    public GroupKind getGroupKind() { return this.groupKind; }

    public String getName() { return this.name; }
    public void setName(String value) { this.name = value; }
    public void requestNameChange (String value) {
        Boolean hasBeenAccepted = true;
        //TODO: implement server communication

        if (hasBeenAccepted) this.setName(value);
        //TODO: implement local update
    }

    public Hive getParentHive() { return this.parentHive; }

    public String getPusherChannel() { return this.pusherChannel; }
    public void setPusherChannel(String value) { this.pusherChannel = value; }


    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/
    public Format toFormat(Format format) {
        if (format instanceof CHAT) {
            ((CHAT) format).CHANNEL_UNICODE = this.channelUnicode;
            ((CHAT) format).PARENT_HIVE = (HIVE_ID)this.parentHive.toFormat(new HIVE_ID());
            ((CHAT) format).CREATION_DATE = this.creationDate;
            ((CHAT) format).PUSHER_CHANNEL = this.pusherChannel;
            ((CHAT) format).MEMBERS = new ArrayList<PROFILE_ID>();
            for (User user : this.members.values())
                ((CHAT) format).MEMBERS.add((PROFILE_ID)user.toFormat(new PROFILE_ID()));
        } else if (format instanceof CHAT_ID) {
            ((CHAT_ID) format).CHANNEL_UNICODE = this.channelUnicode;

        } else if (format instanceof CHAT_SYNC) {
            ((CHAT_SYNC) format).CHANNEL_UNICODE = this.channelUnicode;

            ((CHAT_SYNC) format).LAST_MESSAGE = (MESSAGE)this.chat.getLastMessage().toFormat(new MESSAGE());
        }

        return format;
    }
    public Boolean fromFormat(Format format) {
        if (format instanceof CHAT) {
            this.channelUnicode = ((CHAT) format).CHANNEL_UNICODE;
            this.creationDate = ((CHAT) format).CREATION_DATE;
            this.pusherChannel = ((CHAT) format).PUSHER_CHANNEL;
            this.members = new TreeMap<String, User>();
            for (PROFILE_ID profile_id : ((CHAT) format).MEMBERS)
                this.addMember(User.getUser(profile_id));

            return true;
        } else if (format instanceof CHAT_ID) {
            this.channelUnicode = ((CHAT_ID) format).CHANNEL_UNICODE;

            return true;
        } else if (format instanceof CHAT_SYNC) {
            this.channelUnicode = ((CHAT_SYNC) format).CHANNEL_UNICODE;
            this.chat.addMessage(new Message(((CHAT_SYNC) format).LAST_MESSAGE));

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

        throw  new IllegalArgumentException("Expected CHAT, CHAT_ID or CHAT_SYNC formats.");
    }

}
