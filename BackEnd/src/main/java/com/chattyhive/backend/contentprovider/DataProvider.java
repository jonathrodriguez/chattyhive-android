package com.chattyhive.backend.contentprovider;

import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionStateChange;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;

import com.chattyhive.backend.util.events.CancelableEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jonathan on 11/12/13.
 * This class is intended to provide a generic interface to access data independently
 * from where data comes from. Possible data origins are local, server and pusher.
 */
public class DataProvider {
    /************************************************************************/
    /*                       STATIC MANAGEMENT                              */
    /************************************************************************/

    public static void Initialize() {
        DisposingDataProvider = new Event<CancelableEventArgs>();
        DataProviderDisposed = new Event<EventArgs>();

    }

    public static void Initialize(Object... LocalStorage) {
        Initialize();
        setLocalStorage(LocalStorage);
    }

    //COMMON STATIC
    private static DataProvider dataProvider;

    public static Event<CancelableEventArgs> DisposingDataProvider;
    public static Event<EventArgs> DataProviderDisposed;

    public static DataProvider GetDataProvider() {
        return GetDataProvider(false);
    }
    public static DataProvider GetDataProvider(Boolean initialize) {
        if ((dataProvider == null) && (initialize))
            dataProvider = new DataProvider(LoginLocalStorage);

        return dataProvider;
    }
    public static DataProvider GetDataProvider(Object... LocalStorage) {
        GetDataProvider(true);
        setLocalStorage(LocalStorage);
        return dataProvider;
    }
    public static void DisposeDataProvider() {
        Boolean disposeCanceled = false;

        if (DisposingDataProvider != null) {
            CancelableEventArgs eventArgs = new CancelableEventArgs();
            DisposingDataProvider.fire(dataProvider,eventArgs);
            disposeCanceled = eventArgs.isCanceled();
        }

        if (disposeCanceled) return;

        if (DataProviderDisposed != null)
            DataProviderDisposed.fire(dataProvider,EventArgs.Empty());

        dataProvider = null;
    }

    //STORAGE STATIC


    public static GroupLocalStorageInterface GroupLocalStorage;
    public static HiveLocalStorageInterface HiveLocalStorage;
    public static LoginLocalStorageInterface LoginLocalStorage;
    public static MessageLocalStorageInterface MessageLocalStorage;
    public static UserLocalStorageInterface UserLocalStorage;

    public static void setLocalStorage(Object... LocalStorage) {
        for (Object localStorage : LocalStorage) {
            if (localStorage instanceof GroupLocalStorageInterface) {
                GroupLocalStorage = (GroupLocalStorageInterface) localStorage;
            } else if (localStorage instanceof HiveLocalStorageInterface) {
                HiveLocalStorage = (HiveLocalStorageInterface) localStorage;
            } else if (localStorage instanceof LoginLocalStorageInterface) {
                LoginLocalStorage = (LoginLocalStorageInterface) localStorage;
            } else if (localStorage instanceof MessageLocalStorageInterface) {
                MessageLocalStorage = (MessageLocalStorageInterface) localStorage;
            } else if (localStorage instanceof UserLocalStorageInterface) {
                UserLocalStorage = (UserLocalStorageInterface) localStorage;
            }
        }
    }

    /************************************************************************/
    /************************************************************************/
    /************************************************************************/

    /************************************************************************/
    /*                       DYNAMIC MANAGEMENT                              */
    /************************************************************************/

    private Server server;

    @Deprecated
    public Server getServer() {
        return this.server;
    }

    /************************************************************************/
    //CONSTRUCTORS

    public DataProvider () {
        this.server = new Server();

        this.InitializeEvents();
    }

    public DataProvider(Object... LocalStorage) {
        DataProvider.setLocalStorage(LocalStorage);

        this.server = new Server(LoginLocalStorage);

        this.InitializeEvents();
    }

    private void InitializeEvents() {
        this.ConnectionAvailabilityChanged = new Event<EventArgs>();
        this.ServerConnectionStateChanged = new Event<ConnectionEventArgs>();
    }
    /************************************************************************/
    //CONNECTION MANAGEMENT

    private Boolean connectionAvailable;
    public Event<EventArgs> ConnectionAvailabilityChanged;

    private Boolean connectionState;
    public Event<ConnectionEventArgs> ServerConnectionStateChanged;

    public Boolean isConnectionAvailable() {
        return this.connectionAvailable;
    }
    public void setConnectionAvailable(Boolean value) {
        if (this.connectionAvailable != value) {
            this.connectionAvailable = value;
            if (ConnectionAvailabilityChanged != null)
                ConnectionAvailabilityChanged.fire(this, EventArgs.Empty());
        }
    }

    public Boolean isServerConnected() {
        return this.connectionState;
    }
    public void Connect() {
        if (this.connectionAvailable) {
            this.server.Connect();
            this.targetState = ConnectionState.CONNECTED;
            this.pubSub.Connect();
        }
    }
    public void Disconnect() {
        this.server.Disconnect();
        this.targetState = ConnectionState.DISCONNECTED;
        this.pubSub.Disconnect();
    }

    public void onServerConnectionStateChanged(Object sender, ConnectionEventArgs eventArgs) {
        if (this.ServerConnectionStateChanged != null)
            this.ServerConnectionStateChanged.fire(sender,eventArgs);
    }
    /************************************************************************/
    //EXPLORE


    /************************************************************************/

    /************************************************************************/
    private ServerUser serverUser;

    private PubSub pubSub;
    private ConnectionState targetState;
    private Boolean networkAvailable = true;

    /**
     * Permits other classes to subscribe to pusher channel events.
     * @param eventHandler an event handler that points to the method to be invoked.
     */
    public void SubscribeChannelEventHandler(EventHandler<PubSubChannelEventArgs> eventHandler) {
        this.pubSub.SubscribeChannelEventHandler(eventHandler);
    }

    /**
     * Permits other classes to subscribe to pusher connection events.
     * @param eventHandler an event handler that points to the method to be invoked.
     */
    public void SubscribeConnectionEventHandler(EventHandler<PubSubConnectionEventArgs> eventHandler) {
        this.pubSub.SubscribeConnectionEventHandler(eventHandler);
    }

    /**
     * Permits other classes to subscribe to Server OnConnect event.
     * @param eventHandler an event handler that points to the method to be invoked.
     */
    public void SubscribeToOnConnect(EventHandler<ConnectionEventArgs> eventHandler) {
        if (this.server != null)
            this.server.SubscribeToOnConnected(eventHandler);
    }

    /**
     * Retrieves user login information.
     * @return a string containing the user login-
     */
    public String getUser() { return this.serverUser.getLogin(); }

    /**
     * Retrieves the Server User.
     * @return
     */
    public ServerUser getServerUser() { return this.serverUser; }
    /**
     * Changes the server user.
     * @param newUser the new server user.
     */
    public void setUser(ServerUser newUser) {
        this.serverUser = newUser;
        this.server.setServerUser(this.serverUser);
    }

    /**
     * Changes the server app.
     * @param serverApp
     */
    public void setServerApp (String serverApp) {
        this.server.setAppName(serverApp);
    }

    /**
     * Public constructor.
     * @param user the server user data to use in connections.
     * @param serverApp the server application to be used.
     */
    public DataProvider(ServerUser user, String serverApp) {
        this.serverUser = user;
        this.server = new Server(this.serverUser,serverApp);

        this.pubSub = new PubSub(this.serverUser);

        try {
            this.pubSub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
            this.pubSub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionEvent", PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }

        /*this.pubSub.Join("public_test");*/
    }

    public Boolean JoinHive(String jsonParams) {
        return this.server.JoinHive(jsonParams);
    }

    public void Join(String channel) {
        /*try{
            this.pubSub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
            this.pubSub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionEvent", PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }*/

        while (!this.pubSub.Join(channel)) {
            this.pubSub.Leave(channel);
        }
    }

    public void Leave(String channel) {
        this.pubSub.Leave(channel);
    }


    /**
     * Sends a message, which is correctly JSON formatted, to the server.
     * @param message a JSONElement with the message.
     */
    public Boolean sendMessage(JsonElement message) {
        //
        // TODO: Save message.
        //
        Boolean result = false;
        if (this.networkAvailable) {
            result = this.server.SendMessage(message.toString());
        }
        return result;
    }

    /**
     * This method will be invoked on a channel event. Its function is to save the message locally, so
     * next time the message has to be read it will be read from local storage.
     * @param sender the object which fired the event.
     * @param args the event arguments.
     */
    public void onChannelEvent(Object sender, PubSubChannelEventArgs args) {
        if (args.getEventName().compareTo("msg") == 0) {
            // We have a message so lets play with it.
            // Save the message.
        }
    }

    /**
     * This method will be invoked on a connection event. If the target state doesn't match the current connection state
     * then the last connection operation (Connect or Disconnect) is retried.
     * @param sender the object which fired the event.
     * @param args the event arguments.
     */
    public void onConnectionEvent(Object sender, PubSubConnectionEventArgs args) {
        ConnectionStateChange change = args.getChange();
        if ((this.targetState == ConnectionState.CONNECTED) && (change.getCurrentState() == ConnectionState.DISCONNECTED)) {
            this.pubSub.Connect();
        } else if ((this.targetState == ConnectionState.DISCONNECTED) && (change.getCurrentState() == ConnectionState.CONNECTED)) {
            this.pubSub.Disconnect();
        }
    }

    /**
     * This method recovers the message list for a chat. It has to merge local data with remote data.
     * @param chatID the identification of the chat.
     * @return an ArrayList with the messages.
     */
    public ArrayList<Message> RecoverMessages(String chatID) {
        ArrayList<Message> messageList = new ArrayList<Message>();

        //
        // TODO: Try to get messageList from local.
        //

        //
        // TODO: Try to get newer messages from server.
        //

        Collections.sort(messageList);
        return messageList;
    }

    public JsonElement ExploreHives(int offset,int length) {
        return this.server.ExploreHives(null);
    }

    /**
     * It si very important to know network availability before trying any network operation. There are some
     * functions in android API which permit to retrieve network status, so it's necessary to provide
     * this information to the data provider.
     * @param value a Boolean value indicating whether the network is available.
     */
    public void setNetworkAvailable(Boolean value) { this.networkAvailable = value; }

    /**
     * Returns a value indicating what the data provider was last informed about the network state
     * @return a Boolean value indicating network availability.
     */
    public Boolean getNetworkAvailable() { return this.networkAvailable; }

    /**
     * Returns a value indicating if the PubSub underlying service is connected or connecting.
     * @return a Boolean value indicating if the PubSub service is connected or connecting.
     */
    public Boolean isPubsubConnected() {
        ConnectionState cs = this.pubSub.GetConnectionState();
        return ((cs == ConnectionState.CONNECTED) || (cs == ConnectionState.CONNECTING));
    }
}

