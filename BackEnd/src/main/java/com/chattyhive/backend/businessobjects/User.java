package com.chattyhive.backend.businessobjects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Jonathan on 11/12/13.
 * Represents a user.
 */
public class User {
    private static TreeMap<String,User> knownUsers;
    private static User me;

    /*Fields not coming from profile*/
    private Boolean isMe = false;
    public String color;



    /*Profile fields*/
    private String public_name;
    private String first_name;
    private String last_name;
    private String sex;
    private String language;
    private String location;
    private Boolean private_show_age;
    private Boolean public_show_age;
    private Boolean show_location;

    /*Other fields*/
    private String email;

    /**
     * Public constructor.
     * @param email a string with the user's email.
     */
    public User (String email) {
       this.email = email;
       this.isMe = true;
       this.color = String.format("#%06X", ((int) Math.floor((Math.random() * Math.pow(2, 24)))));
       User.me = this;
    }

    private User () {
        this.isMe = false;
        this.color = String.format("#%06X", ((int) Math.floor((Math.random() * Math.pow(2, 24)))));
    }

    public String getFirstName() { return ((this.first_name != null)?this.first_name:"NULL"); }
    public String getLastName() { return ((this.last_name != null)?this.last_name:"NULL"); }
    public String getSex() { return ((this.sex != null)?this.sex:"NULL"); }
    public String getLanguage() { return ((this.language != null)?this.language:"NULL"); }
    public String getLocation() { return ((this.location != null)?this.location:"NULL"); }
    public Boolean getPrivateShowAge() { return ((this.private_show_age != null)?this.private_show_age:false); }
    public Boolean getPublicShowAge() { return ((this.public_show_age != null)?this.public_show_age:false); }
    public Boolean getShowLocation() { return ((this.show_location != null)?this.show_location:false); }

    /**
     * Returns the user's name
     * @return
     */
    public String getPublicName() {
        return this.public_name;
    }

    public String getEmail() { return this.email; }

    public static User getMe() { return User.me; }

    public Boolean isMe() { return this.isMe; }

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
    }

    public static User getUser(String public_name) {
        if (User.me.public_name.equalsIgnoreCase(public_name)) return User.me;
        if (!User.knownUsers.containsKey(public_name)) {
            User u = new User();
            u.public_name = public_name;
            User.knownUsers.put(public_name,u);
        }

        return User.knownUsers.get("public_name");
    }
}
