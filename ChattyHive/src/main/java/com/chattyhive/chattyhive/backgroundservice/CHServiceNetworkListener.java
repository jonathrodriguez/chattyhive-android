package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.chattyhive.backend.StaticParameters;

/**
 * Created by Jonathan on 4/02/14.
 */
public class CHServiceNetworkListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.w("CHServiceNetworkListener", "Network event... "); //DEBUG
        if (StaticParameters.BackgroundService)
            context.startService(new Intent(context, CHService.class));
    }
}
