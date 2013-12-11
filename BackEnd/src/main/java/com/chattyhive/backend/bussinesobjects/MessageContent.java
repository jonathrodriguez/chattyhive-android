package com.chattyhive.backend.bussinesobjects;

/**
 * Created by Jonathan on 11/12/13.
 */
public class MessageContent {
    String _text;

    public MessageContent(String text) {
        this._text = text;
    }

    public void setContent(String text) {
        this._text = text;
    }

    public String getContent() {
        return this._text;
    }
}
