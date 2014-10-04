package com.chattyhive.backend.contentprovider;

import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT_LIST;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_LIST;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.local.LocalStorageInterface;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionStateChange;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;

import com.chattyhive.backend.util.events.CancelableEventArgs;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.Array;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.rmi.MarshalException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jonathan on 11/12/13.
 * This class is intended to provide a generic interface to access data independently
 * from where data comes from. Possible data origins are local, server and pusher.
 */
public class DataProvider {
    /************************************************************************/
    /*                       STATIC MANAGEMENT                              */
    /************************************************************************/
    private static Boolean Initialized = false;
    public static void Initialize() {
        if (Initialized) return;

        DisposingDataProvider = new Event<CancelableEventArgs>();
        DataProviderDisposed = new Event<EventArgs>();
        ConnectionAvailabilityChanged = new Event<EventArgs>();

        connectionAvailable = true;

        Initialized = true;
    }
    public static void Initialize(Object... LocalStorage) {
        Initialize();
        setLocalStorage(LocalStorage);
    }

    //COMMON STATIC
    private static DataProvider dataProvider;
    private static Boolean connectionAvailable;

    public static Event<EventArgs> ConnectionAvailabilityChanged;
    public static Event<CancelableEventArgs> DisposingDataProvider;
    public static Event<EventArgs> DataProviderDisposed;

    public static DataProvider GetDataProvider() {
        return GetDataProvider(false);
    }
    public static DataProvider GetDataProvider(Boolean initialize) {
        if ((dataProvider == null) && (initialize))
            dataProvider = new DataProvider();

        return dataProvider;
    }
    public static DataProvider GetDataProvider(Object... LocalStorage) {
        setLocalStorage(LocalStorage);
        return GetDataProvider(true);
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

    public static Boolean isConnectionAvailable() {
        return DataProvider.connectionAvailable;
    }
    public static void setConnectionAvailable(Boolean value) {
        if (DataProvider.connectionAvailable != value) {
            DataProvider.connectionAvailable = value;
            if (ConnectionAvailabilityChanged != null)
                ConnectionAvailabilityChanged.fire(DataProvider.dataProvider, EventArgs.Empty());
        }
    }

    //STORAGE STATIC
    private static GroupLocalStorageInterface GroupLocalStorage;
    private static HiveLocalStorageInterface HiveLocalStorage;
    private static LoginLocalStorageInterface LoginLocalStorage;
    private static MessageLocalStorageInterface MessageLocalStorage;
    private static UserLocalStorageInterface UserLocalStorage;

    public static void setLocalStorage(Object... LocalStorage) {
        for (Object localStorage : LocalStorage) {
            if ((localStorage instanceof GroupLocalStorageInterface) && (GroupLocalStorage == null)) {
                GroupLocalStorage = (GroupLocalStorageInterface) localStorage;
            } else if ((localStorage instanceof HiveLocalStorageInterface) && (HiveLocalStorage == null)) {
                HiveLocalStorage = (HiveLocalStorageInterface) localStorage;
            } else if ((localStorage instanceof LoginLocalStorageInterface) && (LoginLocalStorage == null)) {
                LoginLocalStorage = (LoginLocalStorageInterface) localStorage;
            } else if ((localStorage instanceof MessageLocalStorageInterface) && (MessageLocalStorage == null)) {
                MessageLocalStorage = (MessageLocalStorageInterface) localStorage;
            } else if ((localStorage instanceof UserLocalStorageInterface) && (UserLocalStorage == null)) {
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
    private LocalStorageInterface localStorage;
    private Integer nextCommandIndex;
    private HashMap<Integer,CommandData> commandStack;

    @Deprecated
    public Server getServer() {
        return this.server;
    }

    /************************************************************************/
    //CONSTRUCTORS

    public DataProvider () {
        this(null,StaticParameters.DefaultServerAppName);
    }

    public DataProvider(String ServerAppName) {
        this(null,ServerAppName);
    }

    public DataProvider(LocalStorageInterface localStorage) {
        this(localStorage,StaticParameters.DefaultServerAppName);
    }

    public DataProvider(LocalStorageInterface localStorage, String ServerAppName) {
        if (LoginLocalStorage == null) throw new UnsupportedOperationException("DataProvider must be previously initialized with storage objects.");

        this.localStorage = localStorage;
        this.commandStack = new HashMap<Integer, CommandData>();
        this.nextCommandIndex = 0;

        DataProvider.dataProvider = this;

        HttpCookie csrfCookie = null;
        HttpCookie sessionCookie = null;

        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies != null) {
            for (HttpCookie cookie : cookies)
                if (cookie.getName().equalsIgnoreCase("csrftoken")) {
                    csrfCookie = cookie;
                } else if (cookie.getName().equalsIgnoreCase("sessionid")) {
                    sessionCookie = cookie;
                }
        }

        this.csrfTokenValid = ((csrfCookie != null) && (!csrfCookie.hasExpired()));
        this.sessionValid = ((sessionCookie != null) && (!sessionCookie.hasExpired()));

        this.InitializeEvents();

        AbstractMap.SimpleEntry<String,String> loginInfo = LoginLocalStorage.RecoverLoginPassword();
        this.server = new Server(loginInfo, ServerAppName);

        this.pubSub = new PubSub();

        this.SubscribeEvents();
    }

    private void SubscribeEvents() {
        //Subscribe to events
        this.server.responseEvent.add(new EventHandler<FormatReceivedEventArgs>(this, "onFormatReceived", FormatReceivedEventArgs.class));
        this.server.onConnected.add(new EventHandler<ConnectionEventArgs>(this, "onServerConnectionStateChanged", ConnectionEventArgs.class));

        this.server.CsrfTokenChanged.add(new EventHandler<EventArgs>(this,"onCsrfTokenChanged",EventArgs.class));

        this.pubSub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
        this.pubSub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionEvent", PubSubConnectionEventArgs.class));
    }

    private void InitializeEvents() {
        //Initialize events
        this.ServerConnectionStateChanged = new Event<ConnectionEventArgs>();
        this.PubSubConnectionStateChanged = new Event<PubSubConnectionEventArgs>();

        this.CsrfTokenChanged = new Event<EventArgs>();

        this.onMessageReceived = new Event<FormatReceivedEventArgs>();
        this.onUserProfileReceived = new Event<FormatReceivedEventArgs>();
        this.onChatProfileReceived = new Event<FormatReceivedEventArgs>();
        this.onHiveProfileReceived = new Event<FormatReceivedEventArgs>();

        this.onHiveJoined = new Event<CommandCallbackEventArgs>();
    }

    /************************************************************************/
    //CONNECTION MANAGEMENT

    private Boolean csrfTokenValid = false;
    private Boolean sessionValid = false;
    public Event<ConnectionEventArgs> ServerConnectionStateChanged;
    public Event<PubSubConnectionEventArgs> PubSubConnectionStateChanged;
    public Event<EventArgs> CsrfTokenChanged;

    public Boolean isServerConnected() {
        return (this.csrfTokenValid && sessionValid);
    }

    public void Connect() {
        if (connectionAvailable) {
            if (!this.csrfTokenValid) this.server.StartSession();
            if (!this.sessionValid) this.server.Login();

            this.targetState = ConnectionState.CONNECTED;
            this.pubSub.Connect();
        }
    }

    public void Disconnect() {
        this.server.Disconnect();
        this.targetState = ConnectionState.DISCONNECTED;
        this.pubSub.Disconnect();
    }

    public void clearSession() {
        this.Disconnect();
        this.setUser(null);
        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        cookieStore.removeAll();

        if (LoginLocalStorage != null)
            LoginLocalStorage.ClearStoredLogin();
    }

    public void onServerConnectionStateChanged(Object sender, ConnectionEventArgs eventArgs) {

        HttpCookie sessionCookie = null;

        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies != null) {
            for (HttpCookie cookie : cookies)
                if (cookie.getName().equalsIgnoreCase("sessionid")) {
                    sessionCookie = cookie;
                }
        }

        this.sessionValid = ((sessionCookie != null) && (!sessionCookie.hasExpired()));

        if (this.ServerConnectionStateChanged != null)
            this.ServerConnectionStateChanged.fire(sender,eventArgs);
    }

    public void onCsrfTokenChanged(Object sender, EventArgs eventArgs) {
        HttpCookie csrfCookie = null;

        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies != null) {
            for (HttpCookie cookie : cookies)
                if (cookie.getName().equalsIgnoreCase("csrftoken")) {
                    csrfCookie = cookie;
                }
        }

        this.csrfTokenValid = ((csrfCookie != null) && (!csrfCookie.hasExpired()));

        if (this.CsrfTokenChanged != null)
            this.CsrfTokenChanged.fire(sender,eventArgs);
    }
    /************************************************************************/
    //EXPLORE


    /************************************************************************/
    //INCOMING FORMATS MANAGEMENT
    public Event<FormatReceivedEventArgs> onMessageReceived;
    public Event<FormatReceivedEventArgs> onUserProfileReceived;
    public Event<FormatReceivedEventArgs> onHiveProfileReceived;
    public Event<FormatReceivedEventArgs> onChatProfileReceived;

    public void onFormatReceived(Object sender, FormatReceivedEventArgs eventArgs) {
        if (this.localStorage != null)
            this.localStorage.FormatsReceived(eventArgs.getReceivedFormats());
        this.ProcessReceivedFormats(eventArgs.getReceivedFormats());
    }

    private void ProcessReceivedFormats(Collection<Format> receivedFormats) {
        ArrayList<Format> MessageFormats = new ArrayList<Format>();
        ArrayList<Format> UserProfileFormats = new ArrayList<Format>();
        ArrayList<Format> HiveProfileFormats = new ArrayList<Format>();
        ArrayList<Format> ChatProfileFormats = new ArrayList<Format>();

        for (Format format : receivedFormats) {
            if ((format instanceof MESSAGE) || (format instanceof MESSAGE_ACK) || (format instanceof MESSAGE_LIST)) {
                MessageFormats.add(format);
            }
            if ((format instanceof PUBLIC_PROFILE) || (format instanceof PRIVATE_PROFILE) || (format instanceof LOCAL_USER_PROFILE)) {
                UserProfileFormats.add(format);
            }
            if ((format instanceof HIVE) || (format instanceof HIVE_ID)) {
                HiveProfileFormats.add(format);
            }
            if ((format instanceof CHAT) || (format instanceof CHAT_ID) || (format instanceof CHAT_SYNC)) {
                ChatProfileFormats.add(format);
            }
            if (format instanceof CHAT_LIST) {
                ChatProfileFormats.addAll(((CHAT_LIST) format).LIST);
            }
        }

        if ((onMessageReceived != null) && (MessageFormats.size() > 0))
            onMessageReceived.fire(this,new FormatReceivedEventArgs(MessageFormats));

        if ((onUserProfileReceived != null) && (UserProfileFormats.size() > 0))
            onUserProfileReceived.fire(this,new FormatReceivedEventArgs(UserProfileFormats));

        if ((onHiveProfileReceived != null) && (HiveProfileFormats.size() > 0))
            onHiveProfileReceived.fire(this,new FormatReceivedEventArgs(HiveProfileFormats));

        if ((onChatProfileReceived != null) && (ChatProfileFormats.size() > 0))
            onChatProfileReceived.fire(this,new FormatReceivedEventArgs(ChatProfileFormats));
    }

    /************************************************************************/
    private ServerUser serverUser;

    private PubSub pubSub;
    private ConnectionState targetState;
    private Boolean networkAvailable = true;


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



    public void JoinHive(Hive hive) {

        HiveLocalStorage.StoreHive(hive.getNameUrl(),hive.toJson(new HIVE()).toString());
        Hive.getHive(hive.getNameUrl());

        this.server.RunCommand(AvailableCommands.Join,new EventHandler<CommandCallbackEventArgs>(this,"onHiveJoinedCallback",CommandCallbackEventArgs.class),hive.toFormat(new HIVE_ID()));
    }

    public Event<CommandCallbackEventArgs> onHiveJoined;

    public void onHiveJoinedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();

        HIVE_ID hive_id = null;

        for(Format format : receivedFormats)
            if (format instanceof COMMON) {
                if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                    ArrayList<Format> sentFormats = eventArgs.getSentFormats();
                    for (Format sentFormat : sentFormats)
                        if (sentFormat instanceof HIVE_ID)
                            hive_id = (HIVE_ID)sentFormat;
                }
                break;
            }

        if (hive_id != null)
            for (Format format : receivedFormats)
                if (format instanceof CHAT) {
                    Hive h = Hive.getHive(hive_id.NAME_URL);
                    Group g = h.getPublicChat();
                    if (g != null)
                        g.fromFormat(format);
                    else {
                        g = Group.getGroup(format);
                        h.setPublicChat(g);
                    }
                }

        if (onHiveJoined != null)
            onHiveJoined.fire(sender,eventArgs);
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


    public void InvokeServerCommand(AvailableCommands command,Format... formats) {
        this.server.RunCommand(command,formats);
    }

    public void InvokeServerCommand(AvailableCommands command,EventHandler<CommandCallbackEventArgs> Callback,Format... formats) {
        this.server.RunCommand(command, Callback, formats);
    }

    public void RunCommand(AvailableCommands command,EventHandler<CommandCallbackEventArgs> Callback,Format... formats) {
        Integer commandIndex = this.nextCommandIndex++;
        CommandData commandData = new CommandData(command,Callback);
        this.commandStack.put(commandIndex,commandData);

        if (this.localStorage != null) {
            commandData.setLevel(ExecutorLevel.LocalStorage);
            this.localStorage.PreRunCommand(command, new EventHandler<CommandCallbackEventArgs>(this, "CommandCallback", CommandCallbackEventArgs.class), commandIndex, formats);
        }

        commandData.setLevel(ExecutorLevel.Server);
        this.server.RunCommand(command,new EventHandler<CommandCallbackEventArgs>(this,"CommandCallback",CommandCallbackEventArgs.class),commandIndex,formats);
    }

    public void CommandCallback(Object sender, CommandCallbackEventArgs Callback) {
        Object additionalData = Callback.getAdditionalData();
        if (!(additionalData instanceof Integer)) return;
        Integer commandIndex = (Integer)additionalData;
        CommandData commandData = this.commandStack.get(commandIndex);
        if (commandData.getLevel().ordinal() > ExecutorLevel.LocalStorage.ordinal()) {
            if (this.localStorage != null) {
                ArrayList<Format> formats = new ArrayList<Format>(Callback.getSentFormats());
                formats.addAll(Callback.getReceivedFormats());
                this.localStorage.PostRunCommand(Callback.getCommand(), formats.toArray(new Format[formats.size()]));
            }
        }
        try {
            commandData.getCallback().Invoke(sender, Callback);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method will be invoked on a channel event. Its function is to save the message locally, so
     * next time the message has to be read it will be read from local storage.
     * @param sender the object which fired the event.
     * @param args the event arguments.
     */
    public void onChannelEvent(Object sender, PubSubChannelEventArgs args) {
        if (args.getEventName().equalsIgnoreCase("msg")) {
            // We have a message so lets play with it.
            Format[] formats = Format.getFormat(new JsonParser().parse(args.getMessage()));
            this.ProcessReceivedFormats(Arrays.asList(formats));
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

        if (this.PubSubConnectionStateChanged != null)
            this.PubSubConnectionStateChanged.fire(sender,args);
    }

    public void ExploreHives(int offset,int length,EventHandler<CommandCallbackEventArgs> Callback) {
        // TODO: This is for server 0.5.0 which does not support list indexing for explore command.
        this.server.RunCommand(AvailableCommands.Explore,Callback,null);
    }



    /**
     * Returns a value indicating if the PubSub underlying service is connected or connecting.
     * @return a Boolean value indicating if the PubSub service is connected or connecting.
     */
    public Boolean isPubsubConnected() {
        ConnectionState cs = this.pubSub.GetConnectionState();
        return ((cs == ConnectionState.CONNECTED) || (cs == ConnectionState.CONNECTING));
    }

    protected enum ExecutorLevel { LocalStorage, Server };

    private class CommandData {

        AvailableCommands command;
        EventHandler<CommandCallbackEventArgs> callback;
        ExecutorLevel level;

        private CommandData(AvailableCommands command,EventHandler<CommandCallbackEventArgs> callback) {
            this.command = command;
            this.callback = callback;
        }

        AvailableCommands getCommand() {
            return this.command;
        }
        EventHandler<CommandCallbackEventArgs> getCallback() {
            return this.callback;
        }

        void setLevel(ExecutorLevel level) {
            this.level = level;
        }
        ExecutorLevel getLevel() {
            return this.level;
        }
    }
}

