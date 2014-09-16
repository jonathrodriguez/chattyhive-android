package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.HiveLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.backend.contentprovider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.CancelableEventArgs;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.ArrayList;

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
    /************************************************************************/
    /*                       STATIC MANAGEMENT                              */
    /************************************************************************/
    private static Boolean Initialized = false;
    private static Boolean PrivateInitialize() {
        if (Initialized) return false;

        AppBindingEvent = new Event<EventArgs>();
        DisposingRunningController = new Event<CancelableEventArgs>();
        RunningControllerDisposed = new Event<EventArgs>();
        ConnectionAvailabilityChanged = new Event<EventArgs>();

        DataProvider.Initialize();
        ServerCommand.Initialize();

        return (Initialized = true);
    }
    public static void Initialize() {
        Controller.PrivateInitialize();
        CookieHandler.setDefault(new CookieManager());
    }
    public static void Initialize(Object... LocalStorage) {
        PrivateInitialize();
        setLocalStorage(LocalStorage);
        DataProvider.Initialize(LocalStorage);
        CookieHandler.setDefault(new CookieManager());
    }
    public static void Initialize(CookieStore cookieStore) {
        PrivateInitialize();
        CookieHandler.setDefault(new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL));
    }
    public static void Initialize(CookieStore cookieStore, Object... LocalStorage) {
        PrivateInitialize();
        setLocalStorage(LocalStorage);
        DataProvider.Initialize(LocalStorage);

        CookieHandler.setDefault(new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL));
    }

    //COMMON STATIC
    private static Controller controller;

    public static Event<CancelableEventArgs> DisposingRunningController;
    public static Event<EventArgs> RunningControllerDisposed;

    public static Controller GetRunningController() {
        return GetRunningController(false);
    }
    public static Controller GetRunningController(Boolean initialize) {
        if ((controller == null) && (initialize))
            controller = new Controller();

        return controller;
    }
    public static Controller GetRunningController(Object... LocalStorage) {
        setLocalStorage(LocalStorage);
        return GetRunningController(true);
    }
    public static void DisposeRunningController() {
        Boolean disposeCanceled = false;

        if (DisposingRunningController != null) {
            CancelableEventArgs eventArgs = new CancelableEventArgs();
            DisposingRunningController.fire(controller,eventArgs);
            disposeCanceled = eventArgs.isCanceled();
        }

        if (disposeCanceled) return;

        if (RunningControllerDisposed != null)
            RunningControllerDisposed.fire(controller,EventArgs.Empty());

        controller = null;
    }

    //APP STATIC
    private static Boolean appBounded = false;
    private static Boolean svcBounded = false;
    public static Event<EventArgs> AppBindingEvent;

    public static Boolean isAppBounded() {
        return appBounded;
    }
    public static void bindApp(Method getLogin, Object main) {
        if (appBounded) return;
        appBounded = true;
        if (AppBindingEvent != null)
            AppBindingEvent.fire(controller, EventArgs.Empty());

        DataProvider dataProvider = DataProvider.GetDataProvider();

        if (!dataProvider.isServerConnected()) {
            if (LoginLocalStorage.RecoverLoginPassword() == null) {
                try {
                    getLogin.invoke(main);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                dataProvider.Connect();
            }
        } else {
            if (controller == null) GetRunningController(true);
            controller.onServerConnectionStateChanged(controller,new ConnectionEventArgs(true));
        }
    }
    public static void unbindApp() {
        if (!appBounded) return;
        appBounded = false;
        if (AppBindingEvent != null)
            AppBindingEvent.fire(controller,EventArgs.Empty());
    }
    public static void bindSvc() {
        if (svcBounded) return;
        svcBounded = true;

        DataProvider dataProvider = DataProvider.GetDataProvider();
        if ((!dataProvider.isServerConnected()) && (LoginLocalStorage.RecoverLoginPassword() != null))
            dataProvider.Connect();
        else if (dataProvider.isServerConnected() && (!appBounded)) {
            if (controller == null) GetRunningController(true);
            controller.onServerConnectionStateChanged(controller,new ConnectionEventArgs(true));
        }
    }
    public static void unbindSvc() {
        if (!svcBounded) return;
        svcBounded = false;
    }
    //STORAGE STATIC

    @Deprecated
    public static GroupLocalStorageInterface GroupLocalStorage;
    @Deprecated
    public static HiveLocalStorageInterface HiveLocalStorage;
    @Deprecated
    public static LoginLocalStorageInterface LoginLocalStorage;
    @Deprecated
    public static MessageLocalStorageInterface MessageLocalStorage;
    @Deprecated
    public static UserLocalStorageInterface UserLocalStorage;
    @Deprecated
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


    //CONNECTION STATIC
    public static Event<EventArgs> ConnectionAvailabilityChanged;

    public static Boolean getNetworkAvailable() { return DataProvider.isConnectionAvailable(); }
    public static void setNetworkAvailable(Boolean value) { DataProvider.setConnectionAvailable(value); }

    /************************************************************************/
    /************************************************************************/
    /************************************************************************/

    /************************************************************************/
    /*                       DYNAMIC MANAGEMENT                             */
    /************************************************************************/
    private DataProvider dataProvider;

    /************************************************************************/
    //CONSTRUCTORS

    public Controller () {
        this.ServerConnectionStateChanged = new Event<ConnectionEventArgs>();
        this.ExploreHivesListChange = new Event<EventArgs>();
        this.HiveJoined = new Event<EventArgs>();
        this.PubSubConnectionStateChanged = new Event<PubSubConnectionEventArgs>();

        this.dataProvider = DataProvider.GetDataProvider(true);

        Hive.Initialize(this,HiveLocalStorage);
        Group.Initialize(this,GroupLocalStorage);
        Chat.Initialize(this,MessageLocalStorage);
        User.Initialize(UserLocalStorage);

        try {
            DataProvider.ConnectionAvailabilityChanged.add(new EventHandler<EventArgs>(this,"onConnectionAvailabilityChanged",EventArgs.class));
            this.dataProvider.ServerConnectionStateChanged.add(new EventHandler<ConnectionEventArgs>(this,"onServerConnectionStateChanged",ConnectionEventArgs.class));

            this.dataProvider.onHiveJoined.add(new EventHandler<CommandCallbackEventArgs>(this,"onJoinHiveCallback",CommandCallbackEventArgs.class));


        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /************************************************************************/
    //CONNECTION MANAGEMENT

    public Event<ConnectionEventArgs> ServerConnectionStateChanged;
    public Event<PubSubConnectionEventArgs> PubSubConnectionStateChanged;

    public void onPubSubConnectionStateChanged(Object sender, PubSubConnectionEventArgs eventArgs) {
        if (this.PubSubConnectionStateChanged != null)
            this.PubSubConnectionStateChanged.fire(sender,eventArgs);
    }

    public void onConnectionAvailabilityChanged(Object sender, EventArgs eventArgs) {
        if (ConnectionAvailabilityChanged != null)
            ConnectionAvailabilityChanged.fire(sender,eventArgs);
    }

    public Boolean isServerConnected() {
        return this.dataProvider.isServerConnected();
    }
    public void Connect() {
        if ((!this.dataProvider.isServerConnected()) || (!this.dataProvider.isPubsubConnected()))
            this.dataProvider.Connect();
    }
    public void Disconnect() {
        this.dataProvider.Disconnect();
    }
    public void onServerConnectionStateChanged(Object sender, ConnectionEventArgs eventArgs) {
        if (this.dataProvider.isServerConnected()) {
            ArrayList<AvailableCommands> commandSequence = new ArrayList<AvailableCommands>();
            //Define command sequence
            if (Controller.isAppBounded()) {
                //Load Home
                //commandSequence.add(AvailableCommands.Home);
                if (!svcBounded)
                    commandSequence.add(AvailableCommands.ChatList);
                commandSequence.add(AvailableCommands.LocalProfile);
                if (svcBounded)
                    commandSequence.add(AvailableCommands.ChatList);
            } else if (svcBounded) {
                commandSequence.add(AvailableCommands.ChatList);
            }
            //Execute command sequence
            for(AvailableCommands command : commandSequence)
                dataProvider.InvokeServerCommand(command,null);
        }
        if (this.ServerConnectionStateChanged != null)
            this.ServerConnectionStateChanged.fire(sender,eventArgs);
    }
    /************************************************************************/
    //EXPLORE


    /************************************************************************/



    // BusinessObjects


    private ArrayList<Hive> exploreHives = new ArrayList<Hive>();
    public ArrayList<Hive> getExploreHives() { return exploreHives; }

    public Event<EventArgs> ExploreHivesListChange;
    public Event<EventArgs> HiveJoined;

    /**
     * Establishes the server user.
     * @param user
     */
    public void setServerUser(ServerUser user) {
        this.dataProvider.setUser(user);
    }

    /**
     * Changes the server app.
     * @param serverApp
     */
    public void setServerApp(String serverApp) {
        this.dataProvider.setServerApp(serverApp);
    }

    public Boolean isConnected() {
        return (this.dataProvider.isServerConnected() && this.dataProvider.isServerConnected());
    }


    public void JoinHive(String hive) {
        for (Hive h : exploreHives)
            if (h.getNameUrl().equalsIgnoreCase(hive))
                this.dataProvider.JoinHive(h);
    }

    public void onJoinHiveCallback(Object sender, CommandCallbackEventArgs eventArgs)  {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();

        Boolean exploreHivesChanged = false;
        Boolean hiveJoined = false;

        for(Format format : receivedFormats)
            if (format instanceof COMMON) {
                if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                    hiveJoined = true;
                    ArrayList<Format> sentFormats = eventArgs.getSentFormats();
                    for (Format sentFormat : sentFormats)
                        if (sentFormat instanceof HIVE_ID)
                            for (Hive h : exploreHives)
                                if (h.getNameUrl().equalsIgnoreCase(((HIVE_ID) sentFormat).NAME_URL))
                                    exploreHivesChanged = (exploreHivesChanged || exploreHives.remove(h));
                }
                break;
            }

        if ((exploreHivesChanged) && (ExploreHivesListChange != null))
            ExploreHivesListChange.fire(exploreHives, EventArgs.Empty());

        if ((hiveJoined) && (HiveJoined != null))
            HiveJoined.fire(exploreHives,EventArgs.Empty());
    }

    public void JoinTMP (String channel) {
        this.dataProvider.Join(channel);
    }

    public void Join(String channel) {
        //this.dataProvider.Join(channel);
    }

    public void Leave(String channel) {
        //this.dataProvider.Leave(channel);
    }

    /**
     * This method permits application to recover some hives from explore server list.
     * @param
     */
    public void exploreHives(int offset,int length) {
        if (offset == 0) { exploreHives.clear(); }
        if (!StaticParameters.StandAlone) {
            try {
                this.dataProvider.ExploreHives(offset,length,new EventHandler<CommandCallbackEventArgs>(this,"onExploreHivesCallback",CommandCallbackEventArgs.class));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void onExploreHivesCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();

        for (Format format : receivedFormats)
            if (format instanceof HIVE)
                this.exploreHives.add(new Hive((HIVE)format));

        if (this.ExploreHivesListChange != null)
            this.ExploreHivesListChange.fire(this.exploreHives,EventArgs.Empty());
    }

    public void checkEmail(String email,EventHandler<CommandCallbackEventArgs> Callback) {
        USER_EMAIL user_email = new USER_EMAIL();
        user_email.EMAIL = email;
        this.dataProvider.InvokeServerCommand(AvailableCommands.EmailCheck,Callback,user_email);
    }

    public void checkUsername(String username,EventHandler<CommandCallbackEventArgs> Callback) {
        /*USER_USERNAME user_username = new USER_USERNAME(); //TODO: implement server function
        user_username.username = username;
        this.dataProvider.InvokeServerCommand(AvailableCommands.UsernameCheck,Callback,user_username);*/
        COMMON common = new COMMON();
        common.STATUS = "OK";
        ArrayList<Format> rf = new ArrayList<Format>();
        rf.add(common);
        CommandCallbackEventArgs eventArgs = new CommandCallbackEventArgs(rf,null);
        try {
            Callback.Invoke(this,eventArgs);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void clearUserData() {
        this.dataProvider.clearSession();
        User.removeMe();
    }

    public void clearAllChats() {
        Group.clearGroups();
    }

    private void clearChat(String channelUnicode) {
        Group.removeGroup(channelUnicode);
    }

}
