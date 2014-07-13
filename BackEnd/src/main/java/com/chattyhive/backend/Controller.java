package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.contentprovider.server.ServerStatus;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.DateFormatter;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

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
    private LoginLocalStorageInterface loginLocalStorage;
    private MessageLocalStorageInterface messageLocalStorage;

    public static Controller getRunningController() {
        return _controller;
    }

    public static Controller getRunningController(LoginLocalStorageInterface loginLocalStorage) {
        if (_controller == null)
            _controller = new Controller(loginLocalStorage);
        else if (_controller.loginLocalStorage == null) {
            _controller.setLoginLocalStorage(loginLocalStorage);
        }
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
    private HashMap<String, TreeSet<Message>> messages = new HashMap<String, TreeSet<Message>>();
    private ArrayList<Hive> hives = new ArrayList<Hive>();
    public ArrayList<Hive> getHives() { return hives; }
    public Hive getHiveFromUrlName(String UrlName) {
        if ((UrlName != null) && (!UrlName.isEmpty()))
            for (Hive h : this.hives)
                if (h.getNameURL().equalsIgnoreCase(UrlName)) return h;
        return null;
    }
    public Hive getHiveFromName(String Name) {
        if ((Name != null) && (!Name.isEmpty()))
            for (Hive h : this.hives)
                if (h.getName().equalsIgnoreCase(Name)) return h;
        return null;
    }

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
    public Controller(LoginLocalStorageInterface loginLocalStorage) {
        this(new ServerUser(loginLocalStorage),StaticParameters.DefaultServerAppName);
        this.setLoginLocalStorage(loginLocalStorage);
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
        new User(user.getLogin());

        Hive.Initialize(this,null);
        Group.Initialize(this,null);
        Chat.Initialize(this,null);
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
        Boolean result = this._dataProvider.Connect();

        if (result) {
            ServerUser su = this.getServerUser();
            this.loginLocalStorage.StoreLoginPassword(su.getLogin(),su.getPassword());
        }
        return result;
    }

    public Boolean JoinHive(String hive) {
        JsonObject jsonParams = new JsonObject();
        jsonParams.addProperty("user",this.getServerUser().getLogin());
        jsonParams.addProperty("hive",hive);

        Boolean result = this._dataProvider.JoinHive(jsonParams.toString());

        if (result) {
            for (Hive h : exploreHives) {
                if (h.getNameURL().equalsIgnoreCase(hive)) {
                    this.hives.add(h);
                    exploreHives.remove(h);
                }
            }
        }
        
        return result;
    }

    public void JoinTMP (String channel) {
        this._dataProvider.Join(channel);
    }

    public void Join(String channel) {
        //this._dataProvider.Join(channel);
    }

    public void Leave(String channel) {
        //this._dataProvider.Leave(channel);
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

        System.out.println("Channel event: ".concat(args.getChannelName()).concat(" -> ").concat(args.getEventName()).concat(" : ").concat(args.getMessage()));

        if (args.getEventName().equalsIgnoreCase("msg")){
            //System.out.println("Detected as message.");

            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(args.getMessage());
            m = new Message(jsonElement);
            if (m.getChat() == null) m.chat = this.getHiveFromUrlName(args.getChannelName());

            //System.out.println("Received Message: ".concat(m.toJson().toString()));

            if (!this.messages.containsKey(args.getChannelName()))
                this.messages.put(args.getChannelName(),new TreeSet<Message>());

            if (!this.messages.get(args.getChannelName()).contains(m)) {
                //System.out.println("Nuevo mensaje recibido. Tamaño de lista de mensajes: ".concat(String.valueOf(this.messages.get(args.getChannelName()).size())));
                this.messages.get(args.getChannelName()).add(m);
                //System.out.println(" -- MESSAGE SAVED -- ");
                if (this.messageLocalStorage != null) {
                    this.messageLocalStorage.StoreMessage(args.getChannelName(),m.toJson().toString());
                }
                m = new Message(new MessageContent(""), DateFormatter.toDate(DateFormatter.toString(m.getTimeStamp())));
                if (!this.messages.get(args.getChannelName()).contains(m)) this.messages.get(args.getChannelName()).add(m);
            }

        } else {
           // System.out.println("Detected as NOT a message.");
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
    public TreeSet<Message> getMessages(String channel) {
        if (!this.messages.containsKey(channel))
            this.messages.put(channel,new TreeSet<Message>());

        TreeSet<Message> messageList = this.messages.get(channel);

        // Local message recovering
        if (this.messageLocalStorage != null) {
            String[] localMessages = this.messageLocalStorage.RecoverMessages(channel);
            if (localMessages != null) {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement;
                Message m;
                for (String jsonMessage : localMessages) {
                    jsonElement = jsonParser.parse(jsonMessage);
                    m = new Message(jsonElement);
                    if (!messageList.contains(m)) messageList.add(m);
                    m = new Message(new MessageContent(""), DateFormatter.toDate(DateFormatter.toString(m.getTimeStamp())));
                    if (!messageList.contains(m)) messageList.add(m);
                }
            }
        }

        // TODO: Implement remote message recovering.
        //this._dataProvider.RecoverMessages(channel);

        return messageList;
    }

    /**
     * This method permits application to recover some hives from explore server list.
     * @param
     */
    public boolean exploreHives(int offset,int length) {
        if (offset == 0) { exploreHives.clear(); }
        int actualSize = exploreHives.size();
        boolean exploreHiveListChanged = false;
        JsonElement response = null;
        if (!StaticParameters.StandAlone) {

            response = this._dataProvider.ExploreHives(offset,length);

            if ((response != null) && (!response.isJsonNull())) {
                JsonArray hivesArray = null;
                if (response.isJsonArray()) {
                    hivesArray = response.getAsJsonArray();
                } else if (response.isJsonObject()) {
                    hivesArray = new JsonArray();
                    hivesArray.add(response);
                }
                if (hivesArray != null)
                    for (JsonElement jsonElement : hivesArray)
                        if (jsonElement.isJsonObject()) {
                            Hive hive = new Hive(jsonElement);
                            Boolean alreadyJoined = false;
                            for (Hive h : hives) {
                                if (h.getNameURL().equalsIgnoreCase(hive.getNameURL()))
                                    alreadyJoined = true;

                            }
                            exploreHiveListChanged = ((!alreadyJoined) && (exploreHiveListChanged | this.exploreHives.add(hive)));
                        }
            }
            // TODO: This is for server 0.2.0 which does not support list indexing.
            actualSize = this.exploreHives.size();
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

        message.user = User.getMe();
        message.chat = getHiveFromUrlName(channel);


        if (!this.messages.containsKey(channel))
            this.messages.put(channel,new TreeSet<Message>());

        if (!this.messages.get(channel).contains(message)) {
            this.messages.get(channel).add(message);

        }

        Boolean result = this._dataProvider.sendMessage(message.toJson());

        if ((!result) && (this.messages.get(channel).contains(message))) {
         //   System.out.println("Mensaje enviado. Tamaño de lista de mensajes: ".concat(String.valueOf(this.messages.get(channel).size())));
            this.messages.get(channel).remove(message);
        }
        if ((result) && (this.messageLocalStorage != null)) {
                this.messageLocalStorage.StoreMessage(channel,message.toJson().toString());
            Message m = new Message(new MessageContent(""), DateFormatter.toDate(DateFormatter.toString(message.getTimeStamp())));
            if (!this.messages.get(channel).contains(m)) this.messages.get(channel).add(m);
        }


        //return true;
        return result;

        //return this._dataProvider.sendMessage("message=".concat(message.content.getContent().replace("+", "%2B").replace(" ", "+")).concat("&timestamp=").concat(TimestampFormatter.toString(message.getTimeStamp()).replace(":", "%3A").replace("+", "%2B").replace(" ", "+")));
    }

    public void onConnect (Object sender, ConnectionEventArgs args) {
        JsonElement profile = args.getProfile();
        JsonElement hivesSubscribed = args.getHivesSubscribed();
        Boolean hiveListChanged = false;
        System.out.println("onConnect()");
        if ((profile != null) && (!profile.isJsonNull())) {
            System.out.println(profile.toString());
            new User(getServerUser().getLogin());
            User.setUpOwnProfile(profile);
        }
        if ((hivesSubscribed != null) && (!hivesSubscribed.isJsonNull())) {
            System.out.println(String.format("HivesSubscribed: %s",hivesSubscribed.toString()));
            JsonArray hivesArray = null;
            this.hives.clear();
            if (hivesSubscribed.isJsonArray()) {
                //System.out.println("Is a JSON Array");
                hivesArray = hivesSubscribed.getAsJsonArray();
            } else if (hivesSubscribed.isJsonObject()) {
                //System.out.println("Is a JSON Object");
                hivesArray = new JsonArray();
                hivesArray.add(hivesSubscribed);
            }
            if (hivesArray != null)
                for (JsonElement jsonElement : hivesArray)
                    if (jsonElement.isJsonObject()) {
                        Hive hive = new Hive(jsonElement);
                        this.JoinTMP(hive.getNameURL());
                        System.out.println(String.format("Hive: %s",hive.getName()));
                        hiveListChanged = (hiveListChanged | this.hives.add(hive));
                    }
        }


//        for (Hive hive : this.hives) {
//            String line = "Name: ".concat(hive.getName());
//            line = line.concat(" ; NameURL: ").concat(hive.getNameURL());
//            line = line.concat(" ; Description: ").concat(hive.getDescription());
//            line = line.concat(" ; Category: ").concat(hive.getCategory());
//            line = line.concat(" ; Created: ").concat(hive.getCreationDate().toString());
//            System.out.println(line);
//        }

        if (hiveListChanged && (this.hivesListChange != null)) this.hivesListChange.fire(this.hives,EventArgs.Empty());
    }

    public void setLoginLocalStorage(LoginLocalStorageInterface loginLocalStorage) {
        this.loginLocalStorage = loginLocalStorage;
    }

    public void setMessageLocalStorage(MessageLocalStorageInterface messageLocalStorage) {
        this.messageLocalStorage = messageLocalStorage;
    }

    public void clearUserData() {
        this._dataProvider.Disconnect();
        this._dataProvider.setUser(null);
        if (this.loginLocalStorage != null)
            this.loginLocalStorage.ClearStoredLogin();
        User.removeMe();
    }

    public void clearAllChats() {
        for (String channel : this.messages.keySet()) {
            this.clearChat(channel);
        }
    }

    private void clearChat(String channel) {
        if (this.messages.containsKey(channel)) {
            this.messages.get(channel).clear();
            if (this.messageLocalStorage != null)
                this.messageLocalStorage.ClearMessages(channel);

            this._channelEvent.fire(this,new ChannelEventArgs(channel,"clear",null));
        }
    }

}
