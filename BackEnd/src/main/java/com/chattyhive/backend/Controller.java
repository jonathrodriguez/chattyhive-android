package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Hive;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.businessobjects.MessageContent;
import com.chattyhive.backend.businessobjects.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerStatus;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonArray;
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
    private ArrayList<Hive> hives = new ArrayList<Hive>();
    public ArrayList<Hive> getHives() { return hives; }

    private ArrayList<Hive> exploreHives = new ArrayList<Hive>();
    public ArrayList<Hive> getExploreHives() { return exploreHives; }
    // ContentProvider
    private DataProvider _dataProvider;

    public Boolean getNetworkAvailable() { return this._dataProvider.getNetworkAvailable(); }
    public void setNetworkAvailable(Boolean value) { this._dataProvider.setNetworkAvailable(value); }


    // Events
    private static Event<EventArgs> appBindingEvent;
    public static void SubscribeToAppBindingEvent(EventHandler<EventArgs> eventHandler){
        if (appBindingEvent == null)
            appBindingEvent = new Event<EventArgs>();
        appBindingEvent.add(eventHandler);
    }

    private Event<EventArgs> hivesListChange;
    public void SubscribeToHivesListChange(EventHandler<EventArgs> eventHandler) {
        if (hivesListChange == null)
            hivesListChange = new Event<EventArgs>();
        hivesListChange.add(eventHandler);

        if (StaticParameters.StandAlone) {
            // DEBUG
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 12; i++) {
                        hives.add(new Hive("Hive number: ".concat(String.valueOf(i)),"Hn".concat(String.valueOf(i))));
                        if (hivesListChange != null) {
                            hivesListChange.fire(hives, EventArgs.Empty());
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
            // END DEBUG
        }
    }

    private Event<EventArgs> exploreHivesListChange;
    public void SubscribeToExploreHivesListChange(EventHandler<EventArgs> eventHandler) {
        if (exploreHivesListChange == null)
            exploreHivesListChange = new Event<EventArgs>();
        exploreHivesListChange.add(eventHandler);
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
            this._dataProvider.SubscribeToOnConnect(new EventHandler<ConnectionEventArgs>(this,"onConnect",ConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }
    }

    public Boolean isConnected() {
        Boolean result;
        result = !((this._dataProvider.getServerUser() == null) || (this._dataProvider.getServerUser().getStatus() == ServerStatus.DISCONNECTED) ||(this._dataProvider.getServerUser().getStatus() == ServerStatus.EXPIRED));
        result = (result && this._dataProvider.isPubsubConnected());
        return result;
    }

    /**
     * Performs server connection. The connection to pusher service is also done.
     * @return true if connected to our server, else false
     */
    public Boolean Connect () {
        JsonElement profile = null;
        JsonElement hivesSubscribed = null;
        Boolean result = this._dataProvider.Connect();

        return result;
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
     * This method permits application to recover some hives from explore server list.
     * @param
     */
    public boolean exploreHives(int offset,int length) {
        int actualSize = exploreHives.size();
        boolean exploreHiveListChanged = false;
        JsonElement response = null;
        if (!StaticParameters.StandAlone) {
            // TODO: Implement remote hives recovering.
            //response = this._dataProvider.ExploreHives(offset,length);
            // TODO: Parse the response into exploreHivesList.
            if ((response != null) && (!response.isJsonNull())) {
                JsonArray hivesArray = null;
                if (response.isJsonArray()) {
                    System.out.println("Is a JSON Array");
                    hivesArray = response.getAsJsonArray();
                } else if (response.isJsonObject()) {
                    System.out.println("Is a JSON Object");
                    hivesArray = new JsonArray();
                    hivesArray.add(response);
                }
                if (hivesArray != null)
                    for (JsonElement jsonElement : hivesArray)
                        if (jsonElement.isJsonObject()) {
                            Hive hive = new Hive(jsonElement);
                            System.out.println(hive.getName());
                            exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(hive));
                        }
            }
        } else {
            if (offset == 0) {
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 1","sports_1","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 2","sports_2","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 3","sports_3","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Free time 1","free_time_1","free time","This hive is for free time!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Free time 2","free_time_2","free time","This hive is for free time!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Free time 3","free_time_3","free time","This hive is for free time!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Science 1","science_1","Science","This hive is for science!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Science 2","science_2","Science","This hive is for science!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Science 3","science_3","Science","This hive is for science!")));
                length = 9;
            } else if (offset >= 9) {
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 4","sports_4","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 5","sports_5","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Sports 6","sports_6","sports","This hive is for sports!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Free time 4","free_time_4","free time","This hive is for free time!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Free time 5","free_time_5","free time","This hive is for free time!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Science 4","science_4","Science","This hive is for science!")));
                exploreHiveListChanged = (exploreHiveListChanged | this.exploreHives.add(new Hive("Science 5","science_5","Science","This hive is for science!")));
                length = 9;
            }

        }

        if ((exploreHiveListChanged) && (this.exploreHivesListChange != null))
            this.exploreHivesListChange.fire(this.exploreHives,EventArgs.Empty());

        return ((exploreHives.size() - actualSize) >= length);
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

    public void onConnect (Object sender, ConnectionEventArgs args) {
        JsonElement profile = args.getProfile();
        JsonElement hivesSubscribed = args.getHivesSubscribed();
        Boolean hiveListChanged = false;
        if ((hivesSubscribed != null) && (!hivesSubscribed.isJsonNull())) {
            JsonArray hivesArray = null;
            if (hivesSubscribed.isJsonArray()) {
                System.out.println("Is a JSON Array");
                hivesArray = hivesSubscribed.getAsJsonArray();
            } else if (hivesSubscribed.isJsonObject()) {
                System.out.println("Is a JSON Object");
                hivesArray = new JsonArray();
                hivesArray.add(hivesSubscribed);
            }
            if (hivesArray != null)
                for (JsonElement jsonElement : hivesArray)
                    if (jsonElement.isJsonObject()) {
                        Hive hive = new Hive(jsonElement);
                        System.out.println(hive.getName());
                        hiveListChanged = (hiveListChanged | this.hives.add(hive));
                    }
        }

        for (Hive hive : this.hives) {
            String line = "Name: ".concat(hive.getName());
            line = line.concat(" ; NameURL: ").concat(hive.getNameURL());
            line = line.concat(" ; Description: ").concat(hive.getDescription());
            line = line.concat(" ; Category: ").concat(hive.getCategory());
            line = line.concat(" ; Created: ").concat(hive.getCreationDate().toString());
            System.out.println(line);
        }

        if (hiveListChanged) this.hivesListChange.fire(this.hives,EventArgs.Empty());
    }
}
