package com.chattyhive.chattyhive;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 */
public class ChatMessage {
    public String user;
    public String message;
    public Date timeStamp;

    public ChatMessage(String username, String message_text, Date message_timeStamp) {
        this.user = username;
        this.message = message_text;
        this.timeStamp = message_timeStamp;
    }
}
