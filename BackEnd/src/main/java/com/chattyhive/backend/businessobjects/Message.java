package com.chattyhive.backend.businessobjects;

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

    /**
     * Public constructor.
     * @param user The user who sent the message
     * @param content The content of the message
     * @param timeStamp The timestamp of the message
     */
    public Message(User user, MessageContent content, Date timeStamp) {
        this._user = user;
        this._content = content;
        this._timeStamp = timeStamp;
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
        jsonMessage.add("username",this._user.toJson());
        jsonMessage.add("message",this._content.toJson());
        return jsonMessage;
    }

    /**
     * Retrieves the message from its JSON representation.
     * @param json
     */
    public void fromJson(JsonElement json) {
        if (json.isJsonObject()) {
            JsonObject jsonMessage = json.getAsJsonObject();
            if (this._user == null) {
                this._user = new User(jsonMessage.get("username"));
            } else {
                this._user.fromJson(jsonMessage.get("username"));
            }

            if (this._content == null) {
                this._content = new MessageContent(jsonMessage.get("message"));
            } else {
                this._content.fromJson(jsonMessage.get("message"));
            }

            this._timeStamp = TimestampFormatter.toDate(jsonMessage.get("timestamp").getAsString());
        }
        else {
            this._user = null;
            this._content = null;
            this._timeStamp = null;
        }
    }

    @Override
    /**
     * Method from the "Comparable" interface.
     * It compares two messages and returns an integer value indicating the order of the messages based on
     * their timestamps.
     */
    public int compareTo(Object o) {
        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }
        return this._timeStamp.compareTo(((Message)o).getTimeStamp());
    }
}
