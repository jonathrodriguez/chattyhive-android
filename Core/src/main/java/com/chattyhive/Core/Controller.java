package com.chattyhive.Core;


import com.chattyhive.Core.BusinessObjects.Users.User;
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
    private HashMap<IServerUser,User> userRoots;


    private Controller(LocalStorageInterface settingsStorage) {
        this.dataProvider = new DataProvider(settingsStorage);
        this.userRoots = new HashMap<IServerUser, User>();
    }

    public User getUserRoot(IServerUser serverUser) {
        if (!this.userRoots.containsKey(serverUser)) {
            this.userRoots.put(serverUser,new User(this,serverUser));
        }
        return this.userRoots.get(serverUser);
    }

    public DataProvider getDataProvider() {
        return this.dataProvider;
    }


    /*******************************************************************************************/
    /*******************************************************************************************/
    /*                            USERS                                                        */
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
        Command emailCheck = new Command(null,AvailableCommands.EmailCheck,user_email);
        emailCheck.addCallbackDelegate(Callback);
        this.dataProvider.runCommand(emailCheck, CommandQueue.Priority.RealTime);
    }

    public void CheckUsername(String username, CallbackDelegate Callback) {
        USERNAME user_username = new USERNAME();
        user_username.PUBLIC_NAME = username;
        Command usernameCheck = new Command(null,AvailableCommands.UsernameCheck,user_username);
        usernameCheck.addCallbackDelegate(Callback);
        this.dataProvider.runCommand(usernameCheck, CommandQueue.Priority.RealTime);
    }
}
