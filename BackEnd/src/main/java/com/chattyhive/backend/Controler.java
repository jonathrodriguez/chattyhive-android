package com.chattyhive.backend;

import com.chattyhive.backend.bussinesobjects.Message;
import com.chattyhive.backend.bussinesobjects.User;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.chattyhive.backend.contentprovider.pubsubservice.PubSub;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
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

    public Controler (ServerUser user, String serverApp, PubSub.PubSubChannelEventListener pubSubChannelEventListener) {
        this._pubSubChannelEventListener = pubSubChannelEventListener;
        this._dataProvider = new DataProvider(user, serverApp, new PubSub.PubSubChannelEventListener() {
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
        //message._user = new User(this._dataProvider.getUser());
        //this._dataProvider.sendMessage(message.toJson());
        this._dataProvider.sendMessage("message=".concat(message._content.getContent().replace("+", "%2B").replace(" ", "+")).concat("&timestamp=").concat(TimestampFormatter.toString(message.getTimeStamp()).replace(":", "%3A").replace("+", "%2B").replace(" ", "+")));
    }

    private void ChannelEvent(String channel_name, String event_name, String message) {
        // Propagate event up.
        if (this._pubSubChannelEventListener != null) {
            this._pubSubChannelEventListener.onChannelEvent(channel_name,event_name,message);
        }
    }
}
