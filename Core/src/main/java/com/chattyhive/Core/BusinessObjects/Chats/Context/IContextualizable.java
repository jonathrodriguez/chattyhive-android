package com.chattyhive.Core.BusinessObjects.Chats.Context;

import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;

import java.util.List;

/**
 * Created by Jonathan on 10/11/2014.
 */
public interface IContextualizable {
    public ContextElement getCommunityContext();
    public ContextElement getBaseContext();
    public ContextElement getParentContext();

    public Event<EventArgs> getOnContextLoaded();
    public void loadContext(int numberImages,int numberNewUsers, int numberBuzzes);

    public List<ContextElement> getPublicChats();
    public List<Message> getSharedImages();
    public List<User> getNewUsers();
    public List<User> getUsers();
    public List<Message> getTrendingBuzzes();
    public List<ContextElement> getOtherChats();
}
