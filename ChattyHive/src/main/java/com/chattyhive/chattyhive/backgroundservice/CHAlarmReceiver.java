package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.server.ServerCommand;

/**
 * Created by Jonathan on 29/07/2014.
 * Remove this when notification system becomes available.
 */
public class CHAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("CHAlarmReceiver", "Alarm tick!.");
        DataProvider dataProvider = DataProvider.GetDataProvider();
        if (dataProvider != null) {
            dataProvider.InvokeServerCommand(AvailableCommands.ChatList, (Format)null);
        }
    }
}
