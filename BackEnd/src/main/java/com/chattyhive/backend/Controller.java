package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Created by Jonathan on 11/12/13.
 * The Controller class is the main class of the application backend. It provides a unique interface
 * to access information in native communication format and returns up to application the same
 * information structured in business model classes. Business model classes are stored in package
 * com.chattyhive.backend.businessobjects
 * Thus application can ignore communication protocols or data origin, and has only to perform
 * interface data mapping.
 */
public class Controller {
    // BusinessObjects

    // ContentProvider
    private DataProvider _dataProvider;

    // Events
    private Event<ChannelEventArgs> _channelEvent;

    public void SubscribeChannelEventHandler(EventHandler<ChannelEventArgs> eventHandler) {
        this._channelEvent.add(eventHandler);
    }
    public void SubscribeConnectionEventHandler(EventHandler<PubSubConnectionEventArgs> eventHandler) {
        this._dataProvider.SubscribeConnectionEventHandler(eventHandler);
    }

    /**
     * Public constructor. Instantiates a DataProvider and subscribes to PubSubChannelEvent.
     * Since this constructor takes no arguments about server application, the DefaultServerAppName
     * application will be used.
     * @param user A ServerUser with data needed to login to server.
     */
    public Controller(ServerUser user) {
        this(user,StaticParameters.DefaultServerAppName);
    }

    /**
     * Public constructor. Instantiates a DataProvider and subscribes to PubSubChannelEvent.
     * @param user A ServerUser with data needed to login to server.
     * @param serverApp The server application to which DataProvider will connect.
     */
    public Controller(ServerUser user, String serverApp) {
        this._channelEvent = new Event<ChannelEventArgs>();
        this._dataProvider = new DataProvider(user, serverApp);
        try {
            this._dataProvider.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
        } catch (NoSuchMethodException e) { }
    }

    /**
     * Performs server connection. The connection to pusher service is also done.
     */
    public void Connect () {
        this._dataProvider.Connect();
    }

    /**
     * Disconnects from server and pusher service.
     */
    public void Disconnect () {
        this._dataProvider.Disconnect();
    }

    /**
     * Private method to respond to PubSubChannelEvents. It has to be declared as public, else the
     * Java Reflection API could not access to the method. When this method is invoked, the message
     * data is parsed and the ChannelEvent is fired.
     * @param sender Object that thrown the event.
     * @param args Event arguments. PubSubChannelEventArgs contain the channel name, the event name
     *             and string data containing the message which accompanies the event.
     */
    public void onChannelEvent (Object sender, PubSubChannelEventArgs args) {
        Message m;
        if (args.getEventName().compareTo("msg")==0){
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(args.getMessage());
            m = new Message(jsonElement);
        } else {
            m = new Message(new MessageContent(""),TimestampFormatter.toDate(args.getMessage()));
        }
        this._channelEvent.fire(this,new ChannelEventArgs(args.getChannelName(),args.getEventName(),m));
    }

    /**
     * This method permits application to recover all messages from a given channel. Method is not
     * yet implemented, but it  has to get the data returned by DataProvider and format it into
     * business model classes to return. Probably better option is to return an ArrayList sorted by
     * message timestamp.
     */
    public void getMessages(String channel) {
        // TODO: Implement the getMessages method.
        this._dataProvider.RecoverMessages(channel);
    }

    /**
     * This method sends a message to the server through the DataProvider. Message must be passed
     * in JSON string representation to the DataProvider, but for first server version the message
     * is URL encoded.
     * @param message The message to be sent.
     */
    public void sendMessage(Message message) {
        // TODO: Uncomment the following lines to send messages correctly and remove actual sendMessage line.
        //message._user = new User(this._dataProvider.getUser());
        //this._dataProvider.sendMessage(message.toJson());
        this._dataProvider.sendMessage("message=".concat(message._content.getContent().replace("+", "%2B").replace(" ", "+")).concat("&timestamp=").concat(TimestampFormatter.toString(message.getTimeStamp()).replace(":", "%3A").replace("+", "%2B").replace(" ", "+")));
    }
}
