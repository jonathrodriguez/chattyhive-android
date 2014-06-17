package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 * This class represents a message. A message is one of the most basic business objects.
 * Generally a message is sent by a user, in a concrete date and time (timestamp) and it has a content.
 */
public class Message extends AbstractMessageItem {
    @SerializedName("id")
    public String id;

    @SerializedName("user")
    public User user;

    @SerializedName("servertimestamp")
    public Date serverTimeStamp;

    @SerializedName("chat")
    public Hive hive;

    private Boolean confirmed;
    private Event<EventArgs> confirmationReceived;
    public void subscribeConfirmationReceived(EventHandler<EventArgs> eventHandler) { if (this.confirmationReceived == null) this.confirmationReceived = new Event<EventArgs>(); this.confirmationReceived.add(eventHandler); }
    public Boolean unsubscribeConfirmationReceived(EventHandler<EventArgs> eventHandler) {
        Boolean result = false;
        if (this.confirmationReceived != null) {
            result = this.confirmationReceived.remove(eventHandler);
            if (this.confirmationReceived.count() == 0)
                this.confirmationReceived = null;
        }
        return result;
    }
    public void setConfirmed (Boolean value) { this.confirmed = value; if (this.confirmationReceived != null) this.confirmationReceived.fire(this,EventArgs.Empty()); }
    public Boolean getConfirmed() { return this.confirmed; }


    private Event<EventArgs> idReceived;
    public void subscribeIdReceived(EventHandler<EventArgs> eventHandler) { if (this.idReceived == null) { this.idReceived = new Event<EventArgs>(); } this.idReceived.add(eventHandler); }
    public Boolean unsubscribeIdReceived(EventHandler<EventArgs> eventHandler) {
        Boolean result = false;
        if (this.idReceived != null) {
            result = this.idReceived.remove(eventHandler);
            if (this.idReceived.count() == 0)
                this.idReceived = null;
        }
        return result;
    }

    public void setId (String value) { this.id = value; if (this.idReceived != null) this.idReceived.fire(this, EventArgs.Empty());}
    public String getId() { return this.serverTimeStamp.toString(); } //TODO: return this.id;

    /**
     * Public constructor.
     * @param user The user who sent the message
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(User user, Hive hive, MessageContent content, Date timeStamp) {
        this.user = user;
        this.content = content;
        this.timeStamp = timeStamp;
        this.hive = hive;
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

    /**
     * Retrieves the user who sent the message
     * @return
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Retrieves the hive to which this message belongs
     * @return
     */
    public Hive getHive() {
        return this.hive;
    }

    /**
     * Retrieves the message content.
     * @return
     */
    public MessageContent getMessageContent() {
        return this.content;
    }

    /**
     * Retrieves the timestamp of the message.
     * @return
     */
    public Date getTimeStamp() {
        return this.timeStamp;
    }

    /**
     * Converts the message to its JSON representation.
     * @return
     */
    public JsonElement toJson() {
        JsonObject jsonMessage = new JsonObject();

        jsonMessage.addProperty("timestamp", TimestampFormatter.toString(this.timeStamp));

        if ((this.user != null) && (this.user.getEmail() != null))
            jsonMessage.addProperty("user",this.user.getEmail());

        if ((this.user != null) && (this.user.getPublicName() != null))
            jsonMessage.addProperty("public_name",this.user.getPublicName());

        jsonMessage.add("message", this.content.toJson());

        if ((this.hive != null) && (this.hive.getName() != null))
                jsonMessage.addProperty("hive", this.hive.getName());

        return jsonMessage;
    }

    /**
     * Retrieves the message from its JSON representation.
     * @param json
     */
    public void fromJson(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jsonMessage = json.getAsJsonObject();

            this.user = User.getUser(jsonMessage.get("public_name").getAsString());

                if ((jsonMessage.get("username") != null) && (jsonMessage.get("username").getAsString() != null) && ((this.user.getEmail() == null) || (this.user.getEmail().equalsIgnoreCase("NULL")))) {
                    this.user.setEmail(jsonMessage.get("username").getAsString());
                } else if ((jsonMessage.get("user") != null) && (jsonMessage.get("user").getAsString() != null) && ((this.user.getEmail() == null) || (this.user.getEmail().equalsIgnoreCase("NULL")))) {
                    this.user.setEmail(jsonMessage.get("user").getAsString());
                }

            if (this.content == null) {
                this.content = new MessageContent(jsonMessage.get("message"));
            } else {
                this.content.fromJson(jsonMessage.get("message"));
            }

            if ((!this.user.isMe()) && (jsonMessage.get("server_time") != null) && (jsonMessage.get("server_time").getAsString() != null))
                this.timeStamp = TimestampFormatter.toDate(jsonMessage.get("server_time").getAsString());
            else
                this.timeStamp = TimestampFormatter.toDate(jsonMessage.get("timestamp").getAsString());

            if ((jsonMessage.get("hive") != null) && (jsonMessage.get("hive").getAsString() != null)) {
                this.hive = Controller.getRunningController().getHiveFromName(jsonMessage.get("hive").getAsString());
            } else {
                this.hive = null;
            }
        }
        else {
            this.user = null;
            this.content = null;
            this.timeStamp = null;
            this.hive = null;
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

        int compareRes = this.timeStamp.compareTo(((Message)o).getTimeStamp());

        if (compareRes == 0) {
            if ((this.getUser() == null) && (((Message) o).getUser() != null))
                return 1;
            else if ((this.getUser() != null) && (((Message) o).getUser() == null))
                return -1;
            else if ((this.getUser() == null) && (((Message) o).getUser() == null)) {
                if ((this.getMessageContent() != null && (this.getMessageContent().getContent().isEmpty())) && (!(((Message) o).getMessageContent() != null && (((Message) o).getMessageContent().getContent().isEmpty()))))
                    return 1;
                else if ((!(this.getMessageContent() != null && (this.getMessageContent().getContent().isEmpty()))) && (((Message) o).getMessageContent() != null && (((Message) o).getMessageContent().getContent().isEmpty())))
                    return -1;
            }
        }

        return compareRes;
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Message)) {
            Message m = (Message)o;

            Boolean result = true;
//            System.out.println("User: -> ".concat(String.valueOf((this.getUser().getPublicName().equalsIgnoreCase(m.getUser().getPublicName())))));
//            System.out.println("TimeStamp: -> ".concat(String.valueOf((TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp()))))));
//            System.out.println("Message: -> ".concat(String.valueOf((this.getMessageContent().getContent().equalsIgnoreCase(m.getMessageContent().getContent())))));
//           System.out.println("Hive: -> ".concat(String.valueOf((this.getHive().getNameURL().equalsIgnoreCase(m.getHive().getNameURL())))));

            result = result && ((this.getUser() != null) && (m.getUser() != null) && (this.getUser().getPublicName() != null) && (m.getUser().getPublicName() != null) && (this.getUser().getPublicName().equalsIgnoreCase(m.getUser().getPublicName())));
            result = result && (TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp())));
            result = result && ((this.getMessageContent() != null) && (m.getMessageContent() != null) && (this.getMessageContent().getContent() != null) && (m.getMessageContent().getContent() != null) && (this.getMessageContent().getContent().equalsIgnoreCase(m.getMessageContent().getContent())));
            result = result && ((this.getHive() != null) && (m.getHive() != null) && (this.getHive().getNameURL() != null) && (m.getHive().getNameURL() != null) && (this.getHive().getNameURL().equalsIgnoreCase(m.getHive().getNameURL())));
            
            return result;
        }
        return false;
    }
}
