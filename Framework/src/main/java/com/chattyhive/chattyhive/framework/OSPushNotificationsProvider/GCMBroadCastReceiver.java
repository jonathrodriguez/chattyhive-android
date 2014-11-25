package com.chattyhive.chattyhive.framework.OSPushNotificationsProvider;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Jonathan on 22/11/2014.
 */
public class GCMBroadCastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmProcessMessage will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),GCMProcessMessage.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
