package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.BASIC_PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.formats.USERNAME;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.chattyhive.backend.contentprovider.formats.USER_PROFILE;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    public static void CheckEmail(String email, Controller controller, EventHandler<CommandCallbackEventArgs> Callback) {
        if (!email.contains("@")) return;

        String userPart = email.split("@")[0];
        String serverPart = email.split("@")[1];
        if ((userPart.isEmpty()) || (serverPart.isEmpty())) return;

        USER_EMAIL user_email = new USER_EMAIL();
        user_email.EMAIL_USER_PART = userPart;
        user_email.EMAIL_SERVER_PART = serverPart;
        controller.getDataProvider().InvokeServerCommand(AvailableCommands.EmailCheck, Callback, user_email);
    }
    public static void CheckUsername(String username, Controller controller, EventHandler<CommandCallbackEventArgs> Callback) {
        USERNAME user_username = new USERNAME();
        user_username.PUBLIC_NAME = username;
        //controller.getDataProvider().InvokeServerCommand(AvailableCommands.UsernameCheck,Callback,user_username); //TODO: implement server function
        COMMON common = new COMMON();
        common.STATUS = "OK";
        ArrayList<Format> rf = new ArrayList<Format>();
        rf.add(common);
        ArrayList<Format> sf = new ArrayList<Format>();
        sf.add(user_username);
        CommandCallbackEventArgs eventArgs = new CommandCallbackEventArgs(AvailableCommands.EmailCheck,rf,sf,null);
        try {
            Callback.Invoke(controller,eventArgs);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Controller controller;
    private Boolean loading;

    private Boolean isMe = false;
    private String email; //Only for local user;

    private String userID; //ID for any user. (This will be the public username).
    private PublicProfile userPublicProfile; //Public profile for any user.
    private PrivateProfile userPrivateProfile; //Private profile for any user.

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Boolean isLoading() {
        return this.loading;
    }

    public Boolean isMe() {
        return this.isMe;
    }

    public String getEmail() {
        return this.email;
    }
    public void setEmail(String value) {
        this.email = value;
    }

    public String getUserID() {
        return this.userID;
    }
    public void setUserID(String value) {
        this.userID = value;
    }

    public PublicProfile getUserPublicProfile() {
        return this.userPublicProfile;
    }
    public PrivateProfile getUserPrivateProfile() {
        return this.userPrivateProfile;
    }

    /********************************************************************************************/

    public User(String email) {
        this(email,(Controller)null);
    }

    public User(String email, Controller controller) {
        this.email = email;
        this.isMe = true;
        this.userPrivateProfile = new PrivateProfile();
        this.userPublicProfile = new PublicProfile();
        this.controller = controller;
    }

    public User (Format format) {
        this(format,(Controller)null);
    }

    public User(Format format, Controller controller) {
        if (!this.fromFormat(format))
            throw new IllegalArgumentException("LOCAL_USER_PROFILE, PUBLIC_PROFILE or PRIVATE_PROFILE expected.");

        this.controller = controller;
    }

    public User(String userID, ProfileType requiredProfileType) {
        this(userID,requiredProfileType,(Controller)null);
    }

    public User(String userID, ProfileType requiredProfileType, Controller controller) {
        this.controller = controller;
        this.userID = userID;

        if (this.controller == null) return;

        PROFILE_ID requestProfile = new PROFILE_ID();
        requestProfile.USER_ID = userID;
        requestProfile.PROFILE_TYPE = (requiredProfileType == ProfileType.PRIVATE)?"BASIC_PRIVATE":"BASIC_PUBLIC";

        this.controller.getDataProvider().RunCommand(AvailableCommands.UserProfile, new EventHandler<CommandCallbackEventArgs>(this, "loadCallback", CommandCallbackEventArgs.class), requestProfile);
        this.loading = true;
    }


    public void loadCallback (Object sender, CommandCallbackEventArgs args) {
        if (args.countReceivedFormats() == 0) return;
        ArrayList<Format> receivedFormats = args.getReceivedFormats();
        for (Format format : receivedFormats) {
            if ((format instanceof LOCAL_USER_PROFILE) || (format instanceof USER_PROFILE))
                this.fromFormat(format);
        }
    }

    /***********************************/
    /*     STATIC USER MANAGEMENT      */
    /***********************************/
    private static UserLocalStorageInterface userLocalStorage;

    private static TreeMap<String,User> knownUsers;
    private static User me;

    public static void Initialize(UserLocalStorageInterface userLocalStorage) {
        User.userLocalStorage = userLocalStorage;
        User.knownUsers = new TreeMap<String, User>();

        DataProvider.GetDataProvider().onUserProfileReceived.add(new EventHandler<FormatReceivedEventArgs>(User.class,"onFormatReceived",FormatReceivedEventArgs.class));

        //Load local stored users.
        String[] users = userLocalStorage.RecoverAllCompleteUserProfiles();
        if (users != null) {
            for (String user : users) {
                Format[] formats = Format.getFormat((new JsonParser()).parse(user));
                for (Format format : formats) {
                    User u = new User(format);

                    u.unloadProfile(ProfileLevel.Basic); //There is no need to keep in memory complete user profiles.

                    User.knownUsers.put(u.getUserID(), u);
                }
            }
        }

        String localUser = userLocalStorage.RecoverLocalUserProfile();
        if (localUser != null) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(localUser));
            for (Format format : formats) {
                me = new User(format);
            }
        }
    }

    private static User getUser(String userID,Format format) {
        if (User.knownUsers == null) throw new IllegalStateException("Users must be initialized.");
        else if (userID == null) throw new NullPointerException("UserID must not be null.");
        else if (userID.isEmpty()) throw new IllegalArgumentException("UserID must not be empty.");

        if (User.knownUsers.containsKey(userID))
            return User.knownUsers.get(userID);
        else if ((me != null) && ((me.userPrivateProfile.getID().equalsIgnoreCase(userID)) || (me.userPublicProfile.getID().equalsIgnoreCase(userID)))) {
            return me;
        }
        else {
            User u = new User(format);
            if (!u.isMe()) {
                    u.unloadProfile(ProfileLevel.Basic);
                    User.knownUsers.put(userID, u);
                }
            else
                me = u;
            return u;
        }
    }

    public static User getUser(PROFILE_ID profile_id) {
        if (User.knownUsers == null) throw new IllegalStateException("Users must be initialized.");
        else if (profile_id == null) throw new NullPointerException("PROFILE_ID must not be null.");
        else if ((profile_id.USER_ID == null) || profile_id.USER_ID.isEmpty()) throw new IllegalArgumentException("PROFILE_ID must not be empty.");

        String userID = profile_id.USER_ID;

        if (User.knownUsers.containsKey(userID))
            return User.knownUsers.get(userID);
        else if ((me != null) && ((me.userPrivateProfile.getID().equalsIgnoreCase(userID)) || (me.userPublicProfile.getID().equalsIgnoreCase(userID)))) {
            return me;
        }
        else {
            User u = new User(profile_id);
            if (!u.isMe()) {
                u.unloadProfile(ProfileLevel.Basic);
                User.knownUsers.put(userID, u);
            }
            else
                me = u;

            return u;
        }
    }

    public static void unloadProfiles() {
        for (User user : User.knownUsers.values())
            user.unloadProfile(ProfileLevel.Basic);
    }

    public static User getMe() {
        return User.me;
    }
    public static void removeMe() {
        me = null;
    }

    /***********************************/
    /*        STATIC CALLBACKS         */
    /***********************************/

    public static void onFormatReceived(Object sender, FormatReceivedEventArgs args) {
        if (args.countReceivedFormats() > 0) {
            ArrayList<Format> formats = args.getReceivedFormats();
            for (Format format : formats) {
                if (format instanceof LOCAL_USER_PROFILE) {
                    User.userLocalStorage.StoreLocalUserProfile(format.toJSON().toString());

                    if (User.me != null)
                        User.getMe().fromFormat(format);
                    else
                        User.me = new User(format);
                } else if (format instanceof PUBLIC_PROFILE) {
                    User.userLocalStorage.StoreCompleteUserProfile(((PUBLIC_PROFILE) format).USER_ID,format.toJSON().toString());
                    User.getUser(((PUBLIC_PROFILE) format).USER_ID, format);
                } else if  (format instanceof PRIVATE_PROFILE) {
                    User.userLocalStorage.StoreCompleteUserProfile(((PRIVATE_PROFILE) format).USER_ID,format.toJSON().toString());
                    User.getUser(((PRIVATE_PROFILE) format).USER_ID, format);
                }
            }
        }
    }


    /***********************************/
    /***********************************/
    /***********************************/
    /***********************************/

    /*************************************/

    /*************************************/
    /*     COMMUNICATION METHODS         */
    /*************************************/
    public void Register(String password,EventHandler<CommandCallbackEventArgs> Callback) {
        LOGIN login = new LOGIN();
        login.USER = this.email;
        login.PASS = password;
        LOCAL_USER_PROFILE lup = (LOCAL_USER_PROFILE)this.toFormat(new LOCAL_USER_PROFILE());
        lup.PASS = password;
        this.controller.getDataProvider().InvokeServerCommand(AvailableCommands.Register,Callback,lup,login);
    }
    public void EditProfile(EventHandler<CommandCallbackEventArgs> Callback) {
        //this.controller.getDataProvider().InvokeServerCommand(ServerCommand.AvailableCommands.UpdateProfile,Callback,this.toFormat(new LOCAL_USER_PROFILE()));
    }

    public void loadProfile(ProfileType profileType, ProfileLevel profileLevel) {
        if (profileLevel == ProfileLevel.None) return;

        PROFILE_ID profile_id = new PROFILE_ID();
        profile_id.USER_ID = this.userID;

        if (profileType == ProfileType.PUBLIC) {
            if ((this.userPublicProfile != null) && (this.userPublicProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal()))
                return;
            profile_id.PROFILE_TYPE = "_PUBLIC";
        } else if (profileType == ProfileType.PRIVATE) {
            if ((this.userPrivateProfile != null) && (this.userPrivateProfile.getLoadedProfileLevel().ordinal() >= profileLevel.ordinal()))
                return;
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

        this.controller.getDataProvider().RunCommand(AvailableCommands.UserProfile,new EventHandler<CommandCallbackEventArgs>(this,"loadCallback",CommandCallbackEventArgs.class),profile_id);
    }

    public void unloadProfile(ProfileLevel profileLevel) {
        if (profileLevel == ProfileLevel.Complete) return;

        if (profileLevel == ProfileLevel.None)
            //this.controller.removeUser(this);

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

            if ((((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);
            else if ((((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);

            if ((((USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((USER_PROFILE) format).USER_PRIVATE_PROFILE);
            else if ((((USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((USER_PROFILE) format).USER_PRIVATE_PROFILE);

            if (this.userPublicProfile != null)
                this.userID = this.userPublicProfile.userID;
            else if (this.userPrivateProfile != null)
                this.userID = this.userPrivateProfile.userID;

            this.loading = false;

            return true;
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

            if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_BASIC_PRIVATE_PROFILE);

            if ((((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPublicProfile == null))
                this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);
            else if ((((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE != null) && (this.userPublicProfile != null))
                this.userPublicProfile.fromFormat(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);

            for (HIVE_ID hive : ((LOCAL_USER_PROFILE) format).HIVES_SUBSCRIBED) {
                Hive.getHive(hive.NAME_URL);
            }

            if (this.userPublicProfile != null)
                this.userID = this.userPublicProfile.userID;
            else if (this.userPrivateProfile != null)
                this.userID = this.userPrivateProfile.userID;

            this.loading = false;

            return true;
        }

        return false;
    }

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }
    public void fromJson(JsonElement jsonElement) {
        Format[] formats = Format.getFormat(jsonElement);
        for (Format format : formats)
            if (this.fromFormat(format)) return;

        throw  new IllegalArgumentException("Expected LOCAL_USER_PROFILE, or USER_PROFILE formats.");
    }
}
