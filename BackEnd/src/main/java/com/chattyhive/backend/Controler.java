package com.chattyhive.backend;

import com.chattyhive.backend.bussinesobjects.Message;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;
import com.google.gson.JsonElement;

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

    public void sendMessage(Message message) {
        JsonElement json = message.toJson();
        this._dataProvider.sendMessage(message.toJson());
    }

    private void ChannelEvent(String channel_name, String event_name, String message) {
        // Propagate event up.
        if (this._pubSubChannelEventListener != null) {
            this._pubSubChannelEventListener.onChannelEvent(channel_name,event_name,message);
        }
    }
}
