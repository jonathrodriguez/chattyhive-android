package com.chattyhive.Core.BusinessObjects.Chats;

import com.chattyhive.Core.BusinessObjects.Users.User;

/**
 * Created by jonathrodriguez on 11/08/2015.
 */
public class ChatSubscription {
    private Chat chat;
    private User user;

    public ChatSubscription(Chat chat, User user) {
        this.chat = chat;
        this.user = user;
    }

    public Chat getChat() {
        return this.chat;
    }
    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
