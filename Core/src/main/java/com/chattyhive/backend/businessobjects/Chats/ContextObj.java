package com.chattyhive.backend.businessobjects.Chats;

/**
 * Created by J.Guzmán on 10/02/2015.
 */
public class ContextObj {
    private Hive hive;
    private Chat chat;
    private ContextType contextType;


    public Hive getHive() {
        return hive;
    }

    public Chat getChat() {
        return chat;
    }

    public ContextType getContextType() {
        return contextType;
    }

    public void setHive(Hive hive) {
        this.hive = hive;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setContextType(ContextType contextType) {
        this.contextType = contextType;
    }
}
