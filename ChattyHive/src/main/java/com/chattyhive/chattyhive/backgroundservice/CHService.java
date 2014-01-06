package com.chattyhive.chattyhive.backgroundservice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.pubsubservice.ConnectionState;
import com.chattyhive.backend.util.events.ChannelEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.PubSubConnectionEventArgs;
import com.chattyhive.chattyhive.Home;
import com.chattyhive.chattyhive.LoginActivity;

import java.io.FileDescriptor;
import java.io.PrintWriter;

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
            if (this._pendingMsgs == 1) {
                Notification notification;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    notification = new Notification.Builder(this)
                            .setContentTitle("chattyhive. New messages.")
                            .setContentText("You have ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."))
                            .build();
                } else {
                    notification = new Notification(android.R.drawable.sym_def_app_icon,"chattyhive. ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."),System.currentTimeMillis());
                    PendingIntent i= PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);
                    notification.setLatestEventInfo(this, "You have new messages!","There are ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."), i);
                }
                _notificationManager.notify(0,notification);
            } else {
                Notification notification;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    notification = new Notification.Builder(this)
                            .setContentTitle("chattyhive. New messages.")
                            .setContentText("You have ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."))
                            .build();
                } else {
                    notification = new Notification(android.R.drawable.sym_def_app_icon,"chattyhive. ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."),System.currentTimeMillis());
                    Intent intent = new Intent(this,Home.class);
                    PendingIntent i= PendingIntent.getActivity(this, 0, intent, 0);
                    notification.setLatestEventInfo(this, "You have new messages!","There are ".concat(((Integer)this._pendingMsgs).toString()).concat(" new messages."), i);
                }
                _notificationManager.notify(0,notification);
            }
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
            Notification notification;
            PendingIntent i= PendingIntent.getActivity(this, 0, new Intent(this, Home.class), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                notification = new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.sym_def_app_icon)
                        .setContentTitle("chattyhive - No user data!")
                        .setContentText("Welcome to chattyhive!.")
                        .addAction(android.R.drawable.sym_def_app_icon,"Loggin",i)
                        .setSubText("There's no user data. Please, touch here to login.")
                        .setTicker("This is the ticker text")
                        .build();
            } else {
                notification = new Notification(android.R.drawable.sym_def_app_icon,"chattyhive - No user data!",System.currentTimeMillis());
                notification.setLatestEventInfo(this, "Welcome to chattyhive!","There's no user data. Please, touch here to login.", i);
            }
            _notificationManager.notify(0,notification);
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
