package com.chattyhive.backend;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;
import com.pusher.client.util.HttpAuthorizer;

import java.util.ArrayList;
import java.util.Date;

import sun.rmi.runtime.Log;

public class PubSub {

    public interface PubSubChannelEventListener {
        public void onChannelEvent(String channel_name, String event_name, String message);
    }
    public interface PubSubConnectionEventListener {
        public void onConnectionStateChange(ConnectionStateChange change);
    }

    private static String APP_KEY = "f073ebb6f5d1b918e59e"; //"8817c5eeccfb1ea2d1c6"; //"5bec9fb4b45d83495627";
	private static String CLUSTER = "eu";
    private Pusher pusher;
    private String nick;
    private ArrayList lista_canales;

    public PubSubChannelEventListener pscel;
    public PubSubConnectionEventListener psconel;

    public PubSub() {
        this("sin_nombre");
    }

    public PubSub(String nickname, PubSubChannelEventListener listener) {
        this(nickname);
        this.pscel = listener;
    }

    public PubSub(String nickname) {
        lista_canales = new ArrayList();
        nick = nickname;
        pusher = new Pusher(APP_KEY,(new PusherOptions().setEncrypted(false)));

        pusher.getConnection().bind(ConnectionState.ALL, new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                if (psconel != null)
                    psconel.onConnectionStateChange(change);
            }

            @Override
            public void onError(String message, String code, Exception e) {
               // System.out.print("Error " + code + ": " + message);
            }
        });

        //pusher.connect();
    }

    public void Connect() { pusher.connect(); }
    public void Disconnect() {
        for (int i = 0; i < lista_canales.size(); i++) {
            Channel c = (Channel)lista_canales.get(i);
            pusher.unsubscribe(c.getName());
        }
        lista_canales.clear();
        pusher.disconnect();
    }
    public ConnectionState GetConnectionState() { return pusher.getConnection().getState(); }
    public void setConnectionEventListener(PubSubConnectionEventListener listener) { psconel = listener; }

    public void Join(String channel_name) {
        for (int i = 0; i < lista_canales.size(); i++) {
            Channel c = (Channel)lista_canales.get(i);
            if (c.getName().equalsIgnoreCase(channel_name))
                return;
        }
        while (pusher.getConnection().getState() != ConnectionState.CONNECTED) {
            //System.out.print("Pusher no está conectado.");
            int Timeout = 10;
            while ((pusher.getConnection().getState() != ConnectionState.CONNECTED) && (pusher.getConnection().getState() != ConnectionState.DISCONNECTED) && (Timeout > 0)) {
                Timeout--;
                try { wait(1000); } catch (Exception e) { return; };
            }

            if ((pusher.getConnection().getState() != ConnectionState.CONNECTED) && (pusher.getConnection().getState() != ConnectionState.DISCONNECTED)) {
                return;
            } else if ((pusher.getConnection().getState() == ConnectionState.DISCONNECTED)) {
                pusher.connect();
            }
        }
        Channel canal;
        Boolean retry = true;
        while (retry) {
            try {
                retry = false;
                canal = pusher.subscribe(channel_name,new ChannelEventListener() {
                    @Override
                    public void onSubscriptionSucceeded(String channelName) {
                        if (pscel != null)
                            pscel.onChannelEvent(channelName, "SubscriptionSucceeded", (new Date()).toString());
                    }

                    @Override
                    public void onEvent(String channelName, String eventName, String data) {
                        if (pscel != null)
                            pscel.onChannelEvent(channelName, eventName, data);
                    }
                });
                canal.bind("msg",new ChannelEventListener() {
                    @Override
                    public void onSubscriptionSucceeded(String channelName) {
                        if (pscel != null)
                            pscel.onChannelEvent(channelName, "SubscriptionSucceeded", (new Date()).toString());
                    }

                    @Override
                    public void onEvent(String channelName, String eventName, String data) {
                        if (pscel != null)
                            pscel.onChannelEvent(channelName, eventName, data);
                    }
                });
                lista_canales.add(canal);
            } catch (IllegalArgumentException e) {
                if (e.getMessage().startsWith("Already subsribed to a channel with name")) {
                    pusher.unsubscribe(channel_name);
                    retry = true;
                }
            }
        }
    }
}