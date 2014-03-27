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
 * This class is intended to provide a generic interface to access data independently
 * from where data comes from. Possible data origins are local, server and pusher.
 */
public class DataProvider {

    private ServerUser _user;
    private Server _server;
    private PubSub _pubsub;
    private ConnectionState _targetState;
    private Boolean _networkAvailable = true;

    /**
     * Permits other classes to subscribe to pusher channel events.
     * @param eventHandler an event handler that points to the method to be invoked.
     */
    public void SubscribeChannelEventHandler(EventHandler<PubSubChannelEventArgs> eventHandler) {
        this._pubsub.SubscribeChannelEventHandler(eventHandler);
    }

    /**
     * Permits other classes to subscribe to pusher connection events.
     * @param eventHandler an event handler that points to the method to be invoked.
     */
    public void SubscribeConnectionEventHandler(EventHandler<PubSubConnectionEventArgs> eventHandler) {
        this._pubsub.SubscribeConnectionEventHandler(eventHandler);
    }

    /**
     * Retrieves user login information.
     * @return a string containing the user login-
     */
    public String getUser() { return this._user.getLogin(); }

    /**
     * Retrieves the Server User.
     * @return
     */
    public ServerUser getServerUser() { return this._user; }
    /**
     * Changes the server user.
     * @param newUser the new server user.
     */
    public void setUser(ServerUser newUser) {
        this._user = newUser;
        this._server.setServerUser(this._user);
    }

    /**
     * Changes the server app.
     * @param serverApp
     */
    public void setServerApp (String serverApp) {
        this._server.setAppName(serverApp);
    }

    /**
     * Public constructor.
     * @param user the server user data to use in connections.
     * @param serverApp the server application to be used.
     */
    public DataProvider(ServerUser user, String serverApp) {
        this._user = user;
        this._server = new Server(this._user,serverApp);

        this._pubsub = new PubSub(this._user.getLogin());

        try {
            this._pubsub.SubscribeChannelEventHandler(new EventHandler<PubSubChannelEventArgs>(this,"onChannelEvent",PubSubChannelEventArgs.class));
            this._pubsub.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this,"onConnectionEvent",PubSubConnectionEventArgs.class));
        } catch (NoSuchMethodException e) { }

        this._pubsub.Join("public_test");
    }

    /**
     * Establishes the connection with server and pusher.
     * @return true if connected to our server, else false
     */
    public Boolean Connect() {
        Boolean result = false;
        if (this._networkAvailable) {
            result = this._server.Connect();
            this._targetState = ConnectionState.CONNECTED;
            this._pubsub.Connect();
        }
        return result;
    }

    /**
     * Closes the connection with pusher. (The server does not provide a logout method yet).
     */
    public void Disconnect() {
        this._targetState = ConnectionState.DISCONNECTED;
        this._pubsub.Disconnect();
    }

    /**
     * Sends a message, which is correctly JSON formatted, to the server.
     * @param message a JSONElement with the message.
     */
    public Boolean sendMessage(JsonElement message) {
        //
        // TODO: Save message.
        //
        return this._server.SendMessage(message.toString());
    }

    /**
     * Sends a message, which is string represented, to the server.
     * @param message a String representing the message to be sent.
     */
    public Boolean sendMessage(String message) {
        //
        // TODO: Save message.
        //
        return this._server.SendMessage(message);
    }

    /**
     * This method will be invoked on a channel event. Its function is to save the message locally, so
     * next time the message has to be read it will be read from local storage.
     * @param sender the object which fired the event.
     * @param args the event arguments.
     */
    public void onChannelEvent(Object sender, PubSubChannelEventArgs args) {
        if (args.getEventName().compareTo("msg") == 0) {
            // We have a message so lets play with it.
            // Save the message.
        }
    }

    /**
     * This method will be invoked on a connection event. If the target state doesn't match the current connection state
     * then the last connection operation (Connect or Disconnect) is retried.
     * @param sender the object which fired the event.
     * @param args the event arguments.
     */
    public void onConnectionEvent(Object sender, PubSubConnectionEventArgs args) {
        ConnectionStateChange change = args.getChange();
        if ((this._targetState == ConnectionState.CONNECTED) && (change.getCurrentState() == ConnectionState.DISCONNECTED)) {
            this._pubsub.Connect();
        } else if ((this._targetState == ConnectionState.DISCONNECTED) && (change.getCurrentState() == ConnectionState.CONNECTED)) {
            this._pubsub.Disconnect();
        }
    }

    /**
     * This method recovers the message list for a chat. It has to merge local data with remote data.
     * @param chatID the identification of the chat.
     * @return an ArrayList with the messages.
     */
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

    /**
     * It si very important to know network availability before trying any network operation. There are some
     * functions in android API which permit to retrieve network status, so it's necessary to provide
     * this information to the data provider.
     * @param value a Boolean value indicating whether the network is available.
     */
    public void setNetworkAvailable(Boolean value) { this._networkAvailable = value; }

    /**
     * Returns a value indicating what the data provider was last informed about the network state
     * @return a Boolean value indicating network availability.
     */
    public Boolean getNetworkAvailable() { return this._networkAvailable; }
}

