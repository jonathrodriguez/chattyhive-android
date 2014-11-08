package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Users.ProfileLevel;
import com.chattyhive.backend.businessobjects.Users.ProfileType;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
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

    public static Event<EventArgs> GroupListChanged;

    private static TreeMap<String,Group> UnloadedGroups;
    private static TreeMap<String,Group> Groups;
    public static void Initialize(Controller controller, GroupLocalStorageInterface groupLocalStorageInterface) {
        GroupListChanged = new Event<EventArgs>();
        if (Group.UnloadedGroups == null) {
            Group.UnloadedGroups = new TreeMap<String, Group>();
        }
        if (Group.Groups == null) {
            Group.Groups = new TreeMap<String, Group>();
        }

        Group.controller = controller;
        Group.localStorage = groupLocalStorageInterface;

        DataProvider.GetDataProvider().onChatProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(Group.class, "onFormatReceived", FormatReceivedEventArgs.class));


        //Remote recovering of groups -> Recovered when binding app or service.
/*        if (DataProvider.isConnectionAvailable()) {
            DataProvider.GetDataProvider().InvokeServerCommand(ServerCommand.AvailableCommands.ChatList,null);
        }*/
    }

    public static void RecoverLocalGroups() {
        if ((Group.localStorage == null) || (Group.controller == null)) throw new IllegalStateException("Groups must be initialized");

        //Local recovering of groups.
        String[] groups = Group.localStorage.RecoverGroups();
        if (groups != null) {
            for (String group : groups) {
                Format[] formats = Format.getFormat((new JsonParser()).parse(group));
                for (Format format : formats) {
                    if (format instanceof CHAT) {
                        Group g = new Group((CHAT) format, null);
                        if (g.isLoaded()) {
                            Group.Groups.put(g.channelUnicode, g);
                            if (GroupListChanged != null)
                                GroupListChanged.fire(g, EventArgs.Empty());
                        }
                        else
                            Group.UnloadedGroups.put(g.channelUnicode,g);
                    }
                }
            }
        }
    }

    public Boolean isLoaded() {
        Boolean result = false;
        switch (this.getGroupKind()) {
            case HIVE:
                result = ((this.parentHive != null) && (Hive.HiveIsLoaded(this.parentHive)) && (this.getChat() != null));
                break;
            case PUBLIC_SINGLE:
                result = ((this.getChat() != null) && (this.parentHive != null) && (Hive.HiveIsLoaded(this.parentHive)) && (this.members != null));
                if (result) {
                    Boolean anotherUser = false;
                    for (User user : this.members.values())
                        if (!user.isMe())
                            if ((!user.isLoading()) && (user.getUserPublicProfile() != null) && (user.getUserPublicProfile().getPublicName() != null)) {
                                anotherUser = true;
                            } else {
                                user.UserLoaded.add(new EventHandler<EventArgs>(this,"testLoaded",EventArgs.class));
                                if (!user.isLoading())
                                    user.loadProfile(ProfileType.PUBLIC, ProfileLevel.Basic);
                            }
                    if (!anotherUser)
                        result = false;
                }
                break;
            case PRIVATE_SINGLE:
                result = ((this.getChat() != null) && (this.members != null));
                if (result) {
                    Boolean anotherUser = false;
                    for (User user : this.members.values())
                        if (!user.isMe())
                            if ((!user.isLoading()) && (user.getUserPrivateProfile() != null) && (user.getUserPrivateProfile().getShowingName() != null)) {
                                anotherUser = true;
                            } else {
                                user.UserLoaded.add(new EventHandler<EventArgs>(this,"testLoaded",EventArgs.class));
                                if (!user.isLoading())
                                    user.loadProfile(ProfileType.PRIVATE, ProfileLevel.Basic);
                            }
                    if (!anotherUser)
                        result = false;
                }
                break;
        }
        //TODO: before returning correct result it's needed to define corresponding load methods and events, else, unloaded groups will remain in unloaded group list.
        //return result;
        return true;
    }

    public void testLoaded(Object sender, EventArgs eventArgs) {
        if (sender instanceof User)
            ((User)sender).UserLoaded.remove(new EventHandler<EventArgs>(this,"testLoaded",EventArgs.class));

        if (this.isLoaded()) {
            UnloadedGroups.remove(this.channelUnicode);
            Groups.put(this.channelUnicode,this);
            if (GroupListChanged != null)
                GroupListChanged.fire(this, EventArgs.Empty());
        }
    }

    /***********************************/
    /*        STATIC LIST SUPPORT      */
    /***********************************/

    public static Group getGroupByIndex(int index) {
        return Groups.values().toArray(new Group[Groups.size()])[index];
    }

    public static int getGroupCount() {
        return Groups.size();
    }

    /*****************************************
                 Constructor
     *****************************************/
    public Group(Format format,Hive hive) {
        this.members = new TreeMap<String, User>();
        this.chat = new Chat(this);
        if (hive != null)
            this.parentHive = hive;
        this.fromFormat(format);
    }

    protected Group(String channelUnicode) {
        this.members = new TreeMap<String, User>();
        String localGroup = Group.localStorage.RecoverGroup(channelUnicode);
        if (localGroup != null) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(localGroup));
            for (Format format : formats)
                if (format instanceof CHAT) {
                    CHAT data = (CHAT) format;
                    if (data.CHANNEL_UNICODE.equals(channelUnicode)) {
                        this.channelUnicode = data.CHANNEL_UNICODE;
                        this.creationDate = data.CREATION_DATE;
                        this.description = data.DESCRIPTION;
                        this.name = data.NAME;
                        this.groupKind = GroupKind.valueOf(data.CHAT_TYPE);
                        if ((data.PARENT_HIVE != null) && (data.PARENT_HIVE.NAME_URL != null) && (!data.PARENT_HIVE.NAME_URL.isEmpty()))
                            this.parentHive = Hive.getHive(data.PARENT_HIVE.NAME_URL);
                        else
                            this.parentHive = null;
                        this.pusherChannel = data.PUSHER_CHANNEL;
                        this.chat = new Chat(this);
                        break;
                    }
                }
        }
        if ((this.channelUnicode == null) || (!this.channelUnicode.equals(channelUnicode))) {
            this.channelUnicode = channelUnicode;
            this.chat = new Chat(this);
            DataProvider.GetDataProvider().InvokeServerCommand(AvailableCommands.ChatInfo,this.toFormat(new CHAT_ID()));
        }
    }

    public Group(GroupKind groupKind, Hive parentHive) {
        if ((groupKind != GroupKind.PRIVATE_SINGLE) && (groupKind != GroupKind.PRIVATE_GROUP) && (parentHive == null))
            throw new IllegalArgumentException("If chat is not private, parentHive CAN'T be null.");
        this.groupKind = groupKind;
        this.parentHive = parentHive;
        this.chat = new Chat(this);
        this.creationDate = new Date();
    }

    public static Group getGroup(String channelUnicode) {
        return Group.getGroup(channelUnicode,true);
    }

    public static Group getGroup(String channelUnicode, Boolean addToList) {
        if ((Group.UnloadedGroups == null) || (Group.Groups == null)) throw new IllegalStateException("Groups must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Group.UnloadedGroups.containsKey(channelUnicode))
            return Group.UnloadedGroups.get(channelUnicode);
        else if (Group.Groups.containsKey(channelUnicode))
            return Group.Groups.get(channelUnicode);
        else if (addToList) {
            Group g = new Group(channelUnicode);
            if (g.isLoaded()) {
                Group.Groups.put(g.channelUnicode, g);
                if (GroupListChanged != null)
                    GroupListChanged.fire(g,EventArgs.Empty());
            }
            else
                Group.UnloadedGroups.put(g.channelUnicode,g);
            return g;
        } else {
            return null;
        }
    }

    public static Group getGroup(Format format) {
        Group g = new Group(format,null);
        if ((g.channelUnicode != null) && (!g.channelUnicode.isEmpty())) {
            Group existent = Group.getGroup(g.channelUnicode,false);
            if (existent == null) {
                if (g.isLoaded()) {
                    Group.Groups.put(g.channelUnicode, g);
                    if (GroupListChanged != null)
                        GroupListChanged.fire(g,EventArgs.Empty());
                } else
                    Group.UnloadedGroups.put(g.channelUnicode, g);
                if (GroupListChanged != null)
                    GroupListChanged.fire(g,EventArgs.Empty());
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
        String groupChannelUnicode = ""; //Recovered from server.
        //TODO: implement local storage

        Group g = new Group(groupChannelUnicode);
        if (g.isLoaded()) {
            Group.Groups.put(groupChannelUnicode, g);
            if (GroupListChanged != null)
                GroupListChanged.fire(g,EventArgs.Empty());
        } else
            Group.UnloadedGroups.put(groupChannelUnicode, g);
        return g;
    }

    public static void removeGroup(String channelUnicode) {
        if ((Group.UnloadedGroups == null) || (Group.Groups == null)) throw new IllegalStateException("Groups must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Group.UnloadedGroups.containsKey(channelUnicode)) {
            Group g = Group.UnloadedGroups.get(channelUnicode);
            g.chat.clearAllMessages();
            Group.UnloadedGroups.remove(channelUnicode);
        } else if (Group.Groups.containsKey(channelUnicode)) {
            Group g = Group.Groups.get(channelUnicode);
            g.chat.clearAllMessages();
            Group.Groups.remove(channelUnicode);
            if (GroupListChanged != null)
                GroupListChanged.fire(g,EventArgs.Empty());
        }

        Group.localStorage.RemoveGroup(channelUnicode);
    }
    public static void clearGroups() {
        if ((Group.UnloadedGroups == null) || (Group.Groups == null)) throw new IllegalStateException("Groups must be initialized.");

        for (Group group : Group.UnloadedGroups.values())
            if (group.chat != null)
                group.chat.clearAllMessages();
        Group.UnloadedGroups.clear();

        for (Group group : Group.Groups.values())
            if (group.chat != null)
                group.chat.clearAllMessages();
        Group.Groups.clear();

        Group.localStorage.ClearGroups();

        if (GroupListChanged != null)
            GroupListChanged.fire(null,EventArgs.Empty());
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
        if ((this.members == null) || (this.members.isEmpty())) //throw new NullPointerException("There are no members for this group.");
            return new ArrayList<User>();

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

        if (this.members == null)
            this.members = new TreeMap<String, User>();

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


    private void CalculateGroupKind() {
        if (this.getParentHive() == null) {
            if (this.members.size() < 3)
                this.groupKind = GroupKind.PRIVATE_SINGLE;
            else
                this.groupKind = GroupKind.PRIVATE_GROUP;
        } else {
            if ((this.members == null) || (this.members.size() == 0))
                this.groupKind = GroupKind.HIVE;
            else if (this.members.size() < 3)
                this.groupKind = GroupKind.PUBLIC_SINGLE;
            else
                this.groupKind = GroupKind.PUBLIC_GROUP;
        }
    }
    public GroupKind getGroupKind() {
        if (this.groupKind == null)
            this.CalculateGroupKind();
        return this.groupKind;
    }
    public void setGroupKind(GroupKind value) {
        this.groupKind = value;
    }

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
            if (this.parentHive != null)
                ((CHAT) format).PARENT_HIVE = (HIVE_ID)this.parentHive.toFormat(new HIVE_ID());

            ((CHAT) format).CREATION_DATE = this.creationDate;
            ((CHAT) format).PUSHER_CHANNEL = this.pusherChannel;
            ((CHAT) format).CHAT_TYPE = this.groupKind.toString();
            if ((this.members != null) && (this.members.size() > 0)) {
                ((CHAT) format).MEMBERS = new ArrayList<PROFILE_ID>();
                for (User user : this.members.values()) {
                    PROFILE_ID profile_id = new PROFILE_ID();
                    profile_id.USER_ID = user.getUserID();
                    profile_id.PROFILE_TYPE = "BASIC_" + ((this.groupKind.toString().startsWith("PRIVATE_"))?"PRIVATE":"PUBLIC");
                    ((CHAT) format).MEMBERS.add(profile_id);
                }
            }
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
            this.groupKind = GroupKind.valueOf(((CHAT) format).CHAT_TYPE);
            if (((CHAT) format).MEMBERS != null) {
                this.members = new TreeMap<String, User>();
                for (PROFILE_ID profile_id : ((CHAT) format).MEMBERS)
                    this.addMember(controller.getUser(profile_id));
            }
            if (((CHAT) format).PARENT_HIVE != null)
                if ((this.parentHive == null) || (!this.parentHive.getNameUrl().equalsIgnoreCase(((CHAT) format).PARENT_HIVE.NAME_URL)))
                    this.parentHive = Hive.getHive(((CHAT) format).PARENT_HIVE.NAME_URL);

            this.chat = new Chat(this);
            //this.CalculateGroupKind();
            return true;
        } else if (format instanceof CHAT_ID) {
            this.channelUnicode = ((CHAT_ID) format).CHANNEL_UNICODE;
            this.chat = new Chat(this);

            return true;
        } else if (format instanceof CHAT_SYNC) {
            this.channelUnicode = ((CHAT_SYNC) format).CHANNEL_UNICODE;
            this.chat = new Chat(this);
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
