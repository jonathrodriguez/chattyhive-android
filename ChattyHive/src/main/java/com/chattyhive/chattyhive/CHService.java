package com.chattyhive.chattyhive;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.chattyhive.backend.Controler;
import com.chattyhive.backend.server.ServerUser;
import com.chattyhive.backend.server.pubsubservice.PubSub;

public class CHService extends Service {

    private ServerUser _serverUser;
    private Controler _controler;

    public CHService() {
        this._serverUser = new ServerUser("","");

        this._controler = new Controler(this._serverUser,new PubSub.PubSubChannelEventListener() {
            @Override
            public void onChannelEvent(String channel_name, String event_name, String message) {
                ChannelEvent(channel_name,event_name,message);
            }
        });
    }

    private void ChannelEvent(String channel_name, String event_name, String message) {
        // Do something with event
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
