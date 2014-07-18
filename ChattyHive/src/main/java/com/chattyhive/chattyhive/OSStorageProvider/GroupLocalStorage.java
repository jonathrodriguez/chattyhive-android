package com.chattyhive.chattyhive.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.backend.contentprovider.OSStorageProvider.GroupLocalStorageInterface;
import com.chattyhive.chattyhive.Util.ApplicationContextProvider;

/**
 * Created by Jonathan on 23/06/2014.
 */
public class GroupLocalStorage implements GroupLocalStorageInterface {
    private GroupLocalStorage() {}
    static GroupLocalStorage instance;

    public static GroupLocalStorage getGroupLocalStorage() {
        if (instance == null) { instance = new GroupLocalStorage(); }
        return instance;
    }

    private static String CHAT_PROFILES = "chChat";

    @Override
    public void StoreGroup(String CHANNEL_UNICODE, String jsonGroup) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHAT_PROFILES,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(CHANNEL_UNICODE)) {
            sharedPreferencesEditor.remove(CHANNEL_UNICODE);
        }
        sharedPreferencesEditor.putString(CHANNEL_UNICODE,jsonGroup);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String RecoverGroup(String CHANNEL_UNICODE) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHAT_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.contains(CHANNEL_UNICODE)) {
            return sharedPreferences.getString(CHANNEL_UNICODE,null);
        }
        return null;
    }


    @Override
    public String[] RecoverGroups() {
        String[] chats = null;
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHAT_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() > 0)
            chats = sharedPreferences.getAll().values().toArray(new String[0]);

        return chats;
    }

    @Override
    public void RemoveGroup(String CHANNEL_UNICODE) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHAT_PROFILES,context.MODE_PRIVATE);
        if ((sharedPreferences.getAll().size() > 0) && (sharedPreferences.contains(CHANNEL_UNICODE))) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.remove(CHANNEL_UNICODE);
            sharedPreferencesEditor.apply();
        }
    }

    @Override
    public void ClearGroups() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHAT_PROFILES,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
