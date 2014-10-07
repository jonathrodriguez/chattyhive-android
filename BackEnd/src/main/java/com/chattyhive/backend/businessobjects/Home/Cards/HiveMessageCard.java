package com.chattyhive.backend.businessobjects.Home.Cards;

import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Home.HomeCard;
import com.chattyhive.backend.businessobjects.Home.HomeCardType;

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
        if (message.getChat().getParent().getGroupKind() != GroupKind.HIVE) throw new IllegalArgumentException("Message must be from public chat of a hive.");
        this.message = message;
        this.hive = message.getChat().getParent().getParentHive();
    }
    public Message getMessage() {
        return this.message;
    }
    public Hive getHive() {
        return this.hive;
    }
}
