package com.chattyhive.Core.BusinessObjects.Chats;

import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Subscriptions.SubscriberList;
import com.chattyhive.Core.BusinessObjects.Subscriptions.Subscription;
import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_SYNC;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_ID;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.PROFILE_ID;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.Util.CallbackDelegate;

import java.util.ArrayList;

/**
 * Created by jonathrodriguez on 01/11/2015.
 */
public class PublicHiveChat extends Chat {

    /**************************************************/
    /* Fields                                         */
    /**************************************************/
    protected Hive hive;
    /**************************************************/
    /* Constructors                                   */
    /**************************************************/
    public PublicHiveChat(Controller controller, Format format) {
        super(controller, format);
    }
    public PublicHiveChat(Controller controller, CallbackDelegate callback, String chatID, String accountID) {
        super(controller, callback, chatID, accountID);
    }

    /**************************************************/
    /* Getters and Setters                            */
    /**************************************************/

    public Hive getHive() { return this.hive; }
    public void setHive(Hive value) { this.hive = value; } // TODO: �Is this method necessary?

    /*************************************/
    /*         PARSE METHODS             */
    /*************************************/
    @Override
    public Format toFormat(Format format) {
       /* if (format instanceof PUBLIC_HIVE_CHAT) {
            ((PUBLIC_HIVE_CHAT) format).HIVE_ID = this.hive.getID();
        }*/

        return super.toFormat(format);
    }

    @Override
    public Boolean fromFormat(Format format) {
        /*if (format instanceof PUBLIC_HIVE_CHAT) {
            this.hive = this.controller.getHive(((PUBLIC_HIVE_CHAT) format).HIVE_ID);
            if (this.hive == null) {
                HIVE_ID hiveId = new HIVE_ID();
                hiveId.NAME_URL = ((PUBLIC_HIVE_CHAT) format).HIVE_ID;
                this.hive = new Hive(hiveId,null,"");
            }
        }*/

        return super.fromFormat(format);
    }
}
