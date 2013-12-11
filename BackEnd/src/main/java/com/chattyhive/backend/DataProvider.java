package com.chattyhive.backend;

import com.chattyhive.backend.bussinesobjects.Message;
import com.chattyhive.backend.server.Server;
import com.chattyhive.backend.server.ServerUser;
import com.chattyhive.backend.server.pubsubservice.PubSub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Jonathan on 11/12/13.
 */
public class DataProvider {

    private ServerUser _user;
    private Server _server;
    private PubSub _pubsub;
    private PubSub.PubSubChannelEventListener _pubSubChannelEventListener;

    public DataProvider(ServerUser user, PubSub.PubSubChannelEventListener pubSubChannelEventListener) {
        this._user = user;
        this._server = new Server(this._user.getLogin(),"chtest1");
        this._server.Connect();

        this._pubSubChannelEventListener = pubSubChannelEventListener;

        this._pubsub = new PubSub(this._user.getLogin(),new PubSub.PubSubChannelEventListener() {
            @Override
            public void onChannelEvent(String channel_name, String event_name, String message) {
                ChannelEvent(channel_name,event_name,message);
            }
        });
        this._pubsub.Join("public_test");
        this._pubsub.Connect();
    }

    private void ChannelEvent(String channel_name, String event_name, String message) {
        if (event_name.compareTo("msg") == 0) {
            // We have a message so lets play with it.
            // Save the message.
        }
        // Propagate event up.
        if (this._pubSubChannelEventListener != null) {
            this._pubSubChannelEventListener.onChannelEvent(channel_name,event_name,message);
        }
    }

    public ArrayList<Message> RecoverMessages(String chatID) {
        ArrayList<Message> messageList = new ArrayList<Message>();

        //
        // TODO: Try to get messageList from local.
        //

        //
        // TODO: Try to get newer messages from server.
        //

        Collections.sort(messageList);
        return messageList;
    }
}

