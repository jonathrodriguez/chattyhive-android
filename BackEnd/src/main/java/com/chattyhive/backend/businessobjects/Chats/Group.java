package com.chattyhive.backend.businessobjects.Chats;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;

import java.util.Collection;
import java.util.Date;
import java.util.TreeMap;

/**
 * Created by Jonathan on 16/06/2014.
 */
public class Group {
    /**************************
       Static group management
     **************************/
    protected static MessageLocalStorageInterface localStorage;
    protected static Controller controller;

    private static TreeMap<String,Group> Groups;
    public static void Initialize(Controller controller, MessageLocalStorageInterface messageLocalStorageInterface) {
        if (Group.Groups == null) {
            Group.Groups = new TreeMap<String, Group>();
        }

        Group.controller = controller;
        Group.localStorage = messageLocalStorageInterface;

        //TODO: Implement local recovering of groups.
    }

    /**************************
         Chat management
     **************************/

    protected String groupName;
    public String getGroupName() { return this.groupName; }

    protected Chat chat;
    public Chat getChat() { return this.chat; }

    /*****************************************
                 Constructor
     *****************************************/
    protected Group(String groupName) {
        this.users = new TreeMap<String,User>();
        this.subgroups = new TreeMap<String, Group>();

        this.groupName = groupName;

        //TODO: Implement server and local information recovering
    }

    public static Group getGroup(String groupName) {
        if ((Group.Groups == null) || (Group.Groups.isEmpty())) throw new NullPointerException("There are no groups.");
        else if (groupName == null) throw new NullPointerException("groupName must not be null.");
        else if (groupName.isEmpty()) throw  new IllegalArgumentException("groupName must not be empty.");

        if (Group.Groups.containsKey(groupName))
            return Group.Groups.get(groupName);
        else {
            Group g = new Group(groupName);
            Group.Groups.put(groupName,g);
            return g;
        }
    }

    public static Group createGroup(Collection<User> users,String parentGroup) {
        //TODO: implement server communication
        String groupName = ""; //Recovered from server.
        //TODO: implement local storage

        Group g = new Group(groupName);
        Group.Groups.put(groupName,g);
        return g;
    }
    /*****************************************
                users list
     *****************************************/
    protected TreeMap<String,User> users;
    public User getUser(String public_name) {
        if ((this.users == null) || (this.users.isEmpty())) throw new NullPointerException("There are no users for this group.");
        else if (public_name == null) throw new NullPointerException("public_name must not be null.");
        else if (public_name.isEmpty()) throw  new IllegalArgumentException("public_name must not be empty.");

        return users.get("public_name");
    }
    public void addUser(User user) {
        if (user == null) throw new NullPointerException("user must not be null.");

        users.put(user.getPublicName(), user);
    }
    public void requestUsers() {
        //TODO: implement server request
        //TODO: implement local update
    }
    public void inviteUser(String public_name) {
        //TODO: implement server request

        //TODO: implement local update
        User u = User.getUser(public_name);
        this.addUser(u);
    }

    /*****************************************
                subgroups list
     *****************************************/
    protected TreeMap<String,Group> subgroups;
    public Group getSubgroup(String groupName) {
        if ((this.subgroups == null) || (this.subgroups.isEmpty())) throw new NullPointerException("There are no subgroups for this group.");
        else if (groupName == null) throw new NullPointerException("groupName must not be null.");
        else if (groupName.isEmpty()) throw  new IllegalArgumentException("groupName must not be empty.");

        return subgroups.get("groupName");
    }
    public void addSubgroup(Group subgroup) {
        if (subgroup == null) throw new NullPointerException("subgroup must not be null.");

        subgroups.put(subgroup.getGroupName(), subgroup);
    }

    /*****************************************
           context (group shared files, ...)
     *****************************************/
    protected String description;
    public String getDescription() { return this.description; }
    public void setDescription(String value) { this.description = value; }
    public void requestDescriptionChange (String value) {
        Boolean hasBeenAccepted = true;
        //TODO: implement server communication

        if (hasBeenAccepted) this.setDescription(value);
        //TODO: implement local update
    }

    protected String name;
    public String getName() { return this.name; }
    public void setName(String value) { this.name = value; }
    public void requestNameChange (String value) {
        Boolean hasBeenAccepted = true;
        //TODO: implement server communication

        if (hasBeenAccepted) this.setName(value);
        //TODO: implement local update
    }

    protected String nameURL;
    public String getNameURL() { return this.nameURL; }
    public void setNameURL(String value) { this.nameURL = value; }


    protected Date creationDate;
    public Date getCreationDate() { return this.creationDate; }
    public void setCreationDate(Date value) { this.creationDate = value; }

    protected String pusherChannel;
    public String getPusherChannel() { return this.pusherChannel; }
    public void setPusherChannel(String value) { this.pusherChannel = value; }
}
