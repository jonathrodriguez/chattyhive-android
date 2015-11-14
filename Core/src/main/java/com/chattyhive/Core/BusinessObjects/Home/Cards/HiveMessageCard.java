package com.chattyhive.Core.BusinessObjects.Home.Cards;

import com.chattyhive.Core.BusinessObjects.Chats.ChatType;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Home.HomeCard;
import com.chattyhive.Core.BusinessObjects.Home.HomeCardType;

/**
 * Created by Jonathan on 07/10/2014.
 */
public class HiveMessageCard extends HomeCard {

    public HiveMessageCard () {
        this.cardType = HomeCardType.HiveMessage;
    }

    public HiveMessageCard(Message message) {
        this();
        this.setMessage(message);
    }

    private Message message;
    private Hive hive;
    private boolean priorized;

    public void setMessage(Message message) {
        if (message.getConversation().getParent().getChatType() != ChatType.PUBLIC) throw new IllegalArgumentException("Message must be from public chat of a hive.");
        this.message = message;
        this.hive = message.getConversation().getParent().getParentHive();
        this.priorized = false;
    }

    public Message getMessage() {
        return this.message;
    }
    public Hive getHive() {
        return this.hive;
    }

    public boolean getPriorized() {
        return this.priorized;
    }
    public void setPriorized(boolean value) {
        this.priorized = value;
    }
}
