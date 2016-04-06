package com.chattyhive.Core.BusinessObjects.Chats;

import com.chattyhive.Core.BusinessObjects.Chats.Context.IContextualizable;
import com.chattyhive.Core.BusinessObjects.Subscriptions.ISubscribable;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Subscriptions.SubscriberList;
import com.chattyhive.Core.BusinessObjects.Subscriptions.Subscription;
import com.chattyhive.Core.ContentProvider.Formats.COMMON;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_SYNC;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.PROFILE_ID;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 16/06/2014.
 */
public abstract class Chat implements IContextualizable, ISubscribable {

    /**************************************************/
    /* Fields                                         */
    /**************************************************/
    protected boolean loaded = false;
    protected Controller controller;
    protected String chatID;
    protected ChatType type;

    protected String slug;
    protected String pusherChannel;

    protected Date creationDate;
    protected Date lastModified;

    protected Conversation conversation;

    protected SubscriberList<Chat> members;

    protected CallbackDelegate loadChatCallback;

    /**************************************************/
    /* Constructors                                   */
    /**************************************************/
    private Chat(Controller controller) {
        this.controller = controller;

        this.members = new SubscriberList<>();
        this.conversation = new Conversation(this);
    }
    public Chat(Controller controller, Format format) {
        this(controller);

        this.fromFormat(format);
    }
    public Chat(Controller controller, CallbackDelegate callback, String chatID, String accountID) {
        this(controller);
        this.chatID = chatID;
        this.loadChat(callback,accountID);
    }

    /**************************************************/
    /* Load chat                                      */
    /**************************************************/

    private void loadChat(CallbackDelegate callback, String accountID) {
        this.loadChatCallback = callback;
        Command command = new Command(AvailableCommands.ChatInfo,this.toFormat(new CHAT_ID()));
        command.addCallbackDelegate(new CallbackDelegate(this, "onLoadChat", CommandCallbackEventArgs.class));
        this.controller.getDataProvider().runCommand(accountID,command, CommandQueue.Priority.RealTime);
    }
    public void onLoadChat(CommandCallbackEventArgs eventArgs) {
        //TODO: load from received formats
        CHAT data = null;
        Boolean receivedOK = false;

        ArrayList<Format> received = eventArgs.getReceivedFormats();
        for (Format format : received) {
            if ((format instanceof COMMON) && (((COMMON) format).STATUS.equalsIgnoreCase("OK")))
                receivedOK = true;
            else if (format instanceof CHAT)
                data = (CHAT)format;
        }

        if (receivedOK && (data != null)) {
            this.fromFormat(data);

            if (this.loadChatCallback != null)
                this.loadChatCallback.Run(this,eventArgs);
        }
    }

    /**************************************************/
    /* Subscribers list                               */
    /**************************************************/

    public SubscriberList<Chat> getSubscriptions() {
        return this.members;
    }
    public Subscription<Chat> getMemberSubscription(String identifier) {
        if ((this.members == null) || (this.members.isEmpty())) throw new NullPointerException("There are no members for this group.");
        else if (identifier == null) throw new NullPointerException("Identifier must not be null.");
        else if (identifier.isEmpty()) throw  new IllegalArgumentException("Identifier must not be empty.");

        return this.members.get(identifier);
    }
    public void addMemberSubscription(Subscription<Chat> subscription) {
        if (subscription == null) throw new NullPointerException("Subscription must not be null.");

        if (this.members == null)
            this.members = new SubscriberList<>();

        this.members.add(subscription);
    }

    public void requestMembers() {
        //TODO: implement server request (NOT AVAILABLE)
    }
    public void inviteMember(String identifier) {
        //TODO: implement server request (NOT AVAILABLE)
    }

    /**************************************************/
    /* Getters and Setters                            */
    /**************************************************/

    public String getID() { return this.chatID; }
    public void setID(String value) { this.chatID = value; }

    public Conversation getConversation() { return this.conversation; }

    public Date getCreationDate() { return this.creationDate; }
    public void setCreationDate(Date value) { this.creationDate = value; }

    public Date getLastModified() { return this.lastModified; }
    public void setLastModified(Date value) { this.lastModified = value; }

    public ChatType getChatType() {
        return this.type;
    }
    public void setChatType(ChatType value) {
        this.type = value;
    }

    public String getPusherChannel() { return this.pusherChannel; }
    public void setPusherChannel(String value) { this.pusherChannel = value; }

    public String getSlug() { return this.slug; }
    public void setSlug(String value) { this.slug = value; }

    public boolean isLoaded() {
        return this.loaded;
    }
    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/
    public Format toFormat(Format format) {
        if (format instanceof CHAT) {
            ((CHAT) format).CHANNEL_UNICODE = this.chatID;

            ((CHAT) format).CREATION_DATE = this.creationDate;
            ((CHAT) format).PUSHER_CHANNEL = this.pusherChannel;
            ((CHAT) format).CHAT_TYPE = this.type.toString();
            if ((this.members != null) && (this.members.size() > 0)) { // FIXME: Subscription list format
                ((CHAT) format).MEMBERS = new ArrayList<PROFILE_ID>();
                for (Subscription<Chat> subscription : this.members.values()) {
                    PROFILE_ID profile_id = new PROFILE_ID();
                    profile_id.USER_ID = subscription.getUser().getUserID();
                    profile_id.PROFILE_TYPE = "BASIC_" + ((this.type.toString().contains("FRIEND"))?"PRIVATE":"PUBLIC");
                    ((CHAT) format).MEMBERS.add(profile_id);
                }
            }
        } else if (format instanceof CHAT_ID) {
            ((CHAT_ID) format).CHANNEL_UNICODE = this.chatID;

        } else if (format instanceof CHAT_SYNC) {
            ((CHAT_SYNC) format).CHANNEL_UNICODE = this.chatID;

            ((CHAT_SYNC) format).LAST_MESSAGE = (MESSAGE)this.conversation.getLastMessage().toFormat(new MESSAGE());
        }

        return format;
    }
    public Boolean fromFormat(Format format) {
        if (format instanceof CHAT) {
            this.chatID = ((CHAT) format).CHANNEL_UNICODE;
            this.creationDate = ((CHAT) format).CREATION_DATE;
            this.pusherChannel = ((CHAT) format).PUSHER_CHANNEL;
            this.type = ChatType.valueOf(((CHAT) format).CHAT_TYPE);
            if (((CHAT) format).MEMBERS != null) {
                this.members = new SubscriberList<>();
                for (PROFILE_ID profile_id : ((CHAT) format).MEMBERS) {
                    Subscription<Chat> subscription = new Subscription<>();
                    subscription.setUser(this.controller.getUser(profile_id.USER_ID));
                    subscription.setSubscribable(this);
                    this.addMemberSubscription(subscription);
                }
            }

            if (this.conversation == null)
                this.conversation = new Conversation(this);

            this.loaded = true;

            return true;
        } else if (format instanceof CHAT_ID) {
            this.chatID = ((CHAT_ID) format).CHANNEL_UNICODE;
            if (this.conversation == null)
                this.conversation = new Conversation(this);

            return true;
        } else if (format instanceof CHAT_SYNC) {
            this.chatID = ((CHAT_SYNC) format).CHANNEL_UNICODE;
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
