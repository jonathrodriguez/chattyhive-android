package com.chattyhive.backend.util.events;

import com.chattyhive.backend.businessobjects.Message;

/**
 * Created by Jonathan on 30/12/13.
 * This class contains arguments for the Channel Event. Those arguments are the channel name, the
 * event name and the message.
 */
public class ChannelEventArgs extends EventArgs {
    private String _channelName;
    private String _eventName;
    private Message _message;

    public ChannelEventArgs() { super(); }
    public ChannelEventArgs(String channelName, String eventName, Message message) {
        super();
        this._channelName = channelName;
        this._eventName = eventName;
        this._message = message;
    }

    public void setChannelName(String channelName) { this._channelName = channelName; }
    public void setEventName (String eventName) { this._eventName = eventName; }
    public void setMessage (Message message) { this._message = message; }

    public String getChannelName () { return this._channelName; }
    public String getEventName () { return this._eventName; }
    public Message getMessage () { return this._message; }
}
