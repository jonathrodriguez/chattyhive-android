package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.chattyhive.framework.OSStorageProvider.ChatLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.CookieStore;
import com.chattyhive.chattyhive.framework.OSStorageProvider.HiveLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.LoginLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.MessageLocalStorage;
import com.chattyhive.chattyhive.framework.OSStorageProvider.UserLocalStorage;

/**
 * Created by Jonathan on 30/12/13.
 */
public class CHServiceLauncher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("CHServiceLauncher", "Starting CHService..."); //DEBUG
        if (StaticParameters.BackgroundService) {
            Object[] LocalStorage = {LoginLocalStorage.getLoginLocalStorage(), ChatLocalStorage.getGroupLocalStorage(), HiveLocalStorage.getHiveLocalStorage(), MessageLocalStorage.getMessageLocalStorage(), UserLocalStorage.getUserLocalStorage()};
            Controller.Initialize(new CookieStore(),LocalStorage);
            Controller.setNotificationShower(new CHNotificationShower());
            Controller.GetRunningController(com.chattyhive.chattyhive.framework.OSStorageProvider.LocalStorage.getLocalStorage());

            //TODO: Perform GCM connection check.

            // This will be removed when using GCM.
            context.startService(new Intent(context, CHService.class));
        }
    }
}
