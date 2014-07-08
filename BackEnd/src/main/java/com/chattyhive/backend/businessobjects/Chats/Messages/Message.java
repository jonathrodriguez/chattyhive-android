package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_COUNT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ID;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 * This class represents a message. A message is one of the most basic business objects.
 * Generally a message is sent by a user, in a concrete date and time (timestamp) and it has a content.
 */
public class Message implements Comparable {

    protected Chat chat;
    protected MessageContent content;
    protected String id;
    protected Date serverTimeStamp;
    protected Date timeStamp;  

    protected User user;
    protected Boolean confirmed;

    /*
     * Events for class message.
     */
    private Event<EventArgs> confirmationReceived;
    private Event<EventArgs> idReceived;

    /*
     * Subscription methods for events.
     */
    public void subscribeConfirmationReceived(EventHandler<EventArgs> eventHandler) {
        if (this.confirmationReceived == null)
            this.confirmationReceived = new Event<EventArgs>();
        this.confirmationReceived.add(eventHandler);
    }
    public Boolean unsubscribeConfirmationReceived(EventHandler<EventArgs> eventHandler) {
        Boolean result = false;
        if (this.confirmationReceived != null) {
            result = this.confirmationReceived.remove(eventHandler);
            if (this.confirmationReceived.count() == 0)
                this.confirmationReceived = null;
        }
        return result;
    }

    public void subscribeIdReceived(EventHandler<EventArgs> eventHandler) {
        if (this.idReceived == null)
            this.idReceived = new Event<EventArgs>();
        this.idReceived.add(eventHandler);
    }
    public Boolean unsubscribeIdReceived(EventHandler<EventArgs> eventHandler) {
        Boolean result = false;
        if (this.idReceived != null) {
            result = this.idReceived.remove(eventHandler);
            if (this.idReceived.count() == 0)
                this.idReceived = null;
        }
        return result;
    }

    /*
     * Getters and setters.
     */
    public void setChat(Chat value) {
        this.chat = value;
    }
    public Chat getChat() {
        return this.chat;
    }

    public void setMessageContent (MessageContent value) {
        this.content = value;
    }
    public MessageContent getMessageContent() {
        return this.content;
    }

    public void setId (String value) {
        this.id = value;
        if (this.idReceived != null)
            this.idReceived.fire(this, EventArgs.Empty());
    }
    public String getId() {
        return this.id;
    }

    public void setServerTimeStamp (Date value) {
        this.serverTimeStamp = value;
    }
    public Date getServerTimeStamp() {
        return this.serverTimeStamp;
    }

    public void setTimeStamp(Date value) {
        this.timeStamp = value;
    }
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    public void setUser(User value) {
        this.user = value;
    }
    public User getUser() {
        return this.user;
    }

    public void setConfirmed (Boolean value) {
        this.confirmed = value;
        if (this.confirmationReceived != null)
            this.confirmationReceived.fire(this,EventArgs.Empty());
    }
    public Boolean getConfirmed() {
        return this.confirmed;
    }

    /*
     * Special getters
     */
    public Date getOrdinationTimeStamp() {
        if (this.serverTimeStamp != null)
            return this.serverTimeStamp;
        else
            return this.timeStamp;
    }

    /**
     * Public constructor.
     * @param user The user who sent the message
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(User user, Chat chat, MessageContent content, Date timeStamp) {
        this.user = user;
        this.content = content;
        this.timeStamp = timeStamp;
        this.chat = chat;
    }

    /**
     * Public constructor
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(MessageContent content, Date timeStamp) {
        this.content = content;
        this.timeStamp = timeStamp;
    }

    /**
     * Public constructor. This constructor parses the message from a JSONObject.
     * @param jsonMessage the JSONElement representing the message.
     */
    public Message(JsonElement jsonMessage) {
        this.fromJson(jsonMessage);
    }



    
    

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }

    public void fromJson(JsonElement json) {
        Format[] formats = Format.getFormat(json);

        for (Format format : formats) {
            if ((format instanceof MESSAGE) || (format instanceof MESSAGE_ACK) || (format instanceof MESSAGE_ID)) {
                this.fromFormat(format);
                break;
            }
        }

        throw new IllegalArgumentException("MESSAGE, MESSAGE_ACK or MESSAGE_ID formats expected in json parser.");
    }

    public Format toFormat(Format format) {
        if (format instanceof MESSAGE) {
            ((MESSAGE)format).ID = this.id;
            ((MESSAGE) format).TIMESTAMP = this.timeStamp;
            ((MESSAGE) format).SERVER_TIMESTAMP = this.serverTimeStamp;
            ((MESSAGE) format).CONFIRMED = this.confirmed;
            ((MESSAGE) format).CONTENT = (MESSAGE_CONTENT)this.content.toFormat(new MESSAGE_CONTENT());
            ((MESSAGE) format).PROFILE = (PROFILE_ID)this.user.toFormat(new PROFILE_ID());
            ((MESSAGE) format).CHANNEL_UNICODE = this.chat.getParent().getChannelUnicode();
        } else if (format instanceof MESSAGE_ACK) {
            ((MESSAGE_ACK) format).ID = this.id;
            ((MESSAGE_ACK) format).SERVER_TIMESTAMP = this.serverTimeStamp;
        } else if (format instanceof MESSAGE_ID) {
            ((MESSAGE_ID) format).ID = this.id;
        } else {
            throw new IllegalArgumentException("MESSAGE, MESSAGE_ACK or MESSAGE_ID formats expected in format parser.");
        }

        return format;
    }

    public void fromFormat(Format format) {
        if (format instanceof MESSAGE) {
            this.id = ((MESSAGE)format).ID;
            this.timeStamp = ((MESSAGE) format).TIMESTAMP;
            this.serverTimeStamp = ((MESSAGE) format).SERVER_TIMESTAMP;
            this.confirmed = ((MESSAGE) format).CONFIRMED;
            this.content = new MessageContent(((MESSAGE) format).CONTENT);
            this.user = User.getUser( ((((MESSAGE) format).PROFILE.PUBLIC_NAME == null) || (((MESSAGE) format).PROFILE.PUBLIC_NAME.isEmpty()))?((MESSAGE) format).PROFILE.USER_ID:((MESSAGE) format).PROFILE.PUBLIC_NAME );
            this.chat = Group.getGroup(((MESSAGE) format).CHANNEL_UNICODE,true).getChat();
        } else if (format instanceof MESSAGE_ACK) {
            this.id = ((MESSAGE_ACK) format).ID;
            this.serverTimeStamp = ((MESSAGE_ACK) format).SERVER_TIMESTAMP;
            this.idReceived.fire(this, EventArgs.Empty());
        } else if (format instanceof MESSAGE_ID) {
            this.id = ((MESSAGE_ID) format).ID;
        } else {
            throw new IllegalArgumentException("MESSAGE, MESSAGE_ACK or MESSAGE_ID formats expected in format parser.");
        }
    }

    @Override
    /**
     * Method from the "Comparable" interface.
     * It compares two messages and returns an integer value indicating the order of the messages based on
     * their timestamps.
     */
    public int compareTo(Object o) {
        if (!(o instanceof Message)) {
            throw new ClassCastException();
        }
        if (this.equals(o)) return 0;

        int compareRes = this.getOrdinationTimeStamp().compareTo(((Message)o).getOrdinationTimeStamp());

        if (compareRes == 0) {
            if ((this.getId() == null) && (((Message) o).getId() != null))
                compareRes = 1;
            else if ((this.getId() != null) && (((Message) o).getId() == null))
                compareRes = -1;
            else if ((this.getId() != null) && (((Message) o).getId() != null)) {
                compareRes = (this.getId().compareToIgnoreCase(((Message) o).getId()));
            }
        }

        if (compareRes == 0) {
            if ((this.getChat() == null) && (((Message) o).getChat() != null))
                compareRes = 1;
            else if ((this.getChat() != null) && (((Message) o).getChat() == null))
                compareRes = -1;
            else if ((this.getChat() != null) && (((Message) o).getChat() != null)) {
                compareRes = (this.getChat().getParent().getChannelUnicode().compareToIgnoreCase(((Message) o).getChat().getParent().getChannelUnicode()));
            }
        }

        return compareRes;
    }

    @Override
    /**
     * Method from the "Object" class.
     * It compares two messages and returns an boolean value indicating whether the messages are equal or not.
     */
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Message)) {
            Message m = (Message)o;

            Boolean result = true;

            result = result && (this.getChat().getParent().getChannelUnicode().equalsIgnoreCase(m.getChat().getParent().getChannelUnicode()));
            result = result && (this.getId().equalsIgnoreCase(m.getId()));
            
            return result;
        }
        return false;
    }
}
