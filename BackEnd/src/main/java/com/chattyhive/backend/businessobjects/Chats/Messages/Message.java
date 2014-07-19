package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonElement;

import java.io.IOException;
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

    private Boolean isMessageHole;
    private int holeSize;
    private Boolean filling;
    /*
     * Events for class message.
     */
    public Event<EventArgs> ConfirmationReceived;
    public Event<EventArgs> IdReceived;

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
        if (this.IdReceived != null)
            this.IdReceived.fire(this, EventArgs.Empty());
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
        if (this.ConfirmationReceived != null)
            this.ConfirmationReceived.fire(this,EventArgs.Empty());
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

    //CONSTRUCTOR FOR DATE SEPARATOR
    public Message(Chat chat, Date timeStamp) {
        this.user = null;
        this.chat = chat;
        this.content = new MessageContent("DATE_SEPARATOR",null);
        this.timeStamp = timeStamp;
        this.serverTimeStamp = timeStamp;
        this.id = this.serverTimeStamp.toString().concat("-DATE_SEPARATOR");
    }

    //CONSTRUCTOR FOR MESSAGE HOLE
    public Message(Chat chat, Date timeStamp, int holeSize) {
        this.user = null;
        this.chat = chat;
        this.content = new MessageContent("HOLE_SEPARATOR",String.format("%d",holeSize));
        this.timeStamp = timeStamp;
        this.serverTimeStamp = timeStamp;
        this.id = this.serverTimeStamp.toString().concat("-HOLE_SEPARATOR");

        this.isMessageHole = true;
        this.filling = false;
        this.holeSize = holeSize;
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

        this.InitializeEvents();
    }

    public void FillMessageHole(String nextMessageId) {
        if ((!isMessageHole) || (filling)) return;
        if (DataProvider.isConnectionAvailable()) {
            DataProvider dataProvider = DataProvider.GetDataProvider();
            if (dataProvider.isServerConnected()) {
                CHAT_ID chat_id = new CHAT_ID();
                chat_id.CHANNEL_UNICODE = this.chat.getParent().getChannelUnicode();
                MESSAGE_INTERVAL message_interval = new MESSAGE_INTERVAL();
                message_interval.COUNT = this.holeSize+1;
                message_interval.LAST_MESSAGE_ID = nextMessageId;

                try {
                    filling = true;
                    dataProvider.InvokeServerCommand(ServerCommand.AvailableCommands.GetMessages,new EventHandler<CommandCallbackEventArgs>(this,"onMessageHoleFilledCallback",CommandCallbackEventArgs.class),chat_id,message_interval);
                } catch (NoSuchMethodException e) {
                    filling = false;
                    e.printStackTrace();
                }
            }
        }
    }

    public void onMessageHoleFilledCallback (Object sender, CommandCallbackEventArgs eventArgs) {
        if ((!isMessageHole) || (!filling)) return;
        filling = false;
        Chat.onFormatReceived(this,new FormatReceivedEventArgs(eventArgs.getReceivedFormats()));
        this.chat.removeMessage(this.getId());
    }

    /**
     * Public constructor. This constructor parses the message from a JSONObject.
     * @param jsonMessage the JSONElement representing the message.
     */
    public Message(JsonElement jsonMessage) {
        this.fromJson(jsonMessage);
    }

    public Message(Format format) {
        this.fromFormat(format);
    }

    private void InitializeEvents() {
        this.IdReceived = new Event<EventArgs>();
        this.ConfirmationReceived = new Event<EventArgs>();
    }


    public void SendMessage() throws IOException {
        if ((this.user == null) || (this.chat == null) || (this.content == null) || (this.timeStamp == null) || (this.content.getContentType().endsWith("_SEPARATOR"))) throw new IOException("Cannot send message");

        this.chat.addMessage(this);
        DataProvider dataProvider = DataProvider.GetDataProvider();
        try {
            dataProvider.InvokeServerCommand(ServerCommand.AvailableCommands.SendMessage,new EventHandler<CommandCallbackEventArgs>(this,"onMessageSendCallback",CommandCallbackEventArgs.class),this.toFormat(new MESSAGE()));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void onMessageSendCallback (Object sender, CommandCallbackEventArgs eventArgs) {
        for (Format format : eventArgs.getReceivedFormats()) {
            if (format instanceof MESSAGE_ACK) {
                this.fromFormat(format);
            }
        }
    }

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }

    public void fromJson(JsonElement json) {
        Format[] formats = Format.getFormat(json);

        for (Format format : formats)
            if (this.fromFormat(format)) break;

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
        } else {
            throw new IllegalArgumentException("MESSAGE or MESSAGE_ACK formats expected in format parser.");
        }

        return format;
    }

    public Boolean fromFormat(Format format) {
        if (format instanceof MESSAGE) {
            this.id = ((MESSAGE)format).ID;
            this.timeStamp = ((MESSAGE) format).TIMESTAMP;
            this.serverTimeStamp = ((MESSAGE) format).SERVER_TIMESTAMP;
            this.confirmed = ((MESSAGE) format).CONFIRMED;
            this.content = new MessageContent(((MESSAGE) format).CONTENT);
            this.user = User.getUser(((MESSAGE) format).PROFILE);
            this.chat = Group.getGroup(((MESSAGE) format).CHANNEL_UNICODE,true).getChat();

            return true;
        } else if (format instanceof MESSAGE_ACK) {
            this.id = ((MESSAGE_ACK) format).ID;
            this.serverTimeStamp = ((MESSAGE_ACK) format).SERVER_TIMESTAMP;
            this.IdReceived.fire(this, EventArgs.Empty());

            return true;
        }

        return false;
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