package com.chattyhive.backend.contentprovider.pubsubservice;

import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 17/10/13.
 * This class represents an interface to provide abstraction from pusher API.
 */
public class PubSub implements ChannelEventListener, ConnectionEventListener {

    private static String APP_KEY = "f073ebb6f5d1b918e59e"; //"5bec9fb4b45d83495627";
	private static String CLUSTER;// = "eu";
    private Pusher pusher;
    private String nick;
    private ArrayList lista_canales;

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
     * @param nickname the name of the user.
     */
    public PubSub(String nickname) {
        this._pubSubConnectionEvent = new Event<PubSubConnectionEventArgs>();
        this._pubSubChannelEvent = new Event<PubSubChannelEventArgs>();

        lista_canales = new ArrayList();
        nick = nickname;
        PusherOptions pO = new PusherOptions();
        pO.setEncrypted(false);
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
        lista_canales.clear();
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
    public void Join(String channel_name) {
        Channel canal = pusher.subscribe(channel_name,this);
        canal.bind("msg",this);
        lista_canales.add(canal);
    }
}