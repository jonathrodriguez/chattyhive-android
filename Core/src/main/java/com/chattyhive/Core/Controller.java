package com.chattyhive.Core;

import com.chattyhive.Core.BusinessObjects.Chats.Hive;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Home.Cards.HiveMessageCard;
import com.chattyhive.Core.BusinessObjects.Home.HomeCard;
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
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
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
            //TODO: initiate tree
        }
        return this.userRoots.get(serverUser);
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
                    homeCards = new TreeMap<Date, HomeCard>(new Comparator<Date>() {
                        @Override
                        public int compare(Date o1, Date o2) {
                            if ((o1 == null) && (o2 != null))
                                return 1;
                            else if ((o1 != null) && (o2 == null))
                                return -1;
                            else if (o1 != null) //&& (o2 != null)) <- Which is always true
                                return o2.compareTo(o1);
                            else
                                return 0;
                        }
                    });

                int hiveCount = Hive.getHiveCount();
                Hive hive;
                Message message;
                HiveMessageCard homeCard;
                for (int i = 0; i < hiveCount; i++) {
                    hive = Hive.getHiveByIndex(i);
                    if ((hive != null) && (hive.getPublicChat() != null) && (hive.getPublicChat().getConversation() != null) && (hive.getPublicChat().getConversation().getCount() > 0)) {
                        message = hive.getPublicChat().getConversation().getLastMessage();
                        homeCard = new HiveMessageCard(message);
                        homeCards.put(message.getOrdinationTimeStamp(), homeCard);
                    }
                }

                HomeReceived.fire(dataProvider,EventArgs.Empty());
            }
        }.start();
    }

    public void onHiveListChanged (Object sender, EventArgs eventArgs) {
        if ((sender == null) || ((sender instanceof Hive) && (((Hive) sender).getPublicChat() != null) && (((Hive) sender).getPublicChat().getConversation() != null) && (((Hive) sender).getPublicChat().getConversation().getCount() > 0))) {
            this.RequestHome();
        }
    }
}
