package com.chattyhive.chattyhive.backgroundservice;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.chattyhive.Core.Controller;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.pubsubservice.ConnectionState;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Events.FormatReceivedEventArgs;
import com.chattyhive.Core.Util.Events.PubSubConnectionEventArgs;
import com.chattyhive.chattyhive.Main;
import com.chattyhive.chattyhive.framework.OSStorageProvider.ChatLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.CookieStore;
import com.chattyhive.chattyhive.framework.OSStorageProvider.HiveLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.MessageLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.UserLocalStorage;
import com.chattyhive.chattyhive.R;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CHService extends Service {
    private Controller controller;
    private NotificationManager notificationManager;
    private int pendingMessages;
    private Boolean appOpen = false;
    private final IBinder binder = new Binder();

    public CHService() { }

    /*
     * Service start and initialization
     */
    @Override
    //Called by the system when the service is first created.
    public void onCreate() {
        ComponentName launcher = new ComponentName(this.getApplicationContext(), CHServiceLauncher.class);
        PackageManager pm = this.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(launcher, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pendingMessages = 0;
        handleConnectivity();
        Controller.AppBindingEvent.add(new EventHandler<EventArgs>(this,"onAppBinding",EventArgs.class));
        Controller.bindSvc(com.chattyhive.chattyhive.framework.OSStorageProvider.LocalStorage.getLocalStorage());
    }

    @Override
    //Called by the system every time a client explicitly starts the service by calling startService(Intent), providing the arguments it supplied and a unique integer token representing the start request.
    public int 	onStartCommand(Intent intent, int flags, int startId) {
        if (!(this.appOpen = Controller.isAppBounded()))
            checkConnected();
        else
            handleConnectivity();
        return START_STICKY;
    }

    public void captureController() {
        Object[] LocalStorage = {LoginLocalStorage.getLoginLocalStorage(), ChatLocalStorage.getGroupLocalStorage(), HiveLocalStorage.getHiveLocalStorage(), MessageLocalStorage.getMessageLocalStorage(), UserLocalStorage.getUserLocalStorage()};
        Controller.Initialize(new CookieStore(),LocalStorage);

        if ((this.controller == null) || (this.controller != Controller.GetRunningController(com.chattyhive.chattyhive.framework.OSStorageProvider.LocalStorage.getLocalStorage()))) {
            this.controller = Controller.GetRunningController();

            this.controller.getDataProvider().onMessageReceived.add(new EventHandler<FormatReceivedEventArgs>(this,"onChannelEvent",FormatReceivedEventArgs.class));
            this.controller.getDataProvider().PubSubConnectionStateChanged.add(new EventHandler<PubSubConnectionEventArgs>(this, "onConnectionEvent", PubSubConnectionEventArgs.class));
        }
    }

    public void onAppBinding(Object sender,EventArgs e) {
        this.appOpen = Controller.isAppBounded();
        if (this.appOpen) {
            this.notificationManager.cancelAll();
        } else {
            this.pendingMessages = 0;
            checkConnected();
        }
    }

    /*
     * Connection events handling
     */

    public void onConnectionEvent (Object sender, PubSubConnectionEventArgs args) {
        if (args.getChange().getCurrentState() == ConnectionState.DISCONNECTED) {
            checkConnected();
        }
    }

    private void checkConnected () {
        handleConnectivity();

        if (this.controller.getNetworkAvailable()) {
            if (!this.controller.isConnected()) {
                if ((!Controller.isAppBounded()) && (Controller.LoginLocalStorage.RecoverLoginPassword() == null)) { //If I can't login then exit service.
                    Context context = this.getApplicationContext();
                    context.stopService(new Intent(context,this.getClass()));
                } else if (Controller.LoginLocalStorage.RecoverLoginPassword() != null) {
                    this.controller.Connect();
                }
            }
            Log.w("CHService", "Alarm activated.");
            Intent alarmIntent = new Intent(this,CHAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + StaticParameters.IntervalToChatSync, StaticParameters.IntervalToChatSync, pendingIntent);
            } else {
                manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + StaticParameters.IntervalToChatSync, StaticParameters.IntervalToChatSync, pendingIntent);
            }
        } else {
            Log.w("CHService","Alarm deactivated. (Network unavailable)");
            Intent alarmIntent = new Intent(this, CHAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
            AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            manager.cancel(pendingIntent);
        }
    }

    public void handleConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        captureController();
        this.controller.setNetworkAvailable(((networkInfo != null) && (networkInfo.isConnected())));

        Context context = this.getApplicationContext();
        ComponentName networkListener = new ComponentName(context, CHServiceNetworkListener.class);
        PackageManager pm = context.getPackageManager();

        if (this.controller.getNetworkAvailable()) {
            pm.setComponentEnabledSetting(networkListener, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            pm.setComponentEnabledSetting(networkListener, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            context.stopService(new Intent(context,this.getClass()));
        }
    }

    /*
     * Channel events handling
     */

    public void onChannelEvent(Object sender, FormatReceivedEventArgs args) {
        // Do something with event
        if (this.appOpen) return;

        ArrayList<Format> formats = args.getReceivedFormats();

        Boolean messageReceived = false;

        for (Format format : formats) {
            if (format instanceof MESSAGE) {
                messageReceived = true;
                this.pendingMessages++;
            }
        }

        if (messageReceived) {
            this.notificationManager.cancelAll();
            PendingIntent i= PendingIntent.getActivity(this, 0, new Intent(this, Main.class), 0);
            CHNotificationBuilder chNotificationBuilder = new CHNotificationBuilder(this.getApplicationContext());
            chNotificationBuilder.setTickerText(String.format(this.getString(R.string.buzz_in_hive_Ticker), "@".concat("Somebody"), ":".concat("ChattyHive")));
            chNotificationBuilder.setTitleText(String.format(this.getString(R.string.buzz_in_hive_TITLE), ":".concat("ChattyHive")));
            chNotificationBuilder.setMainText(String.format(this.getString(R.string.buzz_in_hive_mainText), this.pendingMessages));

           /* Message[] messages = this.controller.getMessages(args.getChannelName()).toArray(new Message[0]);
            ArrayList<String> subText = new ArrayList<String>();
            for (int idx = (messages.length-this.pendingMessages); idx < messages.length; idx++) {
                subText.add(String.format(this.getString(R.string.buzz_in_hive_subText),"@".concat(messages[idx].getUser().getPublicName()),messages[idx].getMessageContent().getContent()));
            }

            chNotificationBuilder.setSubText(subText);*/
            chNotificationBuilder.setMainAction(i);
            notificationManager.notify(0, chNotificationBuilder.Build());
        }
    }

    /*
     * General service methods
     */

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    //Called by the system to notify a Service that it is no longer used and is being removed.
    public void onDestroy() {
        /*First remove alarm*/
        Log.w("CHService","Alarm deactivated. (Service closing)");
        Intent alarmIntent = new Intent(this, CHAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        /*The remove controller*/

        Controller.unbindSvc();

        if (!Controller.isAppBounded())
            Controller.DisposeRunningController();

        this.controller = null;
    }

    @Override
    //This is called when the overall system is running low on memory, and actively running processes should trim their memory usage.
    public void 	onLowMemory() {}

    @Override
    //Called when new clients have connected to the service, after it had previously been notified that all had disconnected in its onUnbind(Intent).
    public void 	onRebind(Intent intent) {}

    @Override
    //This is called if the service is currently running and the user has removed a task that comes from the service's application.
    public void onTaskRemoved(Intent rootIntent) {}

    @Override
    //Called when the operating system has determined that it is a good time for a process to trim unneeded memory from its process.
    public void onTrimMemory(int level) {}

    @Override
    //Called when all clients have disconnected from a particular interface published by the service.
    public boolean 	onUnbind(Intent intent) {
        return true;
    }

    @Override
    //Print the Service's state into the given stream.
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {}

}
