package com.chattyhive.backend;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Home.Cards.HiveMessageCard;
import com.chattyhive.backend.businessobjects.Home.HomeCard;
import com.chattyhive.backend.businessobjects.Users.ProfileLevel;
import com.chattyhive.backend.businessobjects.Users.ProfileType;
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
import com.chattyhive.backend.contentprovider.formats.HIVE_LIST;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.USERNAME;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.chattyhive.backend.contentprovider.formats.USER_PROFILE;
import com.chattyhive.backend.contentprovider.local.LocalStorageInterface;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.CancelableEventArgs;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

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
        return controller;
    }
    public static Controller GetRunningController(LocalStorageInterface localStorage) {
        if (controller == null)
            controller = new Controller(localStorage);

        return controller;
    }
    public static Controller GetRunningController(LocalStorageInterface localStorage, Object... LocalStorage) {
        setLocalStorage(LocalStorage);
        return GetRunningController(localStorage);
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

        //if (!dataProvider.isServerConnected()) {
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
        //} else {
        //    if (controller == null) GetRunningController(true);
        //    controller.onServerConnectionStateChanged(controller,new ConnectionEventArgs(true));
        //}
    }
    public static void unbindApp() {
        if (!appBounded) return;
        appBounded = false;
        if (AppBindingEvent != null)
            AppBindingEvent.fire(controller,EventArgs.Empty());
    }
    public static void bindSvc(LocalStorageInterface localStorage) {
        if (svcBounded) return;
        svcBounded = true;

        DataProvider dataProvider = DataProvider.GetDataProvider();
        if ((!dataProvider.isServerConnected()) && (LoginLocalStorage.RecoverLoginPassword() != null))
            dataProvider.Connect();
        else if (dataProvider.isServerConnected() && (!appBounded)) {
            if (controller == null) GetRunningController(localStorage);
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

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }
    /************************************************************************/
    //CONSTRUCTORS

    public Controller (LocalStorageInterface localStorage) {
        this.ServerConnectionStateChanged = new Event<ConnectionEventArgs>();
        this.ExploreHivesListChange = new Event<EventArgs>();
        this.HiveJoined = new Event<EventArgs>();
        this.PubSubConnectionStateChanged = new Event<PubSubConnectionEventArgs>();

        this.dataProvider = DataProvider.GetDataProvider(localStorage);

        Chat.Initialize(this,MessageLocalStorage);
        Group.Initialize(this,GroupLocalStorage);
        Hive.Initialize(this,HiveLocalStorage);
        Group.RecoverLocalGroups();

        this.InitializeUsers();
        this.InitializeHome();

        DataProvider.ConnectionAvailabilityChanged.add(new EventHandler<EventArgs>(this,"onConnectionAvailabilityChanged",EventArgs.class));
        this.dataProvider.ServerConnectionStateChanged.add(new EventHandler<ConnectionEventArgs>(this,"onServerConnectionStateChanged",ConnectionEventArgs.class));

        this.dataProvider.onHiveJoined.add(new EventHandler<CommandCallbackEventArgs>(this,"onJoinHiveCallback",CommandCallbackEventArgs.class));
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
                    ArrayList<Hive> toRemove = new ArrayList<Hive>();
                    for (Format sentFormat : sentFormats)
                        if (sentFormat instanceof HIVE_ID)
                            for (Hive h : exploreHives)
                                if (h.getNameUrl().equalsIgnoreCase(((HIVE_ID) sentFormat).NAME_URL))
                                    toRemove.add(h);

                    for (Hive h : toRemove)
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
        this.dataProvider.ExploreHives(offset,length,new EventHandler<CommandCallbackEventArgs>(this,"onExploreHivesCallback",CommandCallbackEventArgs.class));
    }

    public void onExploreHivesCallback(Object sender,CommandCallbackEventArgs eventArgs) {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();

        for (Format format : receivedFormats)
            if (format instanceof HIVE)
                this.exploreHives.add(new Hive((HIVE)format));
            else if (format instanceof HIVE_LIST)
                for (HIVE hive : ((HIVE_LIST) format).LIST)
                    this.exploreHives.add(new Hive(hive));

        if (this.ExploreHivesListChange != null)
            this.ExploreHivesListChange.fire(this.exploreHives,EventArgs.Empty());
    }

    public void clearUserData() {
        this.dataProvider.clearSession();
        me = null;
    }

    public void clearAllChats() {
        Group.clearGroups();
    }

    private void clearChat(String channelUnicode) {
        Group.removeGroup(channelUnicode);
    }


    /*******************************************************************************************/
    /*******************************************************************************************/
    /*                            USERS                                                        */
    /*******************************************************************************************/
    /*******************************************************************************************/
    public void CheckEmail(String email, EventHandler<CommandCallbackEventArgs> Callback) {
        if (!email.contains("@")) return;

        String userPart = email.split("@")[0];
        String serverPart = email.split("@")[1];
        if ((userPart.isEmpty()) || (serverPart.isEmpty())) return;

        USER_EMAIL user_email = new USER_EMAIL();
        user_email.EMAIL_USER_PART = userPart;
        user_email.EMAIL_SERVER_PART = serverPart;
        this.dataProvider.RunCommand(AvailableCommands.EmailCheck, Callback, user_email);
    }
    public void CheckUsername(String username, EventHandler<CommandCallbackEventArgs> Callback) {
        USERNAME user_username = new USERNAME();
        user_username.PUBLIC_NAME = username;
        this.dataProvider.RunCommand(AvailableCommands.UsernameCheck,Callback,user_username);
    }

    private TreeMap<String,User> knownUsers;
    private User me;

    private void InitializeUsers() {
        this.knownUsers = new TreeMap<String, User>();
        this.LocalUserReceived = new Event<EventArgs>();
    }
    private User getUser(String userID,Format format) {
        if (this.knownUsers == null) throw new IllegalStateException("Users must be initialized.");
        else if (userID == null) throw new NullPointerException("UserID must not be null.");
        else if (userID.isEmpty()) throw new IllegalArgumentException("UserID must not be empty.");

        if (this.knownUsers.containsKey(userID))
            return this.knownUsers.get(userID);
        else if ((me != null) && (me.getUserID().equalsIgnoreCase(userID))) {
            return me;
        }
        else {
            User u = new User(format,this);
            if (!u.isMe()) {
                this.knownUsers.put(userID, u);
            }
            else
                me = u;
            return u;
        }
    }
    public User getUser(PROFILE_ID profile_id) {
        if (this.knownUsers == null) throw new IllegalStateException("Users must be initialized.");
        else if (profile_id == null) throw new NullPointerException("PROFILE_ID must not be null.");
        else if ((profile_id.USER_ID == null) || profile_id.USER_ID.isEmpty()) throw new IllegalArgumentException("PROFILE_ID must not be empty.");

        String userID = profile_id.USER_ID;

        User u;

        if (this.knownUsers.containsKey(userID)) {
            u = this.knownUsers.get(userID);
            u.loadProfile(profile_id);
        }
        else if ((me != null) && (me.getUserID() != null) && (me.getUserID().equalsIgnoreCase(userID))) {
            u = me;
            u.loadProfile(profile_id);
        }
        else {
            u = new User(userID,profile_id,this);
            u.UserLoaded.add(new EventHandler<EventArgs>(this,"onUserLoaded",EventArgs.class));
        }

        return u;
    }
    public void updateUser(String userID,Format format) {
        if (this.knownUsers == null) throw new IllegalStateException("Users must be initialized.");

        if (format instanceof LOCAL_USER_PROFILE) {
            String mineID = "";
            if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID != null) && (!((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID.isEmpty()))
                mineID = ((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE.USER_ID;
            else if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID != null) && (!((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID.isEmpty()))
                mineID = ((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE.USER_ID;
            else if ((((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID != null) && (!((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID.isEmpty()))
                mineID = ((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE.USER_ID;
            else if ((((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID != null) && (!((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID.isEmpty()))
                mineID = ((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE.USER_ID;

            if (this.me != null)
                this.getMe().fromFormat(format);
            else if ((!mineID.isEmpty()) && this.knownUsers.containsKey(mineID)) {
                this.me = this.knownUsers.get(mineID);
                this.knownUsers.remove(mineID);
                this.getMe().fromFormat(format);
            }
            else
                this.me = new User(format, this);

            if (LocalUserReceived != null)
                LocalUserReceived.fire(this,EventArgs.Empty());

        } else if (format instanceof USER_PROFILE) {
            if (userID == null) throw new NullPointerException("UserID must not be null.");
            else if (userID.isEmpty()) throw new IllegalArgumentException("UserID must not be empty.");

            if (this.knownUsers.containsKey(userID))
                this.knownUsers.get(userID).fromFormat(format);
            else
                this.knownUsers.put(userID,new User(format,this));
        }
    }

    public void onUserLoaded(Object sender,EventArgs eventArgs) {
        if (!(sender instanceof User)) return;

        if (!((User) sender).isMe()) {
            if ((this.knownUsers.containsKey(((User) sender).getUserID())) && (this.knownUsers.get(((User) sender).getUserID()) == sender))
                return;
            this.knownUsers.put(((User) sender).getUserID(), (User) sender);
        } else if (sender != this.me) {
            this.me = (User)sender;
            if (LocalUserReceived != null)
                LocalUserReceived.fire(this,EventArgs.Empty());
        }
    }

    public Event<EventArgs> LocalUserReceived;

    public void unloadUserProfiles() {
        for (User user : this.knownUsers.values())
            user.unloadProfile(ProfileLevel.Basic);
    }
    public User getMe() {
        return this.me;
    }
    public void setMe(User me) {
        this.me = me;
    }

    /*******************************************************************************************/
    /*******************************************************************************************/
    /*                            HOME                                                         */
    /*******************************************************************************************/
    /*******************************************************************************************/

    private TreeMap<Date,HomeCard> homeCards = null;
    public Event<EventArgs> HomeReceived;

    private void InitializeHome() {
        this.HomeReceived = new Event<EventArgs>();
        Hive.HiveListChanged.add(new EventHandler<EventArgs>(this,"onHiveListChanged",EventArgs.class));
    }
    public ArrayList<HomeCard> getHomeCards() {
        ArrayList<HomeCard> result = new ArrayList<HomeCard>();

        if (homeCards != null)
            result.addAll(this.homeCards.values());

        return result;
    }

    public void RequestHome() {
        //TODO: Request home to the server when implemented.

        new Thread() {
            @Override
            public void run() {
                if (homeCards != null)
                    homeCards.clear();
                else
                    homeCards = new TreeMap<Date, HomeCard>();

                int hiveCount = Hive.getHiveCount();
                Hive hive;
                Message message;
                HiveMessageCard homeCard;
                for (int i = 0; i < hiveCount; i++) {
                    hive = Hive.getHiveByIndex(i);
                    if ((hive != null) && (hive.getPublicChat() != null) && (hive.getPublicChat().getChat() != null) && (hive.getPublicChat().getChat().getCount() > 0)) {
                        message = hive.getPublicChat().getChat().getLastMessage();
                        homeCard = new HiveMessageCard(message);
                        homeCards.put(message.getOrdinationTimeStamp(), homeCard);
                    }
                }

                HomeReceived.fire(dataProvider,EventArgs.Empty());
            }
        }.start();
    }

    public void onHiveListChanged (Object sender, EventArgs eventArgs) {
        if ((sender == null) || ((sender instanceof Hive) && (((Hive) sender).getPublicChat() != null) && (((Hive) sender).getPublicChat().getChat() != null) && (((Hive) sender).getPublicChat().getChat().getCount() > 0))) {
            this.RequestHome();
        }
    }
}
