package com.chattyhive.Core.ContentProvider;

import com.chattyhive.Core.Controller;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.Hive;
import com.chattyhive.Core.BusinessObjects.Explore;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.ChatLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_LIST;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_SYNC;
import com.chattyhive.Core.ContentProvider.Formats.COMMON;
import com.chattyhive.Core.ContentProvider.Formats.EXPLORE_FILTER;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.HIVE;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_ID;
import com.chattyhive.Core.ContentProvider.Formats.INTERVAL;
import com.chattyhive.Core.ContentProvider.Formats.LOCAL_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_ACK;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_LIST;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE;
import com.chattyhive.Core.ContentProvider.local.LocalStorageInterface;
import com.chattyhive.Core.ContentProvider.pubsubservice.ConnectionState;
import com.chattyhive.Core.ContentProvider.pubsubservice.ConnectionStateChange;
import com.chattyhive.Core.ContentProvider.Server.Server;
import com.chattyhive.Core.ContentProvider.Server.UserSession;
import com.chattyhive.Core.ContentProvider.pubsubservice.PubSub;

import com.chattyhive.Core.Util.Events.CancelableEventArgs;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.ConnectionEventArgs;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Events.FormatReceivedEventArgs;
import com.chattyhive.Core.Util.Events.PubSubChannelEventArgs;
import com.chattyhive.Core.Util.Events.PubSubConnectionEventArgs;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jonathan on 11/12/13.
 * This class is intended to provide a generic interface to access data independently
 * from where data comes from. Possible data origins are local, server and pusher.
 */
public class DataProvider {

















    /*******************************************************************************************/
    /*******************************************************************************************/
    /*******************************************************************************************/
    /*******************************************************************************************/
    /*******************************************************************************************/


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
        return dataProvider;
    }
    public static DataProvider GetDataProvider(LocalStorageInterface localStorage) {
        if (dataProvider == null)
            dataProvider = new DataProvider(localStorage);

        return dataProvider;
    }
    public static DataProvider GetDataProvider(LocalStorageInterface localStorage, Object... LocalStorage) {
        setLocalStorage(LocalStorage);
        return GetDataProvider(localStorage);
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
    private static ChatLocalStorageInterface GroupLocalStorage;
    private static HiveLocalStorageInterface HiveLocalStorage;
    private static LoginLocalStorageInterface LoginLocalStorage;
    private static MessageLocalStorageInterface MessageLocalStorage;
    private static UserLocalStorageInterface UserLocalStorage;

    public static void setLocalStorage(Object... LocalStorage) {
        for (Object localStorage : LocalStorage) {
            if ((localStorage instanceof ChatLocalStorageInterface) && (GroupLocalStorage == null)) {
                GroupLocalStorage = (ChatLocalStorageInterface) localStorage;
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
    private Controller controller;

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
        this.controller = Controller.GetRunningController(); //TODO: this must be sent in another way

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
            if (this.userSession == null) {
                this.userSession = new UserSession(LoginLocalStorage);
            }
            if ((!this.csrfTokenValid) || (StaticParameters.StandAlone)) this.server.StartSession();
            if ((!this.sessionValid) || (StaticParameters.StandAlone)) this.RunCommand(AvailableCommands.Login,null,Format.getFormat(this.userSession.toJson()));

            if (!StaticParameters.StandAlone) {
                this.targetState = ConnectionState.CONNECTED;
                this.pubSub.Connect();
            }
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
        if ((eventArgs.getReceivedFormats() == null) || (eventArgs.countReceivedFormats() == 0)) return;
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
            if ((format instanceof USER_PROFILE) || (format instanceof LOCAL_USER_PROFILE)) {
                UserProfileFormats.add(format);
            }
            if ((format instanceof HIVE) || (format instanceof HIVE_ID)) {
                HiveProfileFormats.add(format);
            }
            if ((format instanceof CHAT) || (format instanceof CHAT_ID) || (format instanceof CHAT_SYNC)) {
                ChatProfileFormats.add(format);
            }
            if (format instanceof CHAT_LIST) {
                if (((CHAT_LIST) format).LIST != null)
                    ChatProfileFormats.addAll(((CHAT_LIST) format).LIST);
            }
        }

        if ((onMessageReceived != null) && (MessageFormats.size() > 0))
            onMessageReceived.fire(this,new FormatReceivedEventArgs(MessageFormats));

        if (UserProfileFormats.size() > 0)
            this.ProcessUserReceivedFormats(UserProfileFormats);
            //onUserProfileReceived.fire(this,new FormatReceivedEventArgs(UserProfileFormats));

        if ((onHiveProfileReceived != null) && (HiveProfileFormats.size() > 0))
            onHiveProfileReceived.fire(this,new FormatReceivedEventArgs(HiveProfileFormats));

        if ((onChatProfileReceived != null) && (ChatProfileFormats.size() > 0))
            onChatProfileReceived.fire(this,new FormatReceivedEventArgs(ChatProfileFormats));
    }

    private void ProcessUserReceivedFormats(Collection<Format> receivedFormats) {
        if (this.controller == null)
            this.controller = Controller.GetRunningController(this.localStorage);
        for (Format format : receivedFormats) {
            if (format instanceof LOCAL_USER_PROFILE) {
                UserLocalStorage.StoreLocalUserProfile(format.toJSON().toString());
                this.controller.updateUser(null,format);
            } else if (format instanceof USER_PROFILE) {
                String userID = null;
                if ((((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID != null) && (!((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID.isEmpty()))
                    userID = ((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID;
                else if ((((USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (((USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID != null) && (!((USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID.isEmpty()))
                    userID = ((USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID;
                else if ((((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID != null) && (!((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID.isEmpty()))
                    userID = ((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID;
                else if ((((USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (((USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID != null) && (!((USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID.isEmpty()))
                    userID = ((USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID;

                if (userID == null)
                    continue;

                USER_PROFILE updateFormat = null;
                String remote_profile = UserLocalStorage.RecoverCompleteUserProfile(userID);
                if ((remote_profile != null) && (!remote_profile.isEmpty()))
                    updateFormat = new USER_PROFILE(new JsonParser().parse(remote_profile));
                if (updateFormat != null) {
                    if (((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null)
                        updateFormat.USER_BASIC_PRIVATE_PROFILE = ((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE;
                    if (((USER_PROFILE) format).USER_PRIVATE_PROFILE != null)
                        updateFormat.USER_PRIVATE_PROFILE = ((USER_PROFILE) format).USER_PRIVATE_PROFILE;
                    if (((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null)
                        updateFormat.USER_BASIC_PUBLIC_PROFILE = ((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE;
                    if (((USER_PROFILE) format).USER_PUBLIC_PROFILE != null)
                        updateFormat.USER_PUBLIC_PROFILE = ((USER_PROFILE) format).USER_PUBLIC_PROFILE;

                    UserLocalStorage.StoreCompleteUserProfile(userID,updateFormat.toJSON().toString());
                    this.controller.updateUser(userID,updateFormat);
                } else {
                    UserLocalStorage.StoreCompleteUserProfile(userID,format.toJSON().toString());
                    this.controller.updateUser(userID,format);
                }
            }
        }
    }

    /************************************************************************/
    private UserSession userSession;

    private PubSub pubSub;
    private ConnectionState targetState;
    private Boolean networkAvailable = true;


    /**
     * Changes the server user.
     * @param newUser the new server user.
     */
    public void setUser(UserSession newUser) {
        this.userSession = newUser;
        this.server.setUserSession(this.userSession);
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

        this.server.RunCommand(AvailableCommands.Join,new EventHandler<CommandCallbackEventArgs>(this,"onHiveJoinedCallback",CommandCallbackEventArgs.class),null,hive.toFormat(new HIVE_ID()));
    }

    public Event<CommandCallbackEventArgs> onHiveJoined;

    public void onHiveJoinedCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();

        HIVE_ID hive_id = null;
        Hive hive = null;

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
                    hive = Hive.getHive(hive_id.NAME_URL);
                    Chat g = hive.getPublicChat();
                    if (g != null)
                        g.fromFormat(format);
                    else {
                        g = Chat.getChat(format);
                        hive.setPublicChat(g);
                    }
                }

        if (onHiveJoined != null)
            onHiveJoined.fire(hive,eventArgs);
    }

    public void Join(String channel) {
        /*try{
            this.pubSub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
            this.pubSub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionEvent", PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }*/
        if (StaticParameters.StandAlone) return;

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
        this.server.RunCommand(command, Callback, null, formats);
    }

    public void RunCommand(AvailableCommands command,EventHandler<CommandCallbackEventArgs> Callback,Format... formats) {
        Integer commandIndex = this.nextCommandIndex++;
        CommandData commandData = new CommandData(command,Callback);
        this.commandStack.put(commandIndex,commandData);

        if (this.localStorage != null) {
            commandData.setLevel(ExecutorLevel.LocalStorage);
            this.localStorage.PreRunCommand(command, new EventHandler<CommandCallbackEventArgs>(this, "CommandCallback", CommandCallbackEventArgs.class), commandIndex, formats);
        } else
            System.out.println("LocalStorage is NULL");

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
            } else
                System.out.println("LocalStorage is NULL");

            if (Callback.getCommand() == AvailableCommands.Register)
                this.ProcessRegistration(Callback);

            if (Callback.getCommand() == AvailableCommands.UpdateProfile)
                this.localStorage.PreRunCommand(AvailableCommands.LocalProfile,new EventHandler<CommandCallbackEventArgs>(this.controller.getMe(),"loadCallback",CommandCallbackEventArgs.class),null,null);
        }
        try {
            if (commandData.getCallback() != null)
                commandData.getCallback().Invoke(sender, Callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ProcessRegistration(CommandCallbackEventArgs Callback) {
        COMMON common = null;
        LOCAL_USER_PROFILE lup = null;
        for (Format format : Callback.getReceivedFormats())
            if (format instanceof COMMON)
                common = (COMMON) format;

        for (Format format : Callback.getSentFormats())
            if (format instanceof LOCAL_USER_PROFILE)
                lup = (LOCAL_USER_PROFILE) format;

        if ((common != null) && (lup != null) && (common.STATUS.equalsIgnoreCase("OK"))) {
            this.setUser(new UserSession(lup.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME,lup.PASS));
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

    public void ExploreHives(int offset,int length,Explore.SortType sortType,String categoryCode,EventHandler<CommandCallbackEventArgs> Callback) {
        // TODO: This is for server 0.5.0 which does not support list indexing for explore command.
        //this.server.RunCommand(AvailableCommands.Explore,Callback,null,null);
        EXPLORE_FILTER explore_filter = new EXPLORE_FILTER();
        explore_filter.TYPE = sortType.name();
        explore_filter.RESULT_INTERVAL = new INTERVAL();
        explore_filter.RESULT_INTERVAL.START_INDEX = String.valueOf(offset);
        explore_filter.RESULT_INTERVAL.COUNT = length;

        if ((categoryCode != null) && (!categoryCode.isEmpty()))
            explore_filter.CATEGORY = categoryCode;

        this.server.RunCommand(AvailableCommands.Explore,Callback,null,explore_filter);
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

    //IMAGE MANAGEMENT
    public InputStream getImage(String url) {
        return localStorage.getImage(url);
    }
}

