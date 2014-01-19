package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by Jonathan on 11/12/13.
 * This class represents the message content.
 */
public class MessageContent {
    String _text;

    /**
     * Public constructor.
     * @param text a string representing the content of the message.
     */
    public MessageContent(String text) {
        this._text = text;
    }

    /**
     * Public constructor.
     * @param jsonMessageContent a JSONElement which represents the message content.
     */
    public MessageContent (JsonElement jsonMessageContent) {
        this.fromJson(jsonMessageContent);
    }

    /**
     * Sets the content of this message content object.
     * @param text the text of the message.
     */
    public void setContent(String text) {
        this._text = text;
    }

    /**
     * Returns the content of this message content object.
     * @return a string with its content
     */
    public String getContent() {
        return this._text;
    }

    /**
     * Returns a JSONElement with the content of this message content object.
     * @return a JSONElement representing this object.
     */
    public JsonElement toJson() {
        return new JsonPrimitive(this._text);
    }

    /**
     * Sets the content of this message content object from its JSON representation.
     * @param json a JSONElement with the JSON representation of this message content object.
     */
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            this._text = json.getAsString();
        } else {
            this._text = "";
        }
    }
}
