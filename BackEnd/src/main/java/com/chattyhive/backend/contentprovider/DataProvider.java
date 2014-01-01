package com.chattyhive.backend.contentprovider;

import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionStateChange;
import com.chattyhive.backend.contentprovider.server.Server;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;

import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
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
    private ConnectionState _targetState;
    private Boolean _networkAvailable = true;

    public void SubscribeChannelEventHandler(EventHandler<PubSubChannelEventArgs> eventHandler) {
        this._pubsub.SubscribeChannelEventHandler(eventHandler);
    }
    public void SubscribeConnectionEventHandler(EventHandler<PubSubConnectionEventArgs> eventHandler) {
        this._pubsub.SubscribeConnectionEventHandler(eventHandler);
    }

    public String getUser() { return this._user.getLogin(); }

    public DataProvider(ServerUser user, String serverApp) {
        this._user = user;
        this._server = new Server(this._user.getLogin(),serverApp);

        this._pubsub = new PubSub(this._user.getLogin());

        try {
            this._pubsub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
            this._pubsub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this,"onConnectionEvent",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }

        this._pubsub.Join("public_test");
    }

    public void Connect() {
        if (this._networkAvailable) {
            this._server.Connect();
            this._targetState = ConnectionState.CONNECTED;
            this._pubsub.Connect();
        }
    }

    public void Disconnect() {
        this._targetState = ConnectionState.DISCONNECTED;
        this._pubsub.Disconnect();
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

    public void onChannelEvent(Object sender, PubSubChannelEventArgs args) {
        if (args.getEventName().compareTo("msg") == 0) {
            // We have a message so lets play with it.
            // Save the message.
        }
    }

    public void onConnectionEvent(Object sender, PubSubConnectionEventArgs args) {
        ConnectionStateChange change = args.getChange();
        if ((this._targetState == ConnectionState.CONNECTED) && (change.getCurrentState() == ConnectionState.DISCONNECTED)) {
            this._pubsub.Connect();
        } else if ((this._targetState == ConnectionState.DISCONNECTED) && (change.getCurrentState() == ConnectionState.CONNECTED)) {
            this._pubsub.Disconnect();
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

