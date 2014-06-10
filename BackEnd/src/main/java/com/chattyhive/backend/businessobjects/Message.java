package com.chattyhive.backend.businessobjects;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 * This class represents a message. A message is one of the most basic business objects.
 * Generally a message is sent by a user, in a concrete date and time (timestamp) and it has a content.
 */
public class Message implements Comparable{
    public User _user;
    public MessageContent _content;
    public Date _timeStamp;
    public Hive hive;

    /**
     * Public constructor.
     * @param user The user who sent the message
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(User user, Hive hive, MessageContent content, Date timeStamp) {
        this._user = user;
        this._content = content;
        this._timeStamp = timeStamp;
        this.hive = hive;
    }

    /**
     * Public constructor
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(MessageContent content, Date timeStamp) {
        this._content = content;
        this._timeStamp = timeStamp;
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
        return this._user;
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
    public MessageContent getMessage() {
        return this._content;
    }

    /**
     * Retrieves the timestamp of the message.
     * @return
     */
    public Date getTimeStamp() {
        return this._timeStamp;
    }

    /**
     * Converts the message to its JSON representation.
     * @return
     */
    public JsonElement toJson() {
        JsonObject jsonMessage = new JsonObject();

        jsonMessage.addProperty("timestamp", TimestampFormatter.toString(this._timeStamp));

        if ((this._user != null) && (this._user.getEmail() != null))
            jsonMessage.addProperty("user",this._user.getEmail());

        if ((this._user != null) && (this._user.getPublicName() != null))
            jsonMessage.addProperty("public_name",this._user.getPublicName());

        jsonMessage.add("message",this._content.toJson());

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

            this._user = User.getUser(jsonMessage.get("public_name").getAsString());

                if ((jsonMessage.get("username") != null) && (jsonMessage.get("username").getAsString() != null) && ((this._user.getEmail() == null) || (this._user.getEmail().equalsIgnoreCase("NULL")))) {
                    this._user.setEmail(jsonMessage.get("username").getAsString());
                } else if ((jsonMessage.get("user") != null) && (jsonMessage.get("user").getAsString() != null) && ((this._user.getEmail() == null) || (this._user.getEmail().equalsIgnoreCase("NULL")))) {
                    this._user.setEmail(jsonMessage.get("user").getAsString());
                }

            if (this._content == null) {
                this._content = new MessageContent(jsonMessage.get("message"));
            } else {
                this._content.fromJson(jsonMessage.get("message"));
            }

            if ((!this._user.isMe()) && (jsonMessage.get("server_time") != null) && (jsonMessage.get("server_time").getAsString() != null))
                this._timeStamp = TimestampFormatter.toDate(jsonMessage.get("server_time").getAsString());
            else
                this._timeStamp = TimestampFormatter.toDate(jsonMessage.get("timestamp").getAsString());

            if ((jsonMessage.get("hive") != null) && (jsonMessage.get("hive").getAsString() != null)) {
                this.hive = Controller.getRunningController().getHiveFromName(jsonMessage.get("hive").getAsString());
            } else {
                this.hive = null;
            }
        }
        else {
            this._user = null;
            this._content = null;
            this._timeStamp = null;
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

        int compareRes = this._timeStamp.compareTo(((Message)o).getTimeStamp());

        if (compareRes == 0) {
            if ((this.getUser() == null) && (((Message) o).getUser() != null))
                return 1;
            else if ((this.getUser() != null) && (((Message) o).getUser() == null))
                return -1;
            else if ((this.getUser() == null) && (((Message) o).getUser() == null)) {
                if ((this.getMessage() != null && (this.getMessage().getContent().isEmpty())) && (!(((Message) o).getMessage() != null && (((Message) o).getMessage().getContent().isEmpty()))))
                    return 1;
                else if ((!(this.getMessage() != null && (this.getMessage().getContent().isEmpty()))) && (((Message) o).getMessage() != null && (((Message) o).getMessage().getContent().isEmpty())))
                    return -1;
            }
        }

        return compareRes;
    }

    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Message)) {
            Message m = (Message)o;

            Boolean result = true;
//            System.out.println("User: -> ".concat(String.valueOf((this.getUser().getPublicName().equalsIgnoreCase(m.getUser().getPublicName())))));
//            System.out.println("TimeStamp: -> ".concat(String.valueOf((TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp()))))));
//            System.out.println("Message: -> ".concat(String.valueOf((this.getMessage().getContent().equalsIgnoreCase(m.getMessage().getContent())))));
//           System.out.println("Hive: -> ".concat(String.valueOf((this.getHive().getNameURL().equalsIgnoreCase(m.getHive().getNameURL())))));

            result = result && ((this.getUser() != null) && (m.getUser() != null) && (this.getUser().getPublicName() != null) && (m.getUser().getPublicName() != null) && (this.getUser().getPublicName().equalsIgnoreCase(m.getUser().getPublicName())));
            result = result && (TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp())));
            result = result && ((this.getMessage() != null) && (m.getMessage() != null) && (this.getMessage().getContent() != null) && (m.getMessage().getContent() != null) && (this.getMessage().getContent().equalsIgnoreCase(m.getMessage().getContent())));
            result = result && ((this.getHive() != null) && (m.getHive() != null) && (this.getHive().getNameURL() != null) && (m.getHive().getNameURL() != null) && (this.getHive().getNameURL().equalsIgnoreCase(m.getHive().getNameURL())));
            
            return result;
        }
        return false;
    }
}
