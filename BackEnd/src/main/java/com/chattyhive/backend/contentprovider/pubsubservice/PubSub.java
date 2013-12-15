package com.chattyhive.backend.contentprovider.pubsubservice;

import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;

import java.util.ArrayList;
import java.util.Date;

public class PubSub {

    public interface PubSubChannelEventListener {
        public void onChannelEvent(String channel_name, String event_name, String message);
    }
    public interface PubSubConnectionEventListener {
        public void onConnectionStateChange(ConnectionStateChange change);
    }

    private static String APP_KEY = "f073ebb6f5d1b918e59e"; //"5bec9fb4b45d83495627";
	private static String CLUSTER;// = "eu";
    private Pusher pusher;
    private String nick;
    private ArrayList lista_canales;

    private final ChannelEventListener channelEventListener = new ChannelEventListener() {
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
    };

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
        PusherOptions pO = new PusherOptions();
        pO.setEncrypted(false);
        if ((CLUSTER != null) && (CLUSTER.length() > 0)) {
            pO.setCluster(CLUSTER);
        }
        pusher = new Pusher(APP_KEY,pO);

        pusher.getConnection().bind(com.pusher.client.connection.ConnectionState.ALL, new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(com.pusher.client.connection.ConnectionStateChange change) {
                if (psconel != null) {
                    ConnectionState pS = ConnectionState.valueOf(change.getPreviousState().toString());
                    ConnectionState nS = ConnectionState.valueOf(change.getCurrentState().toString());

                    psconel.onConnectionStateChange(new ConnectionStateChange(pS,nS));
                }
            }

            @Override
            public void onError(String message, String code, Exception e) {
               // System.out.print("Error " + code + ": " + message);
            }
        });

        //pusher.connect();
    }

    public void Connect() {
        pusher.connect();
    }
    public void Disconnect() {
        lista_canales.clear();
        pusher.disconnect();
    }

    public ConnectionState GetConnectionState() {
        ConnectionState pS = ConnectionState.valueOf(pusher.getConnection().getState().toString());
        return pS;
    }
    public void setConnectionEventListener(PubSubConnectionEventListener listener) {
        psconel = listener;
    }

    public void Join(String channel_name) {
        Channel canal = pusher.subscribe(channel_name,channelEventListener);
        canal.bind("msg",channelEventListener);
        lista_canales.add(canal);
    }
}