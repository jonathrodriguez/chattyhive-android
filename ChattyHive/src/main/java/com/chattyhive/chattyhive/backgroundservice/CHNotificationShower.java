package com.chattyhive.chattyhive.backgroundservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.chattyhive.backend.businessobjects.Notifications.INotificationShower;
import com.chattyhive.backend.businessobjects.Notifications.Notification;
import com.chattyhive.chattyhive.Main;
import com.chattyhive.chattyhive.R;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

/**
 * Created by Jonathan on 11/12/2014.
 */
public class CHNotificationShower implements INotificationShower {

    private Context context;
    private NotificationManager notificationManager;

    public CHNotificationShower () {
        this.context = ApplicationContextProvider.getContext();
        this.notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void ShowNotification(Notification notification) {
        this.notificationManager.cancelAll();
        PendingIntent i= PendingIntent.getActivity(this.context, 0, new Intent(this.context, Main.class), 0);
        CHNotificationBuilder chNotificationBuilder = new CHNotificationBuilder(this.context);
        chNotificationBuilder.setTickerText(String.format(this.context.getString(R.string.new_messages_ticker)));
        chNotificationBuilder.setTitleText(String.format(this.context.getString(R.string.new_messages_title)));
        chNotificationBuilder.setMainText(String.format(this.context.getString(R.string.new_messages_mainText), notification.getMessageCount()));

        chNotificationBuilder.setMainAction(i);
        notificationManager.notify(0, chNotificationBuilder.Build());
    }

    @Override
    public void ClearNotifications() {
        this.notificationManager.cancelAll();
    }


}
