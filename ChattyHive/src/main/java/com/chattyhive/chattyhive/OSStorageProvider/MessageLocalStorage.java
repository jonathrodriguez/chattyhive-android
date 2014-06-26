package com.chattyhive.chattyhive.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.chattyhive.Util.ApplicationContextProvider;

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
    public void StoreMessage(String PusherChannel, String jsonMessage) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);
        int count = sharedPreferences.getAll().size();
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        String key = String.format("message%d",count);
        while (sharedPreferences.contains(key)) {
            count++;
            key = String.format("message%d",count);
        }
        sharedPreferencesEditor.putString(key,jsonMessage);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String[] RecoverMessage(String PusherChannel) {
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
    public void RemoveMessage(String PusherChannel, String jsonMessage) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PusherChannel,context.MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > 0) {
            ArrayList<String> messagesList = new ArrayList<String>((Collection<String>)sharedPreferences.getAll().values());

            if (messagesList.contains(jsonMessage)) {
                SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
                sharedPreferencesEditor.clear();
                int i = 0;
                for (String message : messagesList) {
                    if (!message.equalsIgnoreCase(jsonMessage)) {
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
