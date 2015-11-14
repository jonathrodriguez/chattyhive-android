package com.chattyhive.Core;


import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.ChatList;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Hives.HiveList;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.BusinessObjects.Users.UserList;
import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalStorageInterface;
import com.chattyhive.Core.ContentProvider.Server.IServerUser;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.Core.ContentProvider.Formats.USERNAME;
import com.chattyhive.Core.ContentProvider.Formats.USER_EMAIL;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Util.CallbackDelegate;

import java.util.HashMap;


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

    private static Controller runningController;

    public static Controller GetRunningController() {
        return Controller.runningController;
    }

    public static Controller InitializeController(LocalStorageInterface settingsStorage) {
        Controller.runningController = new Controller(settingsStorage);
        return Controller.runningController;
    }

    private DataProvider dataProvider;

    private HashMap<String,IServerUser> serverUsers;
    private HashMap<IServerUser,User> userRoots;


    private Controller(LocalStorageInterface settingsStorage) {
        this.dataProvider = new DataProvider(this,settingsStorage);
        this.serverUsers = new HashMap<String,IServerUser>();
        this.userRoots = new HashMap<IServerUser, User>();

        this.loadedUsers = new UserList();
        this.loadedChats = new ChatList();
        this.loadedHives = new HiveList();
    }

    public User getUserRoot(IServerUser serverUser) {
        if (!this.userRoots.containsKey(serverUser)) {
            this.userRoots.put(serverUser,new User(this,serverUser.getUserData(IServerUser.userIDKey),true));
        }
        return this.userRoots.get(serverUser);
    }
    public User getUserRoot(String publicName) {
        if (!this.serverUsers.containsKey(publicName)) {
            return null;
        }
        return this.getUserRoot(this.serverUsers.get(publicName));
    }
    public void activateAccount(IServerUser serverUser) {
        String publicName = serverUser.getUserData(IServerUser.userIDKey);
        this.serverUsers.put(publicName,serverUser);
        this.userRoots.put(serverUser,new User(this,publicName,true));
    }
    public String getAccountID(IServerUser serverUser) {
        return serverUser.getUserData(IServerUser.userIDKey);
    }
    public IServerUser getServerUser(String accountID) {
        if (!this.serverUsers.containsKey(accountID)) {
            return null;
        }
        return this.serverUsers.get(accountID);
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    /*******************************************************************************************/
    /*******************************************************************************************/
    /*                            LISTS                                                        */
    /*******************************************************************************************/
    /*******************************************************************************************/
    private UserList loadedUsers;
    private ChatList loadedChats;
    private HiveList loadedHives;

    public User getUser(String publicName){
        return this.loadedUsers.get(publicName);
    }
    public Chat getChat(String chatID){
        return this.loadedChats.get(chatID);
    }
    public Hive getHive(String hiveID){
        return this.loadedHives.get(hiveID);
    }

    public void userLoaded(User user){
        this.loadedUsers.add(user);
    }
    public void chatLoaded(Chat chat){
        this.loadedChats.add(chat);
    }
    public void hiveLoaded(Hive hive){
        this.loadedHives.add(hive);
    }

    /*******************************************************************************************/
    /*******************************************************************************************/
    /*                            CHECK                                                        */
    /*******************************************************************************************/
    /*******************************************************************************************/
    public void CheckEmail(String email, CallbackDelegate Callback) {
        if (!email.contains("@")) return;

        String userPart = email.split("@")[0];
        String serverPart = email.split("@")[1];
        if ((userPart.isEmpty()) || (serverPart.isEmpty())) return;

        USER_EMAIL user_email = new USER_EMAIL();
        user_email.EMAIL_USER_PART = userPart;
        user_email.EMAIL_SERVER_PART = serverPart;
        Command emailCheck = new Command(AvailableCommands.EmailCheck,user_email);
        emailCheck.addCallbackDelegate(Callback);
        this.dataProvider.runCommand(emailCheck, CommandQueue.Priority.RealTime);
    }
    public void CheckUsername(String username, CallbackDelegate Callback) {
        USERNAME user_username = new USERNAME();
        user_username.PUBLIC_NAME = username;
        Command usernameCheck = new Command(AvailableCommands.UsernameCheck,user_username);
        usernameCheck.addCallbackDelegate(Callback);
        this.dataProvider.runCommand(usernameCheck, CommandQueue.Priority.RealTime);
    }
}
