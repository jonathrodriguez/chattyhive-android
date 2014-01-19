package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jonathan on 30/12/13.
 */
public class CHServiceLauncher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("CHServiceLauncher", "Starting CHService..."); //DEBUG
        context.startService(new Intent(context, CHService.class));
    }
}
