package com.chattyhive.chattyhive;

import java.util.Date;

/**
 * Created by Jonathan on 24/10/13.
 */
public class Multichat_MessageClass {
    public String user;
    public String message;
    public Date timeStamp;

    public Multichat_MessageClass (String username, String message_text, Date message_timeStamp) {
        this.user = username;
        this.message = message_text;
        this.timeStamp = message_timeStamp;
    }
}
