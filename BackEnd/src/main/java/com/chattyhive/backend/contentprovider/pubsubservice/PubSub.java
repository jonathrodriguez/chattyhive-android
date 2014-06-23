package com.chattyhive.backend.contentprovider.pubsubservice;

import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.channel.PresenceChannel;
import com.pusher.client.channel.PresenceChannelEventListener;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.User;
import com.pusher.client.connection.ConnectionEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Jonathan on 17/10/13.
 * This class represents an interface to provide abstraction from pusher API.
 */
public class PubSub implements ChannelEventListener, ConnectionEventListener, PrivateChannelEventListener, PresenceChannelEventListener {

    private static String APP_KEY = "f073ebb6f5d1b918e59e"; //"5bec9fb4b45d83495627";
	private static String CLUSTER;// = "eu";
    private Pusher pusher;
    private PubSubAuthorizer pubSubAuthorizer;
    private TreeMap<String,Channel> lista_canales;

    private Event<PubSubChannelEventArgs> _pubSubChannelEvent;

    /**
     * This method permits to subscribe an event handler to the PubSub channel event.
     * @param handler the event handler to be subscribed.
     */
    public void SubscribeChannelEventHandler (EventHandler<PubSubChannelEventArgs> handler) {
        this._pubSubChannelEvent.add(handler);
    }

    private Event<PubSubConnectionEventArgs> _pubSubConnectionEvent;

    /**
     * This method permits to subscribe an event handler to the PubSub connection event.
     * @param handler the event handler to be subscribed.
     */
    public void SubscribeConnectionEventHandler (EventHandler<PubSubConnectionEventArgs> handler) {
        this._pubSubConnectionEvent.add(handler);
    }

    @Override
    /**
     * Method from the interface "ChannelEventListener".
     */
    public void onSubscriptionSucceeded(String channelName) {
        this._pubSubChannelEvent.fire(this,new PubSubChannelEventArgs(channelName,"SubscriptionSucceeded", TimestampFormatter.toString(new Date())));
    }

    @Override
    /**
     * Method from the interface "ChannelEventListener".
     */
    public void onEvent(String channelName, String eventName, String data) {
        this._pubSubChannelEvent.fire(this,new PubSubChannelEventArgs(channelName,eventName,data));
        //System.out.println("Channel event: ".concat(channelName).concat(" -> ").concat(eventName).concat(" : ").concat(data));
    }

    @Override
    /**
     * Method from the interface "ConnectionEventListener".
     */
    public void onConnectionStateChange(com.pusher.client.connection.ConnectionStateChange change) {
        ConnectionState pS = ConnectionState.valueOf(change.getPreviousState().toString());
        ConnectionState nS = ConnectionState.valueOf(change.getCurrentState().toString());
        this._pubSubConnectionEvent.fire(this,new PubSubConnectionEventArgs(new ConnectionStateChange(pS,nS)));
    }

    @Override
    /**
     * Method from the interface "ConnectionEventListener".
     */
    public void onError(String message, String code, Exception e) {
        // System.out.print("Error " + code + ": " + message);
    }

    /**
     * Public constructor.
     * @param serverUser the serverUser object which provides authentication tokens.
     */
    public PubSub(ServerUser serverUser) {
        this._pubSubConnectionEvent = new Event<PubSubConnectionEventArgs>();
        this._pubSubChannelEvent = new Event<PubSubChannelEventArgs>();

        lista_canales = new TreeMap<String,Channel>();
        pubSubAuthorizer = new PubSubAuthorizer(serverUser);

        PusherOptions pO = new PusherOptions();
        pO.setEncrypted(false);
        pO.setAuthorizer(this.pubSubAuthorizer);

        if ((CLUSTER != null) && (CLUSTER.length() > 0)) {
            pO.setCluster(CLUSTER);
        }
        pusher = new Pusher(APP_KEY,pO);

        pusher.getConnection().bind(com.pusher.client.connection.ConnectionState.ALL, this);
    }

    /**
     * Establishes the connection with pusher.
     */
    public void Connect() {
        pusher.connect();
    }

    /**
     * Disconnects from pusher.
     */
    public void Disconnect() {
        for (String channel : lista_canales.keySet())
            this.Leave(channel);

        pusher.disconnect();
    }

    /**
     * Retrieves the actual connection status.
     * @return
     */
    public ConnectionState GetConnectionState() {
        ConnectionState pS = ConnectionState.valueOf(pusher.getConnection().getState().toString());
        return pS;
    }

    /**
     * Subscribes to a channel and binds the msg event.
     * @param channel_name the name of the chanel to join.
     */
    public Boolean Join(String channel_name) {
        Channel canal;
        if (lista_canales.containsKey(channel_name))
            this.Leave(channel_name);
        try {
            if (channel_name.startsWith("presence-")) {
                canal = pusher.subscribePresence(channel_name, this);

            } else if (channel_name.startsWith("private-")) {
                canal = pusher.subscribePrivate(channel_name, this);

            } else {
                canal = pusher.subscribe(channel_name, this);
                
            }
            canal.bind("msg", this);
            lista_canales.put(channel_name,canal);
        } catch (IllegalArgumentException e) { return false; }
        return true;
    }

    public Boolean Leave(String channel_name) {
        lista_canales.get(channel_name).unbind("msg",this);
        lista_canales.remove(channel_name);
        try {
            pusher.unsubscribe(channel_name);
        } catch (IllegalStateException e) { return false; }
        return true;
    }

    /**
     * Called when the subscription has succeeded and an initial list of subscribed users has been received from Pusher.
     *
     * @param channelName The name of the channel the list is for.
     * @param users       The users.
     */
    @Override
    public void onUsersInformationReceived(String channelName, Set<User> users) {

    }

    /**
     * Called when a new user subscribes to the channel.
     *
     * @param channelName channelName The name of the channel the list is for.
     * @param user        The newly subscribed user.
     */
    @Override
    public void userSubscribed(String channelName, User user) {

    }

    /**
     * Called when an existing user unsubscribes from the channel.
     *
     * @param channelName The name of the channel that the user unsubscribed from.
     * @param user        The user who unsubscribed.
     */
    @Override
    public void userUnsubscribed(String channelName, User user) {

    }

    /**
     * Called when an attempt to authenticate a private channel fails.
     *
     * @param message A description of the problem.
     * @param e       An associated exception, if available.
     */
    @Override
    public void onAuthenticationFailure(String message, Exception e) {

    }
}