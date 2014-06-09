package com.chattyhive.chattyhive.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.chattyhive.backend.contentprovider.OSStorageProvider.MessageLocalStorageInterface;
import com.chattyhive.chattyhive.Util.ApplicationContextProvider;

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
    public void StoreMessage(String channel, String jsonMessage) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(channel,context.MODE_PRIVATE);
        int count = sharedPreferences.getAll().size();
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        String key = "message".concat(String.valueOf(count));
        while (sharedPreferences.contains(key)) {
            count++;
            key = "message".concat(String.valueOf(count));
        }
        sharedPreferencesEditor.putString(key,jsonMessage);
        sharedPreferencesEditor.apply();

        sharedPreferencesEditor = null;
        sharedPreferences = null;
        context = null;
    }

    @Override
    public String[] RecoverMessage(String channel) {
        String[] messages = null;

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(channel,context.MODE_PRIVATE);

        if (sharedPreferences.getAll().size() > 0)
            messages = sharedPreferences.getAll().values().toArray(new String[0]);

        sharedPreferences = null;
        context = null;

        return messages;
    }

    @Override
    public void ClearMessages(String channel) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(channel,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
        sharedPreferences = null;
        context = null;
    }
}
