package com.chattyhive.backend.businessobjects.Notifications;

import com.chattyhive.backend.businessobjects.Chats.Messages.Message;

/**
 * Created by Jonathan on 10/12/2014.
 */
public class Notification {

    INotificationShower notificationShower;


    public Notification(INotificationShower notificationShower) {
        this.notificationShower = notificationShower;
    }

    public void add(Message message) {

    }

    public void push() {
        //Show or update notification
        notificationShower.ShowNotification(this);
    }
}
