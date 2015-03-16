package com.chattyhive.Core.BusinessObjects.Home.Cards;

import com.chattyhive.Core.BusinessObjects.Chats.ChatKind;
import com.chattyhive.Core.BusinessObjects.Chats.Hive;
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

    public void setMessage(Message message) {
        if (message.getConversation().getParent().getChatKind() != ChatKind.HIVE) throw new IllegalArgumentException("Message must be from public chat of a hive.");
        this.message = message;
        this.hive = message.getConversation().getParent().getParentHive();
    }
    public Message getMessage() {
        return this.message;
    }
    public Hive getHive() {
        return this.hive;
    }
}
