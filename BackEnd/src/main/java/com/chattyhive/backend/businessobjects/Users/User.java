package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.OSStorageProvider.UserLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.TreeMap;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {

    /***********************************/
    /*     STATIC USER MANAGEMENT      */
    /***********************************/
    private static UserLocalStorageInterface userLocalStorage;

    private static TreeMap<String,User> knownUsers;
    private static User me;

    public void Initialize(UserLocalStorageInterface userLocalStorage) {
        User.userLocalStorage = userLocalStorage;
        User.knownUsers = new TreeMap<String, User>();

        //Load local stored users.
        String[] users = userLocalStorage.RecoverAllCompleteUserProfiles();
        for(String user : users) {
            Format[] formats = Format.getFormat((new JsonParser()).parse(user));
            for (Format format : formats) {
                User u = new User(format);
                User.knownUsers.put(u.getUserID(),u);
            }
        }

        String localUser = userLocalStorage.RecoverLocalUserProfile();
        me = new User(localUser);
    }

    public static User getUser(String userID) {
        if (User.knownUsers == null) throw new IllegalStateException("Users must be initialized.");
        else if (userID == null) throw new NullPointerException("UserID must not be null.");
        else if (userID.isEmpty()) throw new IllegalArgumentException("UserID must not be empty.");

        if (User.knownUsers.containsKey(userID))
            return User.knownUsers.get(userID);
        else if ((me != null) && ((me.userPrivateProfile.getID().equalsIgnoreCase(userID)) || (me.userPublicProfile.getID().equalsIgnoreCase(userID)))) {
            return me;
        }
        else {
            User u = new User(userID);
            if (!u.isMe())
                User.knownUsers.put(userID,u);
            else
                me = u;
            return u;
        }
    }

    public static User getMe() {
        return User.me;
    }
    public static void removeMe() {
        me = null;
    }
    /***********************************/

    /***********************************/
    /*      GENERAL USER FIELDS        */
    /***********************************/
    private Boolean isMe = false;
    private String email; //Only for local user;

    private String userID;
    private String color;
    private String showingName;
    private String imageURL;

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

    public String getColor() {
        return this.color;
    }

    public String getShowingName() {
        return this.showingName;
    }

    public String getImageURL() {
        return this.imageURL;
    }
    /***********************************/

    /***********************************/
    /*         USER PROFILES           */
    /***********************************/
    private Boolean isPrivate;
    private PublicProfile userPublicProfile; //Public profile for any user.
    private PrivateProfile userPrivateProfile; //Private profile for any user;

    public Boolean isPrivate() {
        return this.isPrivate;
    }
    public PublicProfile getUserPublicProfile() {
        return this.userPublicProfile;
    }
    public PrivateProfile getUserPrivateProfile() {
        return this.userPrivateProfile;
    }
    public Profile getUserProfile() {
        if (this.isPrivate)
            return this.userPrivateProfile;
        else
            return this.userPublicProfile;
    }
    /***********************************/


    /*************************************/
    /*         CONSTRCUCTORS             */
    /*************************************/
    /**
     * Public constructor.
     * @param userID a string with the user's ID.
     */
    public User (String userID) {

        Format[] formats = Format.getFormat((new JsonParser()).parse(User.userLocalStorage.RecoverCompleteUserProfile(userID)));
        for(Format format : formats)
            if (format instanceof PUBLIC_PROFILE) {
                PublicProfile profile = new PublicProfile(format);
                this.isMe = false;
                this.userID = profile.getID();
                this.color = profile.getColor();
                this.showingName = profile.getShowingName();
                this.imageURL = profile.getImageURL();
                this.userPublicProfile = profile;
            } else if (format instanceof PRIVATE_PROFILE) {
                PrivateProfile profile = new PrivateProfile(format);
                this.isMe = false;
                this.userID = profile.getID();
                this.color = profile.getColor();
                this.showingName = profile.getShowingName();
                this.imageURL = profile.getImageURL();
                this.userPrivateProfile = profile;
            }

        if ((this.userID == null) || (!this.userID.equals(userID))) {
            formats = Format.getFormat((new JsonParser()).parse(User.userLocalStorage.RecoverLocalUserProfile()));
            for(Format format : formats)
                if (format instanceof LOCAL_USER_PROFILE) {
                    this.isMe = true;
                    this.email = ((LOCAL_USER_PROFILE) format).EMAIL;
                    this.userPrivateProfile = new PrivateProfile(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);
                    this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE);

                    this.userID = this.userPrivateProfile.getID();
                    this.color = this.userPrivateProfile.getColor();
                    this.showingName = this.userPrivateProfile.getShowingName();
                    this.imageURL = this.userPrivateProfile.getImageURL();
                }
        }

        if ((this.userID == null) || (!this.userID.equals(userID))) {
            //TODO: Implement server information recovering
        }
    }

    private User () {
        this.isMe = false;
    }

    public User (Format format) {
        if (format instanceof PUBLIC_PROFILE) {
            PublicProfile profile = new PublicProfile(format);
            this.isMe = false;
            this.userID = profile.getID();
            this.color = profile.getColor();
            this.showingName = profile.getShowingName();
            this.imageURL = profile.getImageURL();
            this.userPublicProfile = profile;
            this.isPrivate = false;
        } else if (format instanceof PRIVATE_PROFILE) {
            PrivateProfile profile = new PrivateProfile(format);
            this.isMe = false;
            this.userID = profile.getID();
            this.color = profile.getColor();
            this.showingName = profile.getShowingName();
            this.imageURL = profile.getImageURL();
            this.userPrivateProfile = profile;
            this.isPrivate = true;
        } else if (format instanceof LOCAL_USER_PROFILE) {
            this.isMe = true;
            this.email = ((LOCAL_USER_PROFILE) format).EMAIL;
            this.userPrivateProfile = new PrivateProfile(((LOCAL_USER_PROFILE) format).USER_PRIVATE_PROFILE);
            this.userPublicProfile = new PublicProfile(((LOCAL_USER_PROFILE) format).USER_PUBLIC_PROFILE);
            this.isPrivate = true;

            this.userID = this.userPrivateProfile.getID();
            this.color = this.userPrivateProfile.getColor();
            this.showingName = this.userPrivateProfile.getShowingName();
            this.imageURL = this.userPrivateProfile.getImageURL();
        }
    }


    /**
     * Retrieves the JSON representation of this user object.
     * @return
     */
    public JsonElement toJson() {
        return new JsonPrimitive(this.public_name);
    }

    public static void setUpProfile(String public_name, JsonElement profile) {
        if (!User.knownUsers.containsKey(public_name)) {
            User.knownUsers.put(public_name, new User());
            User.knownUsers.get(public_name).public_name = public_name;
        }
        User u = User.knownUsers.get(public_name);
        u.setUpProfile(profile);

        if (u.public_name.isEmpty())
            User.knownUsers.remove(public_name);
        else if (u.getEmail().isEmpty())
            u.email = u.public_name;
    }

    public static void setUpOwnProfile(JsonElement profile) {
        if (User.me == null)
            User.me = new User();

        User.me.setUpProfile(profile);
        User.me.isMe = true;
    }

    public void setUpProfile(JsonElement profile) {
        try {
            if ((profile != null) && (profile.isJsonPrimitive())) {
                this.public_name = profile.getAsString();
                System.out.println("Profile element: ".concat(profile.toString()));
            } else if ((profile != null) && (profile.isJsonObject())) {
                System.out.println("Profile object: ".concat(profile.toString()));
                JsonObject jsonProfile = profile.getAsJsonObject();
                this.public_name = jsonProfile.get("public_name").getAsString();
                this.first_name = jsonProfile.get("first_name").getAsString();
                this.last_name = jsonProfile.get("last_name").getAsString();
                this.sex = jsonProfile.get("sex").getAsString();
                this.language = jsonProfile.get("language").getAsString();
                this.location = jsonProfile.get("location").getAsString();
                this.private_show_age = jsonProfile.get("private_show_age").getAsBoolean();
                this.public_show_age = jsonProfile.get("public_show_age").getAsBoolean();
                this.show_location = jsonProfile.get("show_location").getAsBoolean();
            } else {
                System.out.println("Profile unknown: ".concat(profile.toString()));
                this.public_name = "";
            }
        } catch (Exception e) { return; }
    }




}
