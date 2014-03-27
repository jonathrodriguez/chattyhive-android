package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.businessobjects.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;

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
    private static Controller _controller;
    private static Boolean appBounded = false;

    private static User user;

    public static Controller getRunningController() {
        if (_controller == null)
            _controller = new Controller();
        return _controller;
    }
    public static void disposeRunningController() {
        _controller = null;
    }
    public static Boolean isAppBounded() {
        return appBounded;
    }
    public static void bindApp() {
        appBounded = true;
        if (appBindingEvent != null)
            appBindingEvent.fire(_controller, EventArgs.Empty());
    }
    public static void unbindApp() {
        appBounded = false;
        if (appBindingEvent != null)
            appBindingEvent.fire(_controller,EventArgs.Empty());
    } 
    // BusinessObjects
    private HashMap<String, ArrayList<Message>> messages = new HashMap<String, ArrayList<Message>>();
    private static final ArrayList<Hive> hives = new ArrayList<Hive>();
    public static final ArrayList<Hive> getHives() { return hives; }
    // ContentProvider
    private DataProvider _dataProvider;

    // Events
    private static Event<EventArgs> appBindingEvent;
    public static void SubscribeToAppBindingEvent(EventHandler<EventArgs> eventHandler){
        if (appBindingEvent == null)
            appBindingEvent = new Event<EventArgs>();
        appBindingEvent.add(eventHandler);
    }

    private static Event<EventArgs> hivesListChange;
    public static void SubscribeToHivesListChange(EventHandler<EventArgs> eventHandler) {
        if (hivesListChange == null)
            hivesListChange = new Event<EventArgs>();
        hivesListChange.add(eventHandler);

/*        // DEBUG
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 12; i++) {
                    hives.add(new Hive("Hive number: ".concat(String.valueOf(i)),""));
                    if (hivesListChange != null) {
                        hivesListChange.fire(hives, new EventArgs());
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        hives.clear();
        t.start();
        // END DEBUG*/
    }

    private Event<ChannelEventArgs> _channelEvent;
    public void SubscribeChannelEventHandler(EventHandler<ChannelEventArgs> eventHandler) {
        if (this._channelEvent == null)
            this._channelEvent = new Event<ChannelEventArgs>();
        this._channelEvent.add(eventHandler);
    }

    public void SubscribeConnectionEventHandler(EventHandler<PubSubConnectionEventArgs> eventHandler) {
        this._dataProvider.SubscribeConnectionEventHandler(eventHandler);
    }

    /**
     * Establishes the server user.
     * @param user
     */
    public void setServerUser(ServerUser user) {
        this._dataProvider.setUser(user);
    }

    /**
     * Retrieves the Server User.
     * @return
     */
    public ServerUser getServerUser() {
        return this._dataProvider.getServerUser();
    }
    /**
     * Changes the server app.
     * @param serverApp
     */
    public void setServerApp(String serverApp) {
        this._dataProvider.setServerApp(serverApp);
    }
    public Controller() {
        this(new ServerUser("",""),StaticParameters.DefaultServerAppName);
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
     * @return true if connected to our server, else false
     */
    public Boolean Connect () {
        return this._dataProvider.Connect();
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
            if (!((m.getUser() != null) && (m.getUser().isMe()))) {
                if (this.messages.containsKey(args.getChannelName())) {
                    this.messages.get(args.getChannelName()).add(m);
                } else {
                    ArrayList<Message> arrayList = new ArrayList<Message>();
                    arrayList.add(m);
                    this.messages.put(args.getChannelName(),arrayList);
                }
            }
        } else {
            m = new Message(new MessageContent(args.getMessage()),TimestampFormatter.toDate(args.getMessage()));
        }

        this._channelEvent.fire(this,new ChannelEventArgs(args.getChannelName(),args.getEventName(),m));
    }

    /**
     * This method permits application to recover all messages from a given channel. Method is not
     * yet implemented, but it  has to get the data returned by DataProvider and format it into
     * business model classes to return. Probably better option is to return an ArrayList sorted by
     * message timestamp.
     */
    public ArrayList<Message> getMessages(String channel) {
        // TODO: Implement local and remote message recovering.
        //this._dataProvider.RecoverMessages(channel);
        if (!this.messages.containsKey(channel)) {
            ArrayList<Message> arrayList = new ArrayList<Message>();
            this.messages.put(channel,arrayList);
        }
        return this.messages.get(channel);
    }

    /**
     * This method sends a message to the server through the DataProvider. Message must be passed
     * in JSON string representation to the DataProvider, but for first server version the message
     * is URL encoded.
     * @param message The message to be sent.
     */
    public Boolean sendMessage(Message message,String channel) {
        if (user == null) {
            user = new User(this._dataProvider.getUser(),true);
        }
        message._user = user;

        if (!this.messages.containsKey(channel))
            this.messages.put(channel,new ArrayList<Message>());
        this.messages.get(channel).add(message);


        //return true;
        return this._dataProvider.sendMessage(message.toJson());

        //return this._dataProvider.sendMessage("message=".concat(message._content.getContent().replace("+", "%2B").replace(" ", "+")).concat("&timestamp=").concat(TimestampFormatter.toString(message.getTimeStamp()).replace(":", "%3A").replace("+", "%2B").replace(" ", "+")));
    }
}
