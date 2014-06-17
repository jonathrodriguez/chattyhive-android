package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Jonathan on 12/06/2014.
 */
public abstract class AbstractMessageItem implements Comparable {

    @SerializedName("timestamp")
    protected Date timeStamp;

    @SerializedName("content")
    protected MessageContent content;

    public MessageContent getMessageContent() { return this.content; }
    public Date getTimeStamp() { return this.timeStamp; }


    @Override
    /**
     * Method from the "Comparable" interface.
     * It compares two messages and returns an integer value indicating the order of the messages based on
     * their timestamps.
     */
    public int compareTo(Object o) {
        if (!(o instanceof AbstractMessageItem)) {
            throw new ClassCastException();
        }
        if (this.equals(o)) return 0;

        int compareRes = this.timeStamp.compareTo(((AbstractMessageItem)o).getTimeStamp());

        if (compareRes == 0) {
            if ((this.getMessageContent() != null && (this.getMessageContent().getContent().isEmpty())) && (!(((Message) o).getMessageContent() != null && (((Message) o).getMessageContent().getContent().isEmpty()))))
                return 1;
            else if ((!(this.getMessageContent() != null && (this.getMessageContent().getContent().isEmpty()))) && (((Message) o).getMessageContent() != null && (((Message) o).getMessageContent().getContent().isEmpty())))
                return -1;
        }

        return compareRes;
    }

    @Override
    public boolean equals(Object o) {
        if ((o != null) && (o instanceof Message)) {
            Message m = (Message)o;

            Boolean result = true;
//            System.out.println("User: -> ".concat(String.valueOf((this.getUser().getPublicName().equalsIgnoreCase(m.getUser().getPublicName())))));
//            System.out.println("TimeStamp: -> ".concat(String.valueOf((TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp()))))));
//            System.out.println("Message: -> ".concat(String.valueOf((this.getMessageContent().getContent().equalsIgnoreCase(m.getMessageContent().getContent())))));
//           System.out.println("Hive: -> ".concat(String.valueOf((this.getHive().getNameURL().equalsIgnoreCase(m.getHive().getNameURL())))));

            result = result && (TimestampFormatter.toString(this.getTimeStamp()).equalsIgnoreCase(TimestampFormatter.toString(m.getTimeStamp())));
            result = result && ((this.getMessageContent() != null) && (m.getMessageContent() != null) && (this.getMessageContent().getContent() != null) && (m.getMessageContent().getContent() != null) && (this.getMessageContent().getContent().equalsIgnoreCase(m.getMessageContent().getContent())));

            return result;
        }
        return false;
    }
}
