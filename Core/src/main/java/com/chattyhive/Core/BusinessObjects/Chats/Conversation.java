package com.chattyhive.Core.BusinessObjects.Chats;

import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.OLD.MessageLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_INTERVAL;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_LIST;
import com.chattyhive.Core.Util.Events.ChannelEventArgs;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Events.FormatReceivedEventArgs;
import com.chattyhive.Core.Util.Formatters.DateFormatter;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 12/06/2014.
 */
public class Conversation { //TODO: implements SortedSet<Message>
    /**************************
       Static conversation management
     **************************/
    private static MessageLocalStorageInterface localStorage;
    private static Controller controller;

    public static void Initialize(Controller controller, MessageLocalStorageInterface messageLocalStorageInterface) {
        Conversation.controller = controller;
        Conversation.localStorage = messageLocalStorageInterface;

        DataProvider.GetDataProvider().onMessageReceived.add(new EventHandler<FormatReceivedEventArgs>(Conversation.class, "onFormatReceived", FormatReceivedEventArgs.class));
    }

    /***********************************/
    /*        STATIC CALLBACKS         */
    /***********************************/

    public static void onFormatReceived(Object sender, FormatReceivedEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if (format instanceof MESSAGE) {
                    Chat.getChat(((MESSAGE) format).CHANNEL_UNICODE).getConversation().addMessage(new Message(format));
                } else if (format instanceof MESSAGE_LIST) {
                    if (((MESSAGE_LIST) format).NUMBER_MESSAGES > 0)
                        Conversation.onFormatReceived(sender, new FormatReceivedEventArgs(((MESSAGE_LIST) format).MESSAGES));
                }
            }
        }
    }

    /**************************
       Proper conversation management
     **************************/

    private Boolean chatWindowActive;
    private Boolean moreMessages;
    private Chat parent;
    private int showingIndex;

    public Chat getParent() { return this.parent; }
    public void setParent(Chat value) { this.parent = value; }

    public void setChatWindowActive(Boolean value) {
        if (this.chatWindowActive == value) return;

        this.chatWindowActive = value;
        if (value) {
            this.showingIndex = this.messages.size() - 100;
            controller.Join(this.parent.pusherChannel);
            this.loadMessages(-1);
        }
        else {
            controller.Leave(this.parent.pusherChannel);
            this.showingIndex = -1;
        }
    }

    public Boolean hasMoreMessages() { return this.moreMessages; }

    public void loadMessages(int lastIndex) {
        String last = "LAST";
        String start = "0";
        if (lastIndex > -1) {
            last = String.format("%d", lastIndex);
            start = String.format("%d", ((lastIndex-100) > 0)?lastIndex-100:0 );
        }

        CHAT_ID chat_id = new CHAT_ID();
        chat_id.CHANNEL_UNICODE = this.getParent().getChannelUnicode();

        MESSAGE_INTERVAL message_interval = new MESSAGE_INTERVAL();
        message_interval.LAST_MESSAGE_ID = last;
        message_interval.COUNT = 100;
        message_interval.START_MESSAGE_ID = start;

        controller.getDataProvider().InvokeServerCommand(AvailableCommands.GetMessages,chat_id,message_interval);
    }
    public void unloadMessages() {
        Message lastMessage = this.getLastMessage();
        SortedSet<Message> tail = this.messages.tailSet(lastMessage,true);
        this.messagesByID.clear();
        this.messages.clear();
        this.messagesByID.put(lastMessage.getId(),lastMessage);
        this.messages.addAll(tail);
    }

    /*****************************************
                    Constructor
     *****************************************/
    public Conversation(Chat parent) {
        this.MessageListModifiedEvent = new Event<EventArgs>();

        this.messages = new TreeSet<Message>();
        this.messagesByID = new TreeMap<String, Message>();

        this.parent = parent;

        this.chatWindowActive = false;
        this.showingIndex = 0;
        this.moreMessages = true;
    }

    /*****************************************
                  Message lists
     *****************************************/
    public void loadPendingMessages() {
        String[] messages = Conversation.localStorage.RecoverMessages(this.parent.pusherChannel);

        //TODO: recover local messages.
        //TODO: recover send pending messages.
        //TODO: identify holes.
    }

    private TreeMap<String,Message> messagesByID;
    private TreeSet<Message> messages;

    public Event<EventArgs> MessageListModifiedEvent;

    public void onMessageChanged(Object sender,EventArgs eventArgs) {
        if (sender instanceof Message) {
            Message m = (Message)sender;
            boolean idReceived = false;
            boolean confirmationReceived = false;
            if ((m.getId() != null) && (!m.getId().isEmpty())) {
                idReceived = m.IdReceived.remove(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
                if (!this.messagesByID.containsKey(m.getId()))
                    this.messagesByID.put(m.getId(),m);
            }
            if (m.getConfirmed())
                confirmationReceived = m.ConfirmationReceived.remove(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
            if (this.MessageListModifiedEvent != null)
                this.MessageListModifiedEvent.fire(this, EventArgs.Empty());

            if (idReceived) {
                Conversation.localStorage.RemoveMessage(String.format("Sending-%s", this.parent.pusherChannel),m.getId());
                Conversation.localStorage.StoreMessage(this.parent.pusherChannel,m.getId(), m.toJson(new MESSAGE()).toString());
                if (StaticParameters.MaxLocalMessages > 0)
                    Conversation.localStorage.TrimStoredMessages(this.parent.pusherChannel, StaticParameters.MaxLocalMessages);
            }

            if (confirmationReceived) { //TODO: think about update method.
                Conversation.localStorage.RemoveMessage(this.parent.pusherChannel,m.getId());
                Conversation.localStorage.StoreMessage(this.parent.pusherChannel, m.getId(),m.toJson(new MESSAGE()).toString());
            }
        }
    }

    public void onMessageReceived(Object sender,ChannelEventArgs eventArgs) {
        if ((eventArgs.getChannelName().equalsIgnoreCase(this.parent.pusherChannel)) && (eventArgs.getEventName().equalsIgnoreCase("msg"))) {
            Message m = eventArgs.getMessage();
            this.addMessage(m);
            //TODO: Confirm message received;
        }
    }

    public Message getMessageByID(String ID) {
        if ((this.messagesByID == null) || (this.messagesByID.isEmpty())) throw new NullPointerException("There are no messages for this chat.");
        else if (ID == null) throw new NullPointerException("ID must not be null.");
        else if (ID.isEmpty()) throw  new IllegalArgumentException("ID must not be empty.");

        return this.messagesByID.get(ID);
    }

    public Message getLastMessage() {
        if ((this.messages == null) || (this.messages.isEmpty())) throw new NullPointerException("There are no messages for this chat.");

        for (Message item : this.messages.descendingSet()) {
            if (!item.getMessageContent().getContentType().endsWith("_SEPARATOR")) return item;
        }

        throw new NullPointerException("There are no messages for this chat.");
    }

    public Message getLastSentMessage(User user) {
        if ((this.messages == null) || (this.messages.isEmpty())) throw new NullPointerException("There are no messages for this chat.");

        for (Message item : this.messages.descendingSet()) {
            if ((!item.getMessageContent().getContentType().endsWith("_SEPARATOR")) && (item.getUser().getUserID().equals(user.getUserID()))) return item;
        }

        return null;
    }

    public Message getMessageByIndex(int index) {
        if ((this.messages == null) || (this.messages.isEmpty())) throw new NullPointerException("There are no messages for this chat.");
        if ((index < 0) || (index >= this.messages.size())) throw new ArrayIndexOutOfBoundsException(String.format("Index %d is out of bounds of array with size %d",index,this.messages.size()));
        return this.messages.toArray(new Message[0])[index];
    }

    public int getCount() {
        if (this.messages == null)
            return 0;

        return this.messages.size();
    }

    public void addMessage(Message message) {
        if (message == null) throw new NullPointerException("message must not be null.");

        Message previous = this.messages.floor(message);
        Message next = this.messages.ceiling(message);

        boolean previousNewDay = false;
        boolean nextNewDay = false;

        //if ((previous != null) && (previous.getMessageContent().getContentType().endsWith("_SEPARATOR")))
        if (previous != null)
            previousNewDay = !(DateFormatter.toString(previous.getTimeStamp()).equalsIgnoreCase(DateFormatter.toString(message.getTimeStamp())));
        else if (previous == null)
            previousNewDay = true;

        if ((next != null) && (previous != null) && (next.getMessageContent().getContentType().endsWith("_SEPARATOR")))
            nextNewDay = !(DateFormatter.toString(previous.getTimeStamp()).equalsIgnoreCase(DateFormatter.toString(message.getTimeStamp())));

        if (previousNewDay)
            this.messages.add(new Message(this,DateFormatter.toDate(DateFormatter.toString(message.getTimeStamp()))));

        if (nextNewDay)
            this.messages.add(new Message(this,DateFormatter.toDate(DateFormatter.toString(next.getTimeStamp()))));

        if (message.getId() == null)
            message.IdReceived.add(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
        if ((this.parent.chatType == ChatType.PRIVATE_HIVEMATE) || (this.parent.chatType == ChatType.PRIVATE_FRIEND))
            message.ConfirmationReceived.add(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));

        Boolean messageListModified = this.messages.add(message);


        if ((message.getId() != null) && (!message.getId().isEmpty())) {
            this.messagesByID.put(message.getId(),message);
        }

        if ((messageListModified) && (this.MessageListModifiedEvent != null))
            this.MessageListModifiedEvent.fire(this, EventArgs.Empty());

        if ((messageListModified) && (this.getParent().chatType == ChatType.PUBLIC) && (Hive.HiveListChanged != null))
            Hive.HiveListChanged.fire(null,EventArgs.Empty());

        if ((messageListModified) && (Chat.ChatListChanged != null))
            Chat.ChatListChanged.fire(null,EventArgs.Empty());

        if ((message.getId() != null) && (!message.getId().isEmpty())) {
            if (StaticParameters.MaxLocalMessages != 0) {
                Conversation.localStorage.StoreMessage(this.parent.pusherChannel,message.getId(), message.toJson(new MESSAGE()).toString());
                if (StaticParameters.MaxLocalMessages > 0)
                    Conversation.localStorage.TrimStoredMessages(this.parent.pusherChannel, StaticParameters.MaxLocalMessages);
            }
        } else {
            Conversation.localStorage.StoreMessage(String.format("Sending-%s",this.parent.pusherChannel),message.getId(),message.toJson(new MESSAGE()).toString());
        }
    }

    public void addMessageByID(Message message) {
        if (message == null) throw new NullPointerException("message must not be null.");
        if ((message.getId() == null) ||(message.getId().isEmpty())) throw new NullPointerException("message must have an ID.");

        Boolean messageListModified = this.messages.add(message);

        if (messageListModified)
            this.messagesByID.put(message.getId(),message);

        if ((messageListModified) && (this.MessageListModifiedEvent != null))
            this.MessageListModifiedEvent.fire(this, EventArgs.Empty());
    }

    public void removeMessage(String ID) {
        Message toBeRemoved = this.getMessageByID(ID);
        this.messagesByID.remove(ID);
        this.messages.remove(toBeRemoved);
        if (StaticParameters.MaxLocalMessages != 0)
            Conversation.localStorage.RemoveMessage(this.parent.pusherChannel,toBeRemoved.getId());
    }

    public void clearAllMessages() {
        this.messagesByID.clear();
        this.messages.clear();
        if (StaticParameters.MaxLocalMessages != 0)
            Conversation.localStorage.ClearMessages(this.parent.pusherChannel);
    }

    public void getMoreMessages() {
        //TODO: implement this function. May get into account that local messages have not to be retrieved from server
    }

    public TreeSet<Message> getMessages() {
        return this.messages;
    }
}
