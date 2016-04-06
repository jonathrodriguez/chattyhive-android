package com.chattyhive.Core.BusinessObjects.Chats;

import com.chattyhive.Core.BusinessObjects.Chats.Context.ContextElement;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.Controller;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;

import java.util.List;

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
    public void setHive(Hive value) { this.hive = value; } // TODO: ¿Is this method necessary?

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

    @Override
    public ContextElement getCommunityContext() {
        return null;
    }

    @Override
    public ContextElement getBaseContext() {
        return null;
    }

    @Override
    public ContextElement getParentContext() {
        return null;
    }

    @Override
    public Event<EventArgs> getOnContextLoaded() {
        return null;
    }

    @Override
    public void loadContext(int numberImages, int numberNewUsers, int numberBuzzes) {

    }

    @Override
    public List<ContextElement> getPublicChats() {
        return null;
    }

    @Override
    public List<Message> getSharedImages() {
        return null;
    }

    @Override
    public List<User> getNewUsers() {
        return null;
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public List<Message> getTrendingBuzzes() {
        return null;
    }

    @Override
    public List<ContextElement> getOtherChats() {
        return null;
    }
}
