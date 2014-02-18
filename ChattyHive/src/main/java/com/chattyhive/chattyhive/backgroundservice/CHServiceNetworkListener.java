package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jonathan on 4/02/14.
 */
public class CHServiceNetworkListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("CHServiceNetworkListener", "Network event..."); //DEBUG
        //context.startService(new Intent(context, CHService.class));
    }
}
