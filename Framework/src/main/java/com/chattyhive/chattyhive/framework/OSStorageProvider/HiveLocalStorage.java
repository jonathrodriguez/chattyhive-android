package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.OLD.HiveLocalStorageInterface;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

/**
 * Created by Jonathan on 01/07/2014.
 */
public class HiveLocalStorage implements HiveLocalStorageInterface {
    private HiveLocalStorage() {}
    static HiveLocalStorage instance;

    public static HiveLocalStorage getHiveLocalStorage() {
        if (instance == null) { instance = new HiveLocalStorage(); }
        return instance;
    }

    private static String HIVE_PROFILES = "chHive";

    @Override
    public void StoreHive(String NAME_URL, String jsonHive) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(HIVE_PROFILES,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(NAME_URL)) {
            sharedPreferencesEditor.remove(NAME_URL);
        }
        sharedPreferencesEditor.putString(NAME_URL,jsonHive);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String RecoverHive(String NAME_URL) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(HIVE_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.contains(NAME_URL)) {
            return sharedPreferences.getString(NAME_URL,null);
        }
        return null;
    }

    @Override
    public String[] RecoverHives() {
        String[] hives = null;
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(HIVE_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() > 0)
            hives = sharedPreferences.getAll().values().toArray(new String[0]);

        return hives;
    }

    @Override
    public void ClearHives() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(HIVE_PROFILES,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void RemoveHive(String NAME_URL) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(HIVE_PROFILES,context.MODE_PRIVATE);
        if ((sharedPreferences.getAll().size() > 0) && (sharedPreferences.contains(NAME_URL))) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.remove(NAME_URL);
            sharedPreferencesEditor.apply();
        }
    }
}
