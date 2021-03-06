package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Context.ContextElement;
import com.chattyhive.backend.businessobjects.Chats.Context.IContextualizable;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Users.ProfileLevel;
import com.chattyhive.backend.businessobjects.Users.ProfileType;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.ChatLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.CONTEXT;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_LIST;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.USER_PROFILE_LIST;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Jonathan on 16/06/2014.
 */
public class Chat implements IContextualizable {
    /**************************
       Static group management
     **************************/
    protected static ChatLocalStorageInterface localStorage;
    protected static Controller controller;

    public static Event<EventArgs> ChatListChanged;

    private static TreeMap<String,Chat> UnloadedChats;
    private static TreeMap<String,Chat> Chats;
    public static void Initialize(Controller controller, ChatLocalStorageInterface chatLocalStorageInterface) {
        ChatListChanged = new Event<EventArgs>();
        if (Chat.UnloadedChats == null) {
            Chat.UnloadedChats = new TreeMap<String, Chat>();
        }
        if (Chat.Chats == null) {
            Chat.Chats = new TreeMap<String, Chat>();
        }

        Chat.controller = controller;
        Chat.localStorage = chatLocalStorageInterface;

        DataProvider.GetDataProvider().onChatProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(Chat.class, "onFormatReceived", FormatReceivedEventArgs.class));


        //Remote recovering of groups -> Recovered when binding app or service.
/*        if (DataProvider.isConnectionAvailable()) {
            DataProvider.GetDataProvider().InvokeServerCommand(ServerCommand.AvailableCommands.ChatList,null);
        }*/
    }

    public static void RecoverLocalChats() {
        if ((Chat.localStorage == null) || (Chat.controller == null)) throw new IllegalStateException("Chats must be initialized");

        //Local recovering of groups.
        String[] groups = Chat.localStorage.RecoverGroups();
        if (groups != null) {
            for (String group : groups) {
                Format[] formats = Format.getFormat((new JsonParser()).parse(group));
                for (Format format : formats) {
                    if (format instanceof CHAT) {
                        Chat g = new Chat((CHAT) format, null);
                        if (g.isLoaded()) {
                            Chat.Chats.put(g.channelUnicode, g);
                            if (ChatListChanged != null)
                                ChatListChanged.fire(g, EventArgs.Empty());
                        }
                        else
                            Chat.UnloadedChats.put(g.channelUnicode,g);
                    }
                }
            }
        }
    }

    public static Chat CreateChat(User user,Hive hive, EventHandler<CommandCallbackEventArgs> Callback) {
        Chat result = null;
        if ((user == null) || ((hive == null) && (user.getUserPrivateProfile() == null)) || ((hive != null) && (user.getUserPublicProfile() == null))) return result;
        Format[] formats = null;

        ArrayList<Chat> knownChats = new ArrayList<Chat>(Chat.UnloadedChats.values());
        knownChats.addAll(Chat.Chats.values());

        if (hive == null) {
            PROFILE_ID profile_id = new PROFILE_ID();
            profile_id.USER_ID = user.getUserID();
            profile_id.PROFILE_TYPE = "BASIC_PRIVATE";

            formats = new Format[]{profile_id};

            for (Chat c : knownChats) {
                if (c.getChatKind() == ChatKind.PRIVATE_SINGLE) {
                   try {
                       if (c.getMember(user.getUserID()) != null) {
                           List<User> users = new ArrayList<User>(c.getMembers());
                           users.remove(user);
                           users.remove(controller.getMe());
                           if ((users.isEmpty()) || ((users.size() == 1) && (users.get(0).getUserID().equalsIgnoreCase(controller.getMe().getUserID())))) {
                               result = c;
                               break;
                           }
                       }
                   } catch (Exception e) { continue; }
                }
            }

        } else {
            PROFILE_ID profile_id = new PROFILE_ID();
            profile_id.USER_ID = user.getUserID();
            profile_id.PROFILE_TYPE = "BASIC_PUBLIC";

            HIVE_ID hive_id = (HIVE_ID)hive.toFormat(new HIVE_ID());

            formats = new Format[]{profile_id, hive_id};

            for (Chat c : knownChats) {
                if (c.getChatKind() == ChatKind.PUBLIC_SINGLE) {
                    try {
                        if ((c.getParentHive().getNameUrl().equalsIgnoreCase(hive.getNameUrl())) && (c.getMember(user.getUserID()) != null)) {
                            List<User> users = new ArrayList<User>(c.getMembers());
                            users.remove(user);
                            users.remove(controller.getMe());
                            if ((users.isEmpty()) || ((users.size() == 1) && (users.get(0).getUserID().equalsIgnoreCase(controller.getMe().getUserID())))) {
                                result = c;
                                break;
                            }
                        }
                    } catch (Exception e) { continue; }
                }
            }
        }

        if ((result == null) && (formats != null)) {
            result = new Chat(hive,user);
            result.OnChatCreated.add(Callback);

            controller.getDataProvider().RunCommand(AvailableCommands.CreateChat,new EventHandler<CommandCallbackEventArgs>(result,"OnChatCreatedCallback",CommandCallbackEventArgs.class),formats);
        }

        return result;
    }

    public Boolean isLoaded() {
        Boolean result = false;
        switch (this.getChatKind()) {
            case HIVE:
                result = ((this.parentHive != null) && (Hive.HiveIsLoaded(this.parentHive)) && (this.getConversation() != null));
                break;
            case PUBLIC_SINGLE:
                result = ((this.getConversation() != null) && (this.parentHive != null) && (Hive.HiveIsLoaded(this.parentHive)) && (this.members != null));
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
                result = ((this.getConversation() != null) && (this.members != null));
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
            UnloadedChats.remove(this.channelUnicode);
            Chats.put(this.channelUnicode, this);
            if (ChatListChanged != null)
                ChatListChanged.fire(this, EventArgs.Empty());
        }
    }

    /***********************************/
    /*        STATIC LIST SUPPORT      */
    /***********************************/

    public static Chat getChatByIndex(int index) {
        return Chats.values().toArray(new Chat[Chats.size()])[index];
    }
    public static Collection<Chat> getChats() {
        return Chats.values();
    }
    public static int getChatCount() {
        return Chats.size();
    }

    /*****************************************
                 Constructor
     *****************************************/
    public Chat(Format format, Hive hive) {
        this.OnContextLoaded = new Event<EventArgs>();
        this.OnChatCreated = new Event<CommandCallbackEventArgs>();
        this.members = new TreeMap<String, User>();
        this.conversation = new Conversation(this);
        if (hive != null)
            this.parentHive = hive;
        this.fromFormat(format);
    }

    protected Chat(String channelUnicode) {
        this.OnContextLoaded = new Event<EventArgs>();
        this.OnChatCreated = new Event<CommandCallbackEventArgs>();
        this.members = new TreeMap<String, User>();
        String localGroup = Chat.localStorage.RecoverGroup(channelUnicode);
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
                        this.chatKind = ChatKind.valueOf(data.CHAT_TYPE);
                        if ((data.PARENT_HIVE != null) && (data.PARENT_HIVE.NAME_URL != null) && (!data.PARENT_HIVE.NAME_URL.isEmpty()))
                            this.parentHive = Hive.getHive(data.PARENT_HIVE.NAME_URL);
                        else
                            this.parentHive = null;
                        this.pusherChannel = data.PUSHER_CHANNEL;
                        this.conversation = new Conversation(this);
                        break;
                    }
                }
        }
        if ((this.channelUnicode == null) || (!this.channelUnicode.equals(channelUnicode))) {
            this.channelUnicode = channelUnicode;
            this.conversation = new Conversation(this);
            DataProvider.GetDataProvider().InvokeServerCommand(AvailableCommands.ChatInfo,this.toFormat(new CHAT_ID()));
        }
    }

    public Chat(ChatKind chatKind, Hive parentHive) {
        this.OnChatCreated = new Event<CommandCallbackEventArgs>();
        if ((chatKind != ChatKind.PRIVATE_SINGLE) && (chatKind != ChatKind.PRIVATE_GROUP) && (parentHive == null))
            throw new IllegalArgumentException("If chat is not private, parentHive CAN'T be null.");

        this.OnContextLoaded = new Event<EventArgs>();
        this.members = new TreeMap<String, User>();

        this.chatKind = chatKind;
        this.parentHive = parentHive;
        this.conversation = new Conversation(this);
        this.creationDate = new Date();
    }

    private Chat(Hive parentHive, User user) {
        this.OnChatCreated = new Event<CommandCallbackEventArgs>();
        this.members = new TreeMap<String, User>();
        if (parentHive != null) {
            this.parentHive = parentHive;
            this.chatKind = ChatKind.PUBLIC_SINGLE;
        } else {
            this.chatKind = ChatKind.PRIVATE_SINGLE;
        }
        this.conversation = new Conversation(this);

    }

    public static Chat getChat(String channelUnicode) {
        return Chat.getChat(channelUnicode, true);
    }

    public static Chat getChat(String channelUnicode, Boolean addToList) {
        if ((Chat.UnloadedChats == null) || (Chat.Chats == null)) throw new IllegalStateException("Chats must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Chat.UnloadedChats.containsKey(channelUnicode))
            return Chat.UnloadedChats.get(channelUnicode);
        else if (Chat.Chats.containsKey(channelUnicode))
            return Chat.Chats.get(channelUnicode);
        else if (addToList) {
            Chat g = new Chat(channelUnicode);
            if (g.isLoaded()) {
                Chat.Chats.put(g.channelUnicode, g);
                if (ChatListChanged != null)
                    ChatListChanged.fire(g,EventArgs.Empty());
            }
            else
                Chat.UnloadedChats.put(g.channelUnicode,g);
            return g;
        } else {
            return null;
        }
    }

    public static Chat getChat(Format format) {
        Chat g = new Chat(format,null);
        if ((g.channelUnicode != null) && (!g.channelUnicode.isEmpty())) {
            Chat existent = Chat.getChat(g.channelUnicode, false);
            if (existent == null) {
                if (g.isLoaded()) {
                    Chat.Chats.put(g.channelUnicode, g);
                    if (ChatListChanged != null)
                        ChatListChanged.fire(g,EventArgs.Empty());
                } else
                    Chat.UnloadedChats.put(g.channelUnicode, g);
                if (ChatListChanged != null)
                    ChatListChanged.fire(g,EventArgs.Empty());
                return g;
            } else {
                return existent;
            }
        } else {
            throw new IllegalArgumentException("Specified format is not correct.");
        }
    }

    public static Chat createChat(Collection<User> users, String parentGroup) {
        //TODO: implement server communication
        String groupChannelUnicode = ""; //Recovered from server.
        //TODO: implement local storage

        Chat g = new Chat(groupChannelUnicode);
        if (g.isLoaded()) {
            Chat.Chats.put(groupChannelUnicode, g);
            if (ChatListChanged != null)
                ChatListChanged.fire(g,EventArgs.Empty());
        } else
            Chat.UnloadedChats.put(groupChannelUnicode, g);
        return g;
    }

    public static void removeChat(String channelUnicode) {
        if ((Chat.UnloadedChats == null) || (Chat.Chats == null)) throw new IllegalStateException("Chats must be initialized.");
        else if (channelUnicode == null) throw new NullPointerException("ChannelUnicode must not be null.");
        else if (channelUnicode.isEmpty()) throw  new IllegalArgumentException("ChannelUnicode must not be empty.");

        if (Chat.UnloadedChats.containsKey(channelUnicode)) {
            Chat g = Chat.UnloadedChats.get(channelUnicode);
            g.conversation.clearAllMessages();
            Chat.UnloadedChats.remove(channelUnicode);
        } else if (Chat.Chats.containsKey(channelUnicode)) {
            Chat g = Chat.Chats.get(channelUnicode);
            g.conversation.clearAllMessages();
            Chat.Chats.remove(channelUnicode);
            if (ChatListChanged != null)
                ChatListChanged.fire(g,EventArgs.Empty());
        }

        Chat.localStorage.RemoveGroup(channelUnicode);
    }
    public static void clearChats() {
        if ((Chat.UnloadedChats == null) || (Chat.Chats == null)) throw new IllegalStateException("Chats must be initialized.");

        for (Chat chat : Chat.UnloadedChats.values())
            if (chat.conversation != null)
                chat.conversation.clearAllMessages();
        Chat.UnloadedChats.clear();

        for (Chat chat : Chat.Chats.values())
            if (chat.conversation != null)
                chat.conversation.clearAllMessages();
        Chat.Chats.clear();

        Chat.localStorage.ClearGroups();

        if (ChatListChanged != null)
            ChatListChanged.fire(null,EventArgs.Empty());
    }

    /***********************************/
    /*        STATIC CALLBACKS         */
    /***********************************/

    public static void onFormatReceived(Object sender, FormatReceivedEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if (format instanceof CHAT) {
                    Chat.getChat(((CHAT) format).CHANNEL_UNICODE).fromFormat(format);
                } else if (format instanceof CHAT_ID) {
                    Chat.getChat(((CHAT_ID) format).CHANNEL_UNICODE).fromFormat(format);
                } else if  (format instanceof CHAT_SYNC) {
                    Chat.getChat(((CHAT_SYNC) format).CHANNEL_UNICODE).fromFormat(format);
                }
            }
        }
    }

    /*****************************************
                users list
     *****************************************/
    protected TreeMap<String,User> members;

    public List<User> getMembers() {
        if ((this.members == null) || (this.members.isEmpty())) //throw new NullPointerException("There are no members for this group.");
            return new ArrayList<User>();

        return Collections.unmodifiableList(new ArrayList<User>(this.members.values()));
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

    /******************************************
     *      IContextualizable
     ******************************************/
    public Event<EventArgs> OnContextLoaded;

    private ArrayList<Message> sharedImages;
    private ArrayList<Message> topBuzzes;

    @Override
    public Event<EventArgs> getOnContextLoaded() {
        return this.OnContextLoaded;
    }

    public void contextLoadedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        //TODO: process received data

        CONTEXT context = null;
        Boolean requestOK = false;

        ArrayList<Format> received = eventArgs.getReceivedFormats();
        for (Format format : received) {
            if ((format instanceof COMMON) && (((COMMON) format).STATUS.equalsIgnoreCase("OK")))
                requestOK = true;
            else if (format instanceof CONTEXT)
                context = (CONTEXT)format;
        }


        if ((requestOK) && (context != null)) {
            if ((this.getParentHive() != null) && (context.NEW_USERS_LIST != null) && (!context.NEW_USERS_LIST.isEmpty()))
                this.parentHive.setContextUsers(context.NEW_USERS_LIST);

            if ((context.SHARED_IMAGES_LIST != null) && (!context.SHARED_IMAGES_LIST.isEmpty())) {
                if (this.sharedImages == null)
                    this.sharedImages = new ArrayList<Message>();

                boolean sharedImagesListChanged = false;
                for (MESSAGE message : context.SHARED_IMAGES_LIST) {
                    try {
                        Message m = new Message(message);
                        sharedImagesListChanged = this.sharedImages.add(m) || sharedImagesListChanged;
                    } catch (Exception e) {
                    }
                }
            }

            if ((this.getParentHive() != null) && (context.TOP_BUZZES_LIST != null) && (!context.TOP_BUZZES_LIST.isEmpty())) {
                if (this.topBuzzes == null)
                    this.topBuzzes = new ArrayList<Message>();

                boolean topBuzzesListChanged = false;
                for (MESSAGE message : context.TOP_BUZZES_LIST) {
                    try {
                        Message m = new Message(message);
                        topBuzzesListChanged = this.topBuzzes.add(m) || topBuzzesListChanged;
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (this.OnContextLoaded != null)
            this.OnContextLoaded.fire(this, EventArgs.Empty());
    }

    @Override
    public void loadContext(int numberImages, int numberNewUsers, int numberBuzzes) {
        CONTEXT context = new CONTEXT();
        context.CHANNEL_UNICODE = this.channelUnicode;
        context.IMAGES_COUNT = numberImages;
        context.NEW_USERS_COUNT = numberNewUsers;
        context.BUZZES_COUNT = numberBuzzes;
        Controller.GetRunningController().getDataProvider().InvokeServerCommand(AvailableCommands.ChatContext,new EventHandler<CommandCallbackEventArgs>(this,"contextLoadedCallback",CommandCallbackEventArgs.class),context);
    }

    @Override
    public ContextElement getCommunityContext() {
        //TODO: implement this method to get community info.
        return null;
    }

    @Override
    public ContextElement getBaseContext() {
        Chat chat = this;
        Hive hive = null;
        switch (this.chatKind) {
            case PUBLIC_SINGLE:
                for (User user : this.members.values())
                    if ((!user.isMe()) && (user.getUserPublicProfile() != null))
                            return new ContextElement(ContextElement.ElementType.PublicUser,user,user.getUserPublicProfile().getProfileImage(),user.getUserPublicProfile().getShowingName(),user.getUserPublicProfile().getStatusMessage());
                break;
            case PRIVATE_SINGLE:
                for (User user : this.members.values())
                    if ((!user.isMe()) && (user.getUserPrivateProfile() != null))
                        return new ContextElement(ContextElement.ElementType.PrivateUser,user,user.getUserPrivateProfile().getProfileImage(),user.getUserPrivateProfile().getShowingName(),user.getUserPrivateProfile().getStatusMessage());
                break;
            case PUBLIC_GROUP:
                break;
            case PRIVATE_GROUP:
                break;
            case HIVE:
                if (this.parentHive != null)
                    return new ContextElement(this.parentHive,this.parentHive.getHiveImage(),this.parentHive.getName(),this.parentHive.getDescription());
                break;
        }
        return null;
    }

    @Override
    public ContextElement getParentContext() {
        if ((this.chatKind == ChatKind.PUBLIC_SINGLE) || (this.chatKind == ChatKind.PUBLIC_GROUP)) {
            return new ContextElement(this.parentHive,this.parentHive.getHiveImage(),this.parentHive.getName());
        } else
            return null;
    }

    @Override
    public List<ContextElement> getPublicChats() {
        //TODO: implement this method to get other public chats in communities.
        return null;
    }

    @Override
    public List<Message> getSharedImages() {
        if (this.sharedImages != null)
            return Collections.unmodifiableList(this.sharedImages);
        else
            return null;
    }

    @Override
    public List<User> getNewUsers() {
        if (this.chatKind == ChatKind.HIVE)
            return this.parentHive.getContextUsers();
        else
            return null;
    }

    @Override
    public List<User> getUsers() {
        ArrayList<User> result = new ArrayList<User>();
        if ((this.members != null) && (!this.members.isEmpty()))
            for (User user : this.members.values())
                if (!user.isMe())
                    result.add(user);

        return Collections.unmodifiableList(result);
    }

    @Override
    public List<Message> getTrendingBuzzes() {
        if ((this.chatKind == ChatKind.HIVE) && (this.topBuzzes != null))
            return Collections.unmodifiableList(this.topBuzzes);
        else
            return null;
    }

    @Override
    public List<ContextElement> getOtherChats() {
        return null;
    }

    public Event<CommandCallbackEventArgs> OnChatCreated;
    public void OnChatCreatedCallback(Object sender, CommandCallbackEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if ((format instanceof CHAT) || (format instanceof CHAT_ID) || (format instanceof CHAT_SYNC)) {
                    if (this.fromFormat(format)) {
                        if (!Chats.containsKey(this.channelUnicode)) {
                            Chats.put(this.channelUnicode,this);
                            if (ChatListChanged != null)
                                ChatListChanged.fire(this,EventArgs.Empty());
                        }
                    }
                }
            }
        }

        if (this.OnChatCreated != null)
            this.OnChatCreated.fire(this,args);
    }
    /*****************************************
           context (group shared files, ...)
     *****************************************/

    protected String channelUnicode;
    protected Conversation conversation;
    protected Date creationDate;
    protected String description;
    protected ChatKind chatKind;
    protected String name;
    protected Hive parentHive;
    protected String pusherChannel;

    public String getChannelUnicode() { return this.channelUnicode; }
    public void setChannelUnicode(String value) { this.channelUnicode = value; }

    public Conversation getConversation() { return this.conversation; }

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


    private void CalculateChatKind() {
        if (this.getParentHive() == null) {
            if (this.members.size() < 3)
                this.chatKind = ChatKind.PRIVATE_SINGLE;
            else
                this.chatKind = ChatKind.PRIVATE_GROUP;
        } else {
            if ((this.members == null) || (this.members.size() == 0))
                this.chatKind = ChatKind.HIVE;
            else if (this.members.size() < 3)
                this.chatKind = ChatKind.PUBLIC_SINGLE;
            else
                this.chatKind = ChatKind.PUBLIC_GROUP;
        }
    }
    public ChatKind getChatKind() {
        if (this.chatKind == null)
            this.CalculateChatKind();
        return this.chatKind;
    }
    public void setChatKind(ChatKind value) {
        this.chatKind = value;
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
            ((CHAT) format).CHAT_TYPE = this.chatKind.toString();
            if ((this.members != null) && (this.members.size() > 0)) {
                ((CHAT) format).MEMBERS = new ArrayList<PROFILE_ID>();
                for (User user : this.members.values()) {
                    PROFILE_ID profile_id = new PROFILE_ID();
                    profile_id.USER_ID = user.getUserID();
                    profile_id.PROFILE_TYPE = "BASIC_" + ((this.chatKind.toString().startsWith("PRIVATE_"))?"PRIVATE":"PUBLIC");
                    ((CHAT) format).MEMBERS.add(profile_id);
                }
            }
        } else if (format instanceof CHAT_ID) {
            ((CHAT_ID) format).CHANNEL_UNICODE = this.channelUnicode;

        } else if (format instanceof CHAT_SYNC) {
            ((CHAT_SYNC) format).CHANNEL_UNICODE = this.channelUnicode;

            ((CHAT_SYNC) format).LAST_MESSAGE = (MESSAGE)this.conversation.getLastMessage().toFormat(new MESSAGE());
        }

        return format;
    }
    public Boolean fromFormat(Format format) {
        if (format instanceof CHAT) {
            this.channelUnicode = ((CHAT) format).CHANNEL_UNICODE;
            this.creationDate = ((CHAT) format).CREATION_DATE;
            this.pusherChannel = ((CHAT) format).PUSHER_CHANNEL;
            this.chatKind = ChatKind.valueOf(((CHAT) format).CHAT_TYPE);
            if (((CHAT) format).MEMBERS != null) {
                this.members = new TreeMap<String, User>();
                for (PROFILE_ID profile_id : ((CHAT) format).MEMBERS)
                    this.addMember(controller.getUser(profile_id));
            }
            if (((CHAT) format).PARENT_HIVE != null)
                if ((this.parentHive == null) || (!this.parentHive.getNameUrl().equalsIgnoreCase(((CHAT) format).PARENT_HIVE.NAME_URL)))
                    this.parentHive = Hive.getHive(((CHAT) format).PARENT_HIVE.NAME_URL);

            if (this.conversation == null)
                this.conversation = new Conversation(this);
            //this.CalculateChatKind();
            return true;
        } else if (format instanceof CHAT_ID) {
            this.channelUnicode = ((CHAT_ID) format).CHANNEL_UNICODE;
            if (this.conversation == null)
                this.conversation = new Conversation(this);

            return true;
        } else if (format instanceof CHAT_SYNC) {
            this.channelUnicode = ((CHAT_SYNC) format).CHANNEL_UNICODE;
            if (this.conversation == null)
                this.conversation = new Conversation(this);
            this.conversation.addMessage(new Message(((CHAT_SYNC) format).LAST_MESSAGE));

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
