package com.chattyhive.backend.bussinesobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Created by Jonathan on 11/12/13.
 */
public class MessageContent {
    String _text;

    public MessageContent(String text) {
        this._text = text;
    }

    public MessageContent (JsonElement jsonMessageContent) {
        this.fromJson(jsonMessageContent);
    }

    public void setContent(String text) {
        this._text = text;
    }

    public String getContent() {
        return this._text;
    }

    public JsonElement toJson() {
        return new JsonPrimitive(this._text);
    }
    public void fromJson(JsonElement json) {
        if (json.isJsonPrimitive()) {
            this._text = json.getAsString();
        } else {
            this._text = "";
        }
    }
}
