package com.chattyhive.backend.bussinesobjects;

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

    public User getUser() {
        return this._user;
    }

    public MessageContent getMessage() {
        return this._content;
    }

    public Date getTimeStamp() {
        return this._timeStamp;
    }

    @Override
    public int compareTo(Object o) {
        if (o.getClass() != this.getClass()) {
            throw new ClassCastException();
        }
        return this._timeStamp.compareTo(((Message)o).getTimeStamp());
    }
}
