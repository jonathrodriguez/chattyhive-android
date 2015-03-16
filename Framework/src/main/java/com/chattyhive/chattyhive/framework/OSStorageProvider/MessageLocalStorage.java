package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.backend.ContentProvider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 26/05/2014.
 */
public class MessageLocalStorage implements MessageLocalStorageInterface {
    private MessageLocalStorage() {}
    static MessageLocalStorage instance = null;

    public static MessageLocalStorage getMessageLocalStorage() {
        if (instance == null) { instance = new MessageLocalStorage(); }
        return instance;
    }

    @Override
    public void StoreMessage(String PusherChannel,String messageId, String jsonMessage) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(messageId))
            sharedPreferencesEditor.remove(messageId);
        sharedPreferencesEditor.putString(messageId,jsonMessage);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String RecoverMessage(String PusherChannel, String messageId) {
        String message = null;

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);

        if (sharedPreferences.contains(messageId))
            message = sharedPreferences.getString(messageId,null);

        return message;
    }

    @Override
    public String[] RecoverMessages(String PusherChannel) {
        String[] messages = null;

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > 0)
            messages = sharedPreferences.getAll().values().toArray(new String[0]);

        return messages;
    }

    @Override
    public void ClearMessages(String PusherChannel) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void RemoveMessage(String PusherChannel, String messageId) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > 0) {
            ArrayList<String> messagesList = new ArrayList<String>((Collection<String>)sharedPreferences.getAll().values());

            if (messagesList.contains(messageId)) {
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.clear();
                int i = 0;
                for (String message : messagesList) {
                    if (!message.equalsIgnoreCase(messageId)) {
                        sharedPreferencesEditor.putString(String.format("message%d",i),message);
                        i++;
                    }
                }
                sharedPreferencesEditor.apply();
            }
        }
    }

    @Override
    public void TrimStoredMessages(String PusherChannel,int numberOfMessages) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > numberOfMessages) {
            ArrayList<String> messagesList = new ArrayList<String>((Collection<String>)sharedPreferences.getAll().values());

            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.clear();

            for (int i = messagesList.size()-numberOfMessages, c = 0;i<messagesList.size();i++,c++) {
                sharedPreferencesEditor.putString(String.format("message%d",c),messagesList.get(i));
            }

            sharedPreferencesEditor.apply();
        }
    }
}
