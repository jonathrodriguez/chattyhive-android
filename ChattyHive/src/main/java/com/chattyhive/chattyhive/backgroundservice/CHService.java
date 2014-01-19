package com.chattyhive.chattyhive.backgroundservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Binder;
import android.os.IBinder;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Message;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.chattyhive.Home;
import com.chattyhive.chattyhive.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CHService extends Service {
    private Controller _controller;
    private NotificationManager _notificationManager;
    private int _pendingMsgs;
    private Boolean _appOpen = false;
    private final IBinder binder = new Binder();

    public CHService() { }

    public void onChannelEvent(Object sender, ChannelEventArgs args) {
        // Do something with event
        if ((!this._appOpen) && (args.getEventName().compareTo("msg")==0)) {
            this._notificationManager.cancelAll();
            this._pendingMsgs++;
            PendingIntent i= PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);
            CHNotificationBuilder chNotificationBuilder = new CHNotificationBuilder(this.getApplicationContext());
            chNotificationBuilder.setTickerText(String.format(this.getString(R.string.buzz_in_hive_Ticker),"@".concat(args.getMessage().getUser().getUsername()),":".concat(args.getChannelName())));
            chNotificationBuilder.setTitleText(String.format(this.getString(R.string.buzz_in_hive_TITLE), ":".concat(args.getChannelName())));
            chNotificationBuilder.setMainText(String.format(this.getString(R.string.buzz_in_hive_mainText), this._pendingMsgs));

            ArrayList<Message> messages = this._controller.getMessages(args.getChannelName());
            ArrayList<String> subText = new ArrayList<String>();
            for (int idx = (messages.size()-this._pendingMsgs); idx < messages.size(); idx++) {
                subText.add(String.format(this.getString(R.string.buzz_in_hive_subText),"@".concat(messages.get(idx).getUser().getUsername()),messages.get(idx).getMessage().getContent()));
            }

            chNotificationBuilder.setSubText(subText);
            chNotificationBuilder.setMainAction(i);
            _notificationManager.notify(0,chNotificationBuilder.Build());
        }
    }

    public void onConnectionEvent (Object sender, PubSubConnectionEventArgs args) {
        if (args.getChange().getCurrentState() == ConnectionState.DISCONNECTED) {
            // TODO: Check network availability. If not available don't try to reconnect until available.
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    //Called by the system when the device configuration changes while your component is running.
    public void onConfigurationChanged(Configuration newConfig) {}

    @Override
    //Called by the system when the service is first created.
    public void onCreate() {
        _notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        _pendingMsgs = 0;
        _controller = Controller.getRunningController();
        try {
            Controller.SubscribeToAppBindingEvent(new EventHandler<EventArgs>(this,"onAppBinding",EventArgs.class));
        } catch (NoSuchMethodException e) {}

        if (this._controller != null) {
            try {
                this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
                this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this,"onConnectionEvent",PubSubConnectionEventArgs.class));
            } catch (NoSuchMethodException e) { }
        }
    }

    public void onAppBinding(Object sender,EventArgs e) {
        this._appOpen = Controller.isAppBounded();
        if (this._appOpen) {
            this._notificationManager.cancelAll();
        } else {
            this._pendingMsgs = 0;
            checkConnected();
        }
    }

    @Override
    //Called by the system to notify a Service that it is no longer used and is being removed.
    public void onDestroy() {
        Controller.disposeRunningController();
        this._controller = null;
    }

    @Override
    //This is called when the overall system is running low on memory, and actively running processes should trim their memory usage.
    public void 	onLowMemory() {}

    @Override
    //Called when new clients have connected to the service, after it had previously been notified that all had disconnected in its onUnbind(Intent).
    public void 	onRebind(Intent intent) {}

 /*   @Override
    //This method was deprecated in API level 5. Implement onStartCommand(Intent, int, int) instead.
    public void 	onStart(Intent intent, int startId) {}*/

    @Override
    //Called by the system every time a client explicitly starts the service by calling startService(Intent), providing the arguments it supplied and a unique integer token representing the start request.
    public int 	onStartCommand(Intent intent, int flags, int startId) {
        if (!(this._appOpen = Controller.isAppBounded()))
            checkConnected();
        return START_STICKY;
    }

    private void checkConnected () {
        if (Controller.getRunningController() != this._controller) {
            this._controller = Controller.getRunningController();
            try {
                this._controller.SubscribeChannelEventHandler(new EventHandler<ChannelEventArgs>(this,"onChannelEvent",ChannelEventArgs.class));
                this._controller.SubscribeConnectionEventHandler(new EventHandler<PubSubConnectionEventArgs>(this,"onConnectionEvent",PubSubConnectionEventArgs.class));
            } catch (NoSuchMethodException e) { }
        }
        if ((this._controller.getServerUser() == null) ||
                (this._controller.getServerUser().getLogin().isEmpty())) {
            PendingIntent i= PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);
            CHNotificationBuilder chNotificationBuilder = new CHNotificationBuilder(this.getApplicationContext());
            chNotificationBuilder.setTickerText("No user login data!");
            chNotificationBuilder.setTitleText("Welcome to chattyhive!");
            chNotificationBuilder.setMainText("There's no user data. Please, touch here to loggin.");
            chNotificationBuilder.setMainAction(i);
            _notificationManager.notify(0,chNotificationBuilder.Build());
        }
    }

    @Override
    //This is called if the service is currently running and the user has removed a task that comes from the service's application.
    public void 	onTaskRemoved(Intent rootIntent) {}

    @Override
    //Called when the operating system has determined that it is a good time for a process to trim unneeded memory from its process.
    public void 	onTrimMemory(int level) {}

    @Override
    //Called when all clients have disconnected from a particular interface published by the service.
    public boolean 	onUnbind(Intent intent) {
        return true;
    }

    @Override
    //Print the Service's state into the given stream.
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {}

}
