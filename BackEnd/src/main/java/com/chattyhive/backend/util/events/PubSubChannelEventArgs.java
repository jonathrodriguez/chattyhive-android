package com.chattyhive.backend.util.events;

/**
 * Created by Jonathan on 26/12/13.
 * This class contains arguments for the PubSub Channel Event. Those arguments are the channel name, the
 * event name and the message string representation.
 */
public class PubSubChannelEventArgs extends EventArgs {
    private String _channelName;
    private String _eventName;
    private String _message;

    public PubSubChannelEventArgs() { super(); }
    public PubSubChannelEventArgs(String channelName, String eventName, String message) {
        super();
        this._channelName = channelName;
        this._eventName = eventName;
        this._message = message;
    }

    public void setChannelName(String channelName) { this._channelName = channelName; }
    public void setEventName (String eventName) { this._eventName = eventName; }
    public void setMessage (String message) { this._message = message; }

    public String getChannelName () { return this._channelName; }
    public String getEventName () { return this._eventName; }
    public String getMessage () { return this._message; }
}
