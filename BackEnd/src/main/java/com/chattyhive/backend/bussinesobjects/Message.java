package com.chattyhive.backend.bussinesobjects;

import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 */
public class Message implements Comparable{
    public User _user;
    public MessageContent _content;
    public Date _timeStamp;

    public Message(User user, MessageContent content, Date timeStamp) {
        this._user = user;
        this._content = content;
        this._timeStamp = timeStamp;
    }

    public Message(MessageContent content, Date timeStamp) {
        this._content = content;
        this._timeStamp = timeStamp;
    }

    public Message(JsonElement jsonMessage) {
        this.fromJson(jsonMessage);
    }

    public User getUser() {
        return this._user;
    }

    public MessageContent getMessage() {
        return this._content;
    }

    public Date getTimeStamp() {
        return this._timeStamp;
    }

    public JsonElement toJson() {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.addProperty("timestamp", TimestampFormatter.toString(this._timeStamp));
        jsonMessage.add("username",this._user.toJson());
        jsonMessage.add("message",this._content.toJson());
        return jsonMessage;
    }

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
    public int compareTo(Object o) {
        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }
        return this._timeStamp.compareTo(((Message)o).getTimeStamp());
    }
}
