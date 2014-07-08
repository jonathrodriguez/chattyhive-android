package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Chats.Messages.AbstractMessageItem;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageSeparator;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.DateFormatter;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 12/06/2014.
 */
public class Chat {
    /**************************
       Static chat management
     **************************/
    private static MessageLocalStorageInterface localStorage;
    private static Controller controller;

    public static void Initialize(Controller controller, MessageLocalStorageInterface messageLocalStorageInterface) {
        Chat.controller = controller;
        Chat.localStorage = messageLocalStorageInterface;
    }

    /**************************
       Proper chat management
     **************************/
    private Boolean chatWindowActive;
    private Boolean moreMessages;
    private Group parent;
    private int showingIndex;

    public Group getParent() { return this.parent; }
    public void setParent(Group value) { this.parent = value; }



    public void setChatWindowActive(Boolean value) {
        if (this.chatWindowActive == value) return;

        this.chatWindowActive = value;
        if (value) {
            this.showingIndex = this.messages.size() - 100;
            this.controller.Join(this.parent.pusherChannel);
        }
        else {
            this.controller.Leave(this.parent.pusherChannel);
            this.showingIndex = -1;
        }
    }

    public Boolean hasMoreMessages() { return this.moreMessages; }

    /*****************************************
                    Constructor
     *****************************************/
    private Chat(String channelName) {
        this.messages = new TreeSet<AbstractMessageItem>();
        this.messagesByID = new TreeMap<String, Message>();

        String[] messages = Chat.localStorage.RecoverMessages(channelName);

        //TODO: recover local messages.
        //TODO: recover send pending messages.
        //TODO: identify holes.
    }

    /*****************************************
                  Message lists
     *****************************************/
    private TreeMap<String,Message> messagesByID;
    private TreeSet<AbstractMessageItem> messages;

    private Event<EventArgs> messageListModifiedEvent;
    public void subscribeMessageListModified(EventHandler<EventArgs> eventHandler) {
        if (this.messageListModifiedEvent == null)
            this.messageListModifiedEvent = new Event<EventArgs>();

        this.messageListModifiedEvent.add(eventHandler);
    }
    public void unsubscribeMessageListModified(EventHandler<EventArgs> eventHandler) {
        if (this.messageListModifiedEvent != null) {
            this.messageListModifiedEvent.remove(eventHandler);
            if (this.messageListModifiedEvent.count() == 0)
                this.messageListModifiedEvent = null;
        }
    }
    private void onMessageChanged(Object sender,EventArgs eventArgs) {
        if (sender instanceof Message) {
            Message m = (Message)sender;
            boolean idReceived = false;
            boolean confirmationReceived = false;
            try {
                if ((m.getId() != null) && (!m.getId().isEmpty())) {
                    idReceived = m.unsubscribeIdReceived(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
                    if (!this.messagesByID.containsKey(m.getId()))
                        this.messagesByID.put(m.getId(),m);
                }
                if (m.getConfirmed())
                    confirmationReceived = m.unsubscribeConfirmationReceived(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (this.messageListModifiedEvent != null)
                this.messageListModifiedEvent.fire(this, EventArgs.Empty());

            if (idReceived) {
                Chat.localStorage.RemoveMessage(String.format("Sending-%s",this.parent.pusherChannel),m.toJson().toString());
                Chat.localStorage.StoreMessage(this.parent.pusherChannel, m.toJson().toString());
                if (StaticParameters.MaxLocalMessages > 0)
                    Chat.localStorage.TrimStoredMessages(this.parent.pusherChannel, StaticParameters.MaxLocalMessages);
            }

            if (confirmationReceived) { //TODO: think about update method.
                Chat.localStorage.RemoveMessage(this.parent.pusherChannel,m.toJson().toString());
                Chat.localStorage.StoreMessage(this.parent.pusherChannel, m.toJson().toString());
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

        for (AbstractMessageItem item : this.messages.descendingSet()) {
            if(item instanceof  Message) return (Message)item;
        }

        throw new NullPointerException("There are no messages for this chat.");
    }

    public AbstractMessageItem getMessageByIndex(int index) {
        if ((this.messages == null) || (this.messages.isEmpty())) throw new NullPointerException("There are no messages for this chat.");
        if ((index < 0) || (index >= this.messages.size())) throw new ArrayIndexOutOfBoundsException(String.format("Index %d is out of bounds of array with size %d",index,this.messages.size()));
        return this.messages.toArray(new AbstractMessageItem[0])[index];
    }

    public void addMessage(Message message) {
        if (message == null) throw new NullPointerException("message must not be null.");

        boolean newDay = !(DateFormatter.toString(this.getLastMessage().getTimeStamp()).equalsIgnoreCase(DateFormatter.toString(message.getTimeStamp())));

        if (newDay)
            this.messages.add(new MessageSeparator(message.getTimeStamp()));

        try {
            if (message.getId() == null)
                message.subscribeIdReceived(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
            if ((this.parent.groupKind == GroupKind.PUBLIC_SINGLE) || (this.parent.groupKind == GroupKind.PRIVATE_SINGLE))
                message.subscribeConfirmationReceived(new EventHandler<EventArgs>(this, "onMessageChanged", EventArgs.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Boolean messageListModified = this.messages.add(message);


        if ((message.getId() != null) && (!message.getId().isEmpty())) {
            this.messagesByID.put(message.getId(),message);
        }

        if ((messageListModified) && (this.messageListModifiedEvent != null))
            this.messageListModifiedEvent.fire(this, EventArgs.Empty());

        if ((message.getId() != null) && (!message.getId().isEmpty())) {
            if (StaticParameters.MaxLocalMessages != 0) {
                Chat.localStorage.StoreMessage(this.parent.pusherChannel, message.toJson().toString());
                if (StaticParameters.MaxLocalMessages > 0)
                    Chat.localStorage.TrimStoredMessages(this.parent.pusherChannel, StaticParameters.MaxLocalMessages);
            }
        } else {
            Chat.localStorage.StoreMessage(String.format("Sending-%s",this.parent.pusherChannel),message.toJson().toString());
        }

    }

    public void removeMessage(String ID) {
        Message toBeRemoved = this.getMessageByID(ID);
        this.messagesByID.remove(ID);
        this.messages.remove(toBeRemoved);
        if (StaticParameters.MaxLocalMessages != 0)
            Chat.localStorage.RemoveMessage(this.parent.pusherChannel,toBeRemoved.toJson().toString());
    }

    public void getMoreMessages() {
        //TODO: implement this function. May get into account that local messages have not to be retrieved from server
    }
}
