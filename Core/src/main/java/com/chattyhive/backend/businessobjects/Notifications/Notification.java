package com.chattyhive.backend.businessobjects.Notifications;

import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jonathan on 10/12/2014.
 */
public class Notification {

    INotificationShower notificationShower;

    HashMap<Chat,ArrayList<Message>> messages;
    int totalMessages;

    public HashMap<Chat,ArrayList<Message>> getMessages() {
        return this.messages;
    }
    public int getMessageCount () {
        return this.totalMessages;
    }

    public Notification(INotificationShower notificationShower) {
        this.notificationShower = notificationShower;

        this.messages = new HashMap<Chat, ArrayList<Message>>();
        this.totalMessages = 0;
    }

    public void add(Message message) {
        Chat c = message.getConversation().getParent();
        if (!messages.containsKey(c))
            messages.put(c,new ArrayList<Message>());
        messages.get(c).add(message);
        this.totalMessages++;
    }

    public void push() {
        //Show or update notification
        notificationShower.ShowNotification(this);
    }
}
