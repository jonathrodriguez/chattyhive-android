package com.chattyhive.Core.BusinessObjects.Users;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Home.Home;
import com.chattyhive.Core.BusinessObjects.Subscriptions.SubscribableList;
import com.chattyhive.Core.BusinessObjects.Users.Requests.RequestList;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.Formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.BASIC_PUBLIC_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.LOCAL_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.LOGIN;
import com.chattyhive.Core.ContentProvider.Formats.PRIVATE_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.PROFILE_ID;
import com.chattyhive.Core.ContentProvider.Formats.PUBLIC_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;

import java.util.List;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    private Controller controller;

    // Members
    private Boolean loading;

    private Boolean isMe = false;
    private String email; //Only for local user;

    private String userID; //ID for any user. (This will be the public username).
    private PublicProfile userPublicProfile; //Public profile for any user.
    private PrivateProfile userPrivateProfile; //Private profile for any user.

    private Home home;
    private SubscribableList<Hive> hiveSubscriptionsList;
    private SubscribableList<Chat> chatSubscriptionsList;
    private UserList friendList;
    private RequestList requestList;

    // Events
    public Event<EventArgs> UserLoaded;

    // Constructors
    private User() {
        this.userPrivateProfile = new PrivateProfile();
        this.userPublicProfile = new PublicProfile();

        this.requestList = new RequestList();
        this.friendList = new UserList();
        this.chatSubscriptionsList = new SubscribableList<Chat>();
        this.hiveSubscriptionsList = new SubscribableList<Hive>();

        this.UserLoaded = new Event<EventArgs>();
    }

    public User(Controller controller, String email) {
        this();
        this.controller = controller;
        this.email = email;
    }

    public User(Controller controller, String accountID, boolean loading) {
        this();

        this.controller = controller;

        this.isMe = true;
        this.loading = true;

        this.userID = accountID;

        PROFILE_ID requestProfile = new PROFILE_ID();
        requestProfile.PROFILE_TYPE = "";
        requestProfile.USER_ID = this.userID;

        Command command = new Command(AvailableCommands.UserProfile,requestProfile);
        command.addCallbackDelegate(new CallbackDelegate(this,"loadCallback",Command.class));

        this.controller.getDataProvider().runCommand(accountID, command, CommandQueue.Priority.RealTime);
    }

    public User(Controller controller, Format format) {
        this();

        this.controller = controller;

        this.isMe = false; //Unnecesary.
        this.loading = true;

        if (!this.fromFormat(format))
            throw new IllegalArgumentException("LOCAL_USER_PROFILE or USER_PROFILE expected.");
    }

    public User(Controller controller, PROFILE_ID requestProfile, CommandQueue.Priority priority, String accountID) {
        this();
        this.controller = controller;

        this.isMe = false; //Unnecesary.
        this.loading = true;

        Command command = new Command(AvailableCommands.UserProfile,requestProfile);
        command.addCallbackDelegate(new CallbackDelegate(this,"loadCallback",Command.class));
        this.controller.getDataProvider().runCommand(accountID,command,priority);
    }

    // Simple Getters/Setters
    public Boolean isLoading() {
        return this.loading;
    }

    public Boolean isMe() {
        return this.isMe;
    }

    public String getEmail() {
        return this.email;
    }

    public String getUserID() {
        return this.userID;
    }
    private void setUserID(String value) {
        this.userID = value;
    }

    public Home getHome() {
        return this.home;
    }

    public SubscribableList<Hive> getHiveSubscriptionsList() {
        return this.hiveSubscriptionsList;
    }

    public SubscribableList<Chat> getChatSubscriptionsList() {
        return this.chatSubscriptionsList;
    }

    public UserList getFriendList() { //FIXME: return unmodifiable list.
        return this.friendList;
    }

    public PublicProfile getUserPublicProfile() {
        return this.userPublicProfile;
    }
    public PrivateProfile getUserPrivateProfile() {
        return this.userPrivateProfile;
    }

    // Complex Getters/Setters
    public void updateEmail(String value) { //TODO: Implement server request
        this.email = value;
    }

    // Methods


    // Callbacks
    public void loadCallback (Command command) {
        if (command.getResultFormats().size() == 0) return;
        List<Format> receivedFormats = command.getResultFormats();
        for (Format format : receivedFormats) {
            if ((format instanceof LOCAL_USER_PROFILE) || (format instanceof USER_PROFILE))
                this.fromFormat(format);
        }
    }

    /*************************************/
    /*     COMMUNICATION METHODS         */
    /*************************************/
    public void Register(String password,CallbackDelegate Callback) {
        LOGIN login = new LOGIN();
        login.USER = this.email;
        login.PASS = password;
        this.userPrivateProfile.loadedProfileLevel = ProfileLevel.Complete;
        this.userPublicProfile.loadedProfileLevel = ProfileLevel.Complete;
        this.userID = this.userPublicProfile.getPublicName();
        this.userPublicProfile.userID = this.userID;
        this.userPrivateProfile.userID = this.userID;
        LOCAL_USER_PROFILE lup = (LOCAL_USER_PROFILE)this.toFormat(new LOCAL_USER_PROFILE());
        lup.PASS = password;
        Command registerCommand = new Command(AvailableCommands.Register,login,lup);
        registerCommand.addCallbackDelegate(Callback);
        this.controller.getDataProvider().runCommand(registerCommand, CommandQueue.Priority.RealTime);
    }
    public void EditProfile(CallbackDelegate Callback,User newUserData,String accountID) {
        // TODO: compare newUser with this and send only fields which differ.
        this.getUserPrivateProfile().setStatusMessage(newUserData.getUserPrivateProfile().getStatusMessage());
        this.getUserPublicProfile().setStatusMessage(newUserData.getUserPublicProfile().getStatusMessage());
        Command updateProfile = new Command(AvailableCommands.UpdateProfile,newUserData.toFormat(new LOCAL_USER_PROFILE()));
        updateProfile.addCallbackDelegate(Callback);
        this.controller.getDataProvider().runCommand(accountID, updateProfile, CommandQueue.Priority.High);
    }

    public void loadProfile(ProfileType profileType, ProfileLevel profileLevel, String accountID) {
        if (profileLevel == ProfileLevel.None) return;

        PROFILE_ID profile_id = new PROFILE_ID();
        profile_id.USER_ID = this.userID;

        if (profileType == ProfileType.PUBLIC) {
            if ((this.userPublicProfile != null) && (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal())) {
                if (this.UserLoaded != null)
                    this.UserLoaded.fire(this,EventArgs.Empty());
                return;
            }
            profile_id.PROFILE_TYPE = "_PUBLIC";
        } else if (profileType == ProfileType.PRIVATE) {
            if ((this.userPrivateProfile != null) && (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal())) {
                if (this.UserLoaded != null)
                    this.UserLoaded.fire(this,EventArgs.Empty());
                return;
            }
            profile_id.PROFILE_TYPE = "_PRIVATE";
        } else return;

        switch (profileLevel) {
            case Basic:
                profile_id.PROFILE_TYPE = "BASIC".concat(profile_id.PROFILE_TYPE);
                break;
            case Extended:
                profile_id.PROFILE_TYPE = "EXTENDED".concat(profile_id.PROFILE_TYPE);
                break;
            case Complete:
                profile_id.PROFILE_TYPE = "COMPLETE".concat(profile_id.PROFILE_TYPE);
                break;
            default:
                return;
        }

        this.loadProfile(profile_id,accountID);
    }

    public void loadProfile(PROFILE_ID profile_id, String accountID) {

        ProfileLevel profileLevel = (profile_id.PROFILE_TYPE.startsWith("BASIC_"))?ProfileLevel.Basic:((profile_id.PROFILE_TYPE.startsWith("EXTENDED_"))?ProfileLevel.Extended:((profile_id.PROFILE_TYPE.startsWith("COMPLETE_"))?ProfileLevel.Complete:ProfileLevel.None));

        if ((profile_id.PROFILE_TYPE.endsWith("_PUBLIC")) && (this.userPublicProfile != null) && (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal()))  {
            if (this.UserLoaded != null)
                this.UserLoaded.fire(this,EventArgs.Empty());
            return;
        }
        else if ((profile_id.PROFILE_TYPE.endsWith("_PRIVATE")) && (this.userPrivateProfile != null) && (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal()))  {
            if (this.UserLoaded != null)
                this.UserLoaded.fire(this,EventArgs.Empty());
            return;
        }

        this.loading = true;
        Command requestUserProfile = new Command(AvailableCommands.UpdateProfile,profile_id);
        requestUserProfile.addCallbackDelegate(new CallbackDelegate(this,"loadCallback"));
        this.controller.getDataProvider().runCommand(accountID, requestUserProfile, ((profileLevel == ProfileLevel.Extended)?CommandQueue.Priority.High: CommandQueue.Priority.RealTime));
    }

    public void unloadProfile(ProfileLevel profileLevel) {
        if (profileLevel == ProfileLevel.Complete) return;

            if (this.userPublicProfile != null)
                this.userPublicProfile.unloadProfile(profileLevel);

        if (this.userPrivateProfile != null)
            this.userPrivateProfile.unloadProfile(profileLevel);
    }
    /*************************************/

    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/
    public Format toFormat(Format format) {
        if ((format instanceof LOCAL_USER_PROFILE) && (!this.isMe())) throw new IllegalArgumentException("Can`t convert general user to LOCAL_USER_PROFILE");
        else if ((format instanceof USER_PROFILE) && (this.isMe())) throw new IllegalArgumentException("Can`t convert local user to USER_PROFILE");

        if (format instanceof LOCAL_USER_PROFILE) {
            ((LOCAL_USER_PROFILE) format).EMAIL = this.email;
            if (this.userPublicProfile != null) {
                if (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Basic.ordinal())
                    ((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE = ((BASIC_PUBLIC_PROFILE) this.userPublicProfile.toFormat(new BASIC_PUBLIC_PROFILE()));
                if (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Extended.ordinal())
                    ((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE = ((PUBLIC_PROFILE) this.userPublicProfile.toFormat(new PUBLIC_PROFILE()));
            }
            if (this.userPrivateProfile != null) {
                if (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Basic.ordinal())
                    ((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE = ((BASIC_PRIVATE_PROFILE) this.userPrivateProfile.toFormat(new BASIC_PRIVATE_PROFILE()));
                if (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Extended.ordinal())
                    ((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE = ((PRIVATE_PROFILE) this.userPrivateProfile.toFormat(new PRIVATE_PROFILE()));
            }
        } else if (format instanceof USER_PROFILE) {
            if (this.userPublicProfile != null) {
                if (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Basic.ordinal())
                    ((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE = ((BASIC_PUBLIC_PROFILE) this.userPublicProfile.toFormat(new BASIC_PUBLIC_PROFILE()));
                if (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Extended.ordinal())
                    ((USER_PROFILE) format).USER_PUBLIC_PROFILE = ((PUBLIC_PROFILE) this.userPublicProfile.toFormat(new PUBLIC_PROFILE()));
            }
            if (this.userPrivateProfile != null) {
                if (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Basic.ordinal())
                    ((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE = ((BASIC_PRIVATE_PROFILE) this.userPrivateProfile.toFormat(new BASIC_PRIVATE_PROFILE()));
                if (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= ProfileLevel.Extended.ordinal())
                    ((USER_PROFILE) format).USER_PRIVATE_PROFILE = ((PRIVATE_PROFILE) this.userPrivateProfile.toFormat(new PRIVATE_PROFILE()));
            }
        }

        return format;
    }
    public Boolean fromFormat(Format format) {
        boolean result = false;

        if (format instanceof USER_PROFILE) {
            this. isMe = false;

            if ((((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE);
            else if ((((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE);

            if ((((USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((USER_PROFILE) format).USER_PUBLIC_PROFILE);
            else if ((((USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((USER_PROFILE) format).USER_PUBLIC_PROFILE);

            if ((((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPrivateProfile == null))
                this.userPrivateProfile = new PrivateProfile(((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);
            else if ((((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPrivateProfile != null))
                this.userPrivateProfile.fromFormat(((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);

            if ((((USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPrivateProfile == null))
                this.userPrivateProfile = new PrivateProfile(((USER_PROFILE) format).USER_PRIVATE_PROFILE);
            else if ((((USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPrivateProfile != null))
                this.userPrivateProfile.fromFormat(((USER_PROFILE) format).USER_PRIVATE_PROFILE);

            if (this.userPublicProfile != null)
                this.userID = this.userPublicProfile.userID;
            else if (this.userPrivateProfile != null)
                this.userID = this.userPrivateProfile.userID;

            result=true;
        } else if (format instanceof LOCAL_USER_PROFILE) {
            this.isMe = true;
            this.email = ((LOCAL_USER_PROFILE) format).EMAIL;

            if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_BASIC_PUBLIC_PROFILE);

            if ((((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE);

            if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPrivateProfile == null))
                this.userPrivateProfile = new PrivateProfile(((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPrivateProfile != null))
                this.userPrivateProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);

            if ((((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPrivateProfile == null))
                this.userPrivateProfile = new PrivateProfile(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPrivateProfile != null))
                this.userPrivateProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);

           /* if (((LOCAL_USER_PROFILE) format).HIVES_SUBSCRIBED != null)
                for (HIVE_ID hive : ((LOCAL_USER_PROFILE) format).HIVES_SUBSCRIBED) {
                    if (!this.hiveSubscriptionsList.containsKey(hive.NAME_URL));
                        this.hiveSubscriptionsList.add(new Hive(hive, this.userID));
                }*/ //TODO: Fill a subscription here

            if (this.userPublicProfile != null)
                this.userID = this.userPublicProfile.userID;
            else if (this.userPrivateProfile != null)
                this.userID = this.userPrivateProfile.userID;

            this.home = new Home(this);

            result=true;
        }

        this.loading = !result;

        if ((result) && (this.UserLoaded != null))
            this.UserLoaded.fire(this,EventArgs.Empty());

        return result;
    }

}
