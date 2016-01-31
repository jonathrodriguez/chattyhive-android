package com.chattyhive.chattyhive.backgroundservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.Controller;

/**
 * Created by Jonathan on 29/07/2014.
 * Remove this when notification system becomes available.
 */
public class CHAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("CHAlarmReceiver", "Alarm tick!.");
        DataProvider dataProvider = Controller.GetRunningController().getDataProvider();
        if (dataProvider != null) {
            Command command = new Command(AvailableCommands.ChatList);
            dataProvider.runCommand(command, CommandQueue.Priority.Low);
        }
    }
}
