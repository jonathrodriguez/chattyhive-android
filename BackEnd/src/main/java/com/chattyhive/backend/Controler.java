package com.chattyhive.backend;

import com.chattyhive.backend.server.ServerUser;
import com.chattyhive.backend.server.pubsubservice.PubSub;

/**
 * Created by Jonathan on 11/12/13.
 */
public class Controler {
    // BussinesObjects
    private DataProvider _dataProvider;

    private PubSub.PubSubChannelEventListener _pubSubChannelEventListener;

    public Controler (ServerUser user, PubSub.PubSubChannelEventListener pubSubChannelEventListener) {
        this._pubSubChannelEventListener = pubSubChannelEventListener;
        this._dataProvider = new DataProvider(user,new PubSub.PubSubChannelEventListener() {
            @Override
            public void onChannelEvent(String channel_name, String event_name, String message) {
                ChannelEvent(channel_name,event_name,message);
            }
        });
    }

    public void getMessages() {
        this._dataProvider.RecoverMessages("");
    }

    private void ChannelEvent(String channel_name, String event_name, String message) {
        // Propagate event up.
        if (this._pubSubChannelEventListener != null) {
            this._pubSubChannelEventListener.onChannelEvent(channel_name,event_name,message);
        }
    }
}
