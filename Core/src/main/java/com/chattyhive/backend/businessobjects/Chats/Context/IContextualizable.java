package com.chattyhive.backend.businessobjects.Chats.Context;

import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

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
