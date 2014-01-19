package com.chattyhive.chattyhive.backgroundservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.chattyhive.chattyhive.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonathan on 18/01/14.
 * This class is a builder for notifications. The build method returns an android notification
 * with all of it's parameters set according to the builder.
 */
public class CHNotificationBuilder {
    private Context context;
    private String tickerText;
    private String titleText;
    private String mainText;
    private ArrayList<String> subText;
    private PendingIntent mainAction;
    private HashMap<String,PendingIntent> actions;
    private PendingIntent deleteAction;

    public void setTickerText (String value)  { this.tickerText = value; }
    public String getTickerText () { return this.tickerText; }

    public void setTitleText (String value) { this.titleText = value; }
    public String getTitleText () { return this.titleText; }

    public void setMainText (String value) { this.mainText = value; }
    public String getMainText () { return this.mainText; }

    public void addSubTextLine (String value) { this.subText.add(value); }
    public String getSubTextLine (int index) { return this.subText.get(index); }
    public String remSubTextLine (int index) { return this.subText.remove(index); }
    public void setSubText (ArrayList<String> value) { this.subText = value; }
    public ArrayList<String> getSubText () { return this.subText; }

    public void setMainAction (PendingIntent value) { this.mainAction = value; }
    public PendingIntent getMainAction () { return this.mainAction; }

    public void addAction (String actionText, PendingIntent actionIntent) { this.actions.put(actionText,actionIntent); }
    public PendingIntent getAction (String actionText) { return this.actions.get(actionText); }
    public String getAction (int index) { return ((String)(this.actions.keySet().toArray()[index])); }
    public void remAction (String actionText) { this.actions.remove(actionText); }

    public void setActions (HashMap<String, PendingIntent> value) { this.actions = value; }
    public HashMap<String, PendingIntent> getActions () { return this.actions; }

    public void setDeleteAction (PendingIntent value) { this.deleteAction = value; }
    public PendingIntent getDeleteAction () { return this.deleteAction; }

    public CHNotificationBuilder (Context context) {
        this.context = context;
        this.actions = new HashMap<String, PendingIntent>();
    }

    public Notification Build() {
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Bitmap largeImage = BitmapFactory.decodeResource(this.context.getResources(), R.drawable.ic_launcher);
            if ((this.subText == null) || (this.subText.isEmpty())) {
                Notification.Builder notificationBuilder = new Notification.Builder(this.context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(this.titleText)
                        .setContentText(this.mainText)
                        .setContentIntent(this.mainAction)
                        .setTicker(this.tickerText);
                if (largeImage != null) {
                    notificationBuilder.setLargeIcon(largeImage);
                }
                if (this.deleteAction != null) {
                    notificationBuilder = notificationBuilder.setDeleteIntent(this.deleteAction);
                }
                if ((this.actions != null) && (!this.actions.isEmpty())) {
                    for (Map.Entry<String,PendingIntent> entry : this.actions.entrySet()) {
                        notificationBuilder = notificationBuilder.addAction(android.R.drawable.sym_def_app_icon,entry.getKey(),entry.getValue());
                    }
                }
                notification = notificationBuilder.build();
            } else {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this.context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(this.mainAction)
                        .setTicker(this.tickerText);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(this.titleText);
                inboxStyle.setSummaryText(this.mainText);
                for (String line : this.subText) {
                    inboxStyle.addLine(Html.fromHtml(line));
                }
                notificationBuilder.setStyle(inboxStyle);
                notificationBuilder.setNumber(this.subText.size());

                if (largeImage != null) {
                    notificationBuilder.setLargeIcon(largeImage);
                }
                if (this.deleteAction != null) {
                    notificationBuilder = notificationBuilder.setDeleteIntent(this.deleteAction);
                }
                if ((this.actions != null) && (!this.actions.isEmpty())) {
                    for (Map.Entry<String,PendingIntent> entry : this.actions.entrySet()) {
                        notificationBuilder = notificationBuilder.addAction(android.R.drawable.sym_def_app_icon,entry.getKey(),entry.getValue());
                    }
                }
                notification = notificationBuilder.build();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Notification.Builder notificationBuilder = new Notification.Builder(this.context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(this.titleText)
                    .setContentText(this.mainText)
                    .setContentIntent(this.mainAction)
                    .setTicker(this.tickerText);

            notification = notificationBuilder.getNotification();
            notification.largeIcon = (((BitmapDrawable)this.context.getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
            if ((this.subText != null) && (!this.subText.isEmpty())) {
                notification.number = this.subText.size();
            }
        } else {
            notification = new Notification(R.drawable.ic_launcher,this.tickerText,System.currentTimeMillis());
            notification.setLatestEventInfo(this.context,this.titleText,this.mainText,this.mainAction);
            if ((this.subText != null) && (!this.subText.isEmpty())) {
                notification.number = this.subText.size();
            }
            /**
             * There is no Large Icon before API 11, only the Small Icon is available.
             */
            //notification.largeIcon = BitmapFactory.decodeResource(this.context.getResources(),R.drawable.ic_launcher);
        }

        return notification;
    }
}
