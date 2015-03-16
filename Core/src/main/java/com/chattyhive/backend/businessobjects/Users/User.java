package com.chattyhive.backend.BusinessObjects.Users;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.BusinessObjects.Chats.Hive;
import com.chattyhive.backend.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.backend.ContentProvider.formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.backend.ContentProvider.formats.BASIC_PUBLIC_PROFILE;
import com.chattyhive.backend.ContentProvider.formats.Format;
import com.chattyhive.backend.ContentProvider.formats.HIVE_ID;
import com.chattyhive.backend.ContentProvider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.ContentProvider.formats.LOGIN;
import com.chattyhive.backend.ContentProvider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.ContentProvider.formats.PROFILE_ID;
import com.chattyhive.backend.ContentProvider.formats.PUBLIC_PROFILE;
import com.chattyhive.backend.ContentProvider.formats.USER_PROFILE;
import com.chattyhive.backend.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.backend.Util.Events.Event;
import com.chattyhive.backend.Util.Events.EventArgs;
import com.chattyhive.backend.Util.Events.EventHandler;
import com.google.gson.JsonElement;

import java.util.ArrayList;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    public Event<EventArgs> UserLoaded;

    private Controller controller;
    private Boolean loading;

    private Boolean isMe = false;
    private String email; //Only for local user;

    private String userID; //ID for any user. (This will be the public username).
    private PublicProfile userPublicProfile; //Public profile for any user.
    private PrivateProfile userPrivateProfile; //Private profile for any user.

    public Boolean hasController() {
        return (this.controller != null);
    }
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
        this.UserLoaded = new Event<EventArgs>();
    }

    public User (Format format) {
        this(format,(Controller)null);
    }

    public User(Format format, Controller controller) {
        if (!this.fromFormat(format))
            throw new IllegalArgumentException("LOCAL_USER_PROFILE or USER_PROFILE expected.");

        this.controller = controller;
        this.UserLoaded = new Event<EventArgs>();
    }

    public User(String userID, PROFILE_ID requestProfile) {
        this(userID,requestProfile,(Controller)null);
    }

    public User(String userID, PROFILE_ID requestProfile, Controller controller) {
        this.controller = controller;
        this.userID = userID;

        if (this.controller == null) return;

        this.controller.getDataProvider().RunCommand(AvailableCommands.UserProfile, new EventHandler<CommandCallbackEventArgs>(this, "loadCallback", CommandCallbackEventArgs.class), requestProfile);
        this.loading = true;

        this.UserLoaded = new Event<EventArgs>();
    }

    public void loadCallback (Object sender, CommandCallbackEventArgs args) {
        if (args.countReceivedFormats() == 0) return;
        ArrayList<Format> receivedFormats = args.getReceivedFormats();
        for (Format format : receivedFormats) {
            if ((format instanceof LOCAL_USER_PROFILE) || (format instanceof USER_PROFILE))
                this.fromFormat(format);
        }
    }

    /*************************************/
    /*     COMMUNICATION METHODS         */
    /*************************************/
    public void Register(String password,EventHandler<CommandCallbackEventArgs> Callback) {
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
        this.controller.getDataProvider().RunCommand(AvailableCommands.Register,Callback,lup,login);
    }
    public void EditProfile(EventHandler<CommandCallbackEventArgs> Callback,User newUser) {
        // TODO: compare newUser with this and send only fields which differ.
        this.getUserPrivateProfile().setStatusMessage(newUser.getUserPrivateProfile().getStatusMessage());
        this.getUserPublicProfile().setStatusMessage(newUser.getUserPublicProfile().getStatusMessage());
        this.controller.getDataProvider().RunCommand(AvailableCommands.UpdateProfile,Callback,newUser.toFormat(new LOCAL_USER_PROFILE()));
    }

    public void loadProfile(ProfileType profileType, ProfileLevel profileLevel) {
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

        this.loadProfile(profile_id);
    }

    public void loadProfile(PROFILE_ID profile_id) {

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

            this.loading = false;

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

            if (((LOCAL_USER_PROFILE) format).HIVES_SUBSCRIBED != null)
                for (HIVE_ID hive : ((LOCAL_USER_PROFILE) format).HIVES_SUBSCRIBED) {
                    Hive.getHive(hive.NAME_URL);
                }

            if (this.userPublicProfile != null)
                this.userID = this.userPublicProfile.userID;
            else if (this.userPrivateProfile != null)
                this.userID = this.userPrivateProfile.userID;

            this.loading = false;

            result=true;
        }

        if ((result) && (this.UserLoaded != null))
            this.UserLoaded.fire(this,EventArgs.Empty());

        return result;
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
