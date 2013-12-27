package com.chattyhive.backend.contentprovider;

import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.bussinesobjects.Message;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;

import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Jonathan on 11/12/13.
 */
public class DataProvider {

    private ServerUser _user;
    private Server _server;
    private PubSub _pubsub;
    private PubSub.PubSubChannelEventListener _pubSubChannelEventListener;
    private Boolean _networkAvailable = false;

    public String getUser() { return this._user.getLogin(); }

    public DataProvider(ServerUser user, PubSub.PubSubChannelEventListener pubSubChannelEventListener) {
        this(user, StaticParameters.DefaultServerAppName,pubSubChannelEventListener);
    }

    public DataProvider(ServerUser user, String serverApp,PubSub.PubSubChannelEventListener pubSubChannelEventListener) {
        this._user = user;
        this._server = new Server(this._user.getLogin(),serverApp);
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

    public void sendMessage(JsonElement message) {
        //
        // TODO: Save message.
        //
        this._server.SendMessage(message.toString());
    }

    public void sendMessage(String message) {
        //
        // TODO: Save message.
        //
        this._server.SendMessage(message);
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

    public void setNetworkAvailable(Boolean value) { this._networkAvailable = value; }
    public Boolean getNetworkAvailable() { return this._networkAvailable; }
}

