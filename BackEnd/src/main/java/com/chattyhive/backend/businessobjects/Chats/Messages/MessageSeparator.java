package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.util.formatters.DateFormatter;

import java.util.Date;

/**
 * Created by Jonathan on 12/06/2014.
 */
public class MessageSeparator extends AbstractMessageItem {

    public MessageSeparator (Date timeStamp) {
        this.timeStamp = DateFormatter.toDate(DateFormatter.toString(timeStamp));
        this.content = null;
    }

    public MessageSeparator (Date timeStamp, Boolean holeSeparator) {
        this.timeStamp = timeStamp;
        this.content = new MessageContent("HOLE-SEPARATOR");
    }
}
