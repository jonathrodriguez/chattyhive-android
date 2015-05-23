package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalStorageInterface;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

/**
 * Created by Jonathan on 23/05/2015.
 */
public class LocalSettings implements LocalStorageInterface{
    private LocalSettings() {}
    static LocalSettings instance = null;

    public static LocalSettings getLocalSettings() {
        if (instance == null) { instance = new LocalSettings(); }
        return instance;
    }

    private static String getLocalUserSettingsStorage() {
        String uName = "";
        //TODO: Find a way to get the user name.
        return "ch"+uName+"Settings";
    }

    private static String GlobalSettings = "chSettings";

    @Override
    public String getData(StorageType storageType, String name) {
        Context context = ApplicationContextProvider.getContext();

        String store = GlobalSettings;
        switch (storageType) {
            case AccountSettings:
                store = getLocalUserSettingsStorage();
                break;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(store,Context.MODE_PRIVATE);
        if (sharedPreferences.contains(name))
            return sharedPreferences.getString(name,null);

        return null;
    }

    @Override
    public void setData(StorageType storageType, String name, String value) {
        Context context = ApplicationContextProvider.getContext();

        String store = GlobalSettings;
        switch (storageType) {
            case AccountSettings:
                store = getLocalUserSettingsStorage();
                break;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(store,Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(name)) {
            sharedPreferencesEditor.remove(name);
        }
        sharedPreferencesEditor.putString(name,value);
        sharedPreferencesEditor.apply();
    }
}
