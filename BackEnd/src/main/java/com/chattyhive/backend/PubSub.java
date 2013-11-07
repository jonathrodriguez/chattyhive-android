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

public class PubSub {

    public interface PubSubChannelEventListener {
        public void onChannelEvent(String channel_name, String event_name, String message);
    }

    private static String APP_KEY = "8817c5eeccfb1ea2d1c6"; //"5bec9fb4b45d83495627";
	private static String CLUSTER = "eu";
    private Pusher pusher;
    private String nick;
    private ArrayList lista_canales;

    public PubSubChannelEventListener pscel;

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
        pusher = new Pusher(APP_KEY,(new PusherOptions().setEncrypted(true).setAuthorizer(new HttpAuthorizer("http://www.leggetter.co.uk/pusher/pusher-examples/php/authentication/src/private_auth.php"))));

        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                System.out.print(change.toString());
            }

            @Override
            public void onError(String message, String code, Exception e) {
                System.out.print("Error "+code+": "+message);
            }
        }, ConnectionState.ALL);
    }

    public void Join(String channel_name) {
        Channel canal;
        canal = pusher.subscribe(channel_name, new ChannelEventListener() {
            @Override
            public void onSubscriptionSucceeded(String channelName) {
                System.out.print("Conectado a: "+channelName);
            }

            @Override
            public void onEvent(String channelName, String eventName, String data) {
                pscel.onChannelEvent(channelName, eventName, data);
            }
        });

        lista_canales.add(canal);
    }
}