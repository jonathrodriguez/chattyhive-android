package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.OLD.UserLocalStorageInterface;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

/**
 * Created by Jonathan on 23/06/2014.
 */
public class UserLocalStorage implements UserLocalStorageInterface {
    private UserLocalStorage() {}
    static UserLocalStorage instance = null;

    public static UserLocalStorage getUserLocalStorage() {
        if (instance == null) { instance = new UserLocalStorage(); }
        return instance;
    }

    private static String OTHER_USER_PROFILES = "chProfiles";
    private static String LOCAL_USER_PROFILE = "chUser";

    @Override
    public void StoreLocalUserProfile(String jsonUser) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCAL_USER_PROFILE,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains("localUser")) {
            sharedPreferencesEditor.remove("localUser");
        }
        sharedPreferencesEditor.putString("localUser",jsonUser);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String RecoverLocalUserProfile() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCAL_USER_PROFILE,context.MODE_PRIVATE);
        if (sharedPreferences.contains("localUser"))
            return sharedPreferences.getString("localUser",null);

        return null;
    }

    @Override
    public void ClearLocalUserProfile() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOCAL_USER_PROFILE,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }



    @Override
    public void StoreCompleteUserProfile(String userID, String jsonCompleteUser) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTHER_USER_PROFILES,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains(userID)) {
            sharedPreferencesEditor.remove(userID);
        }
        sharedPreferencesEditor.putString(userID,jsonCompleteUser);
        sharedPreferencesEditor.apply();
    }

    @Override
    public String RecoverCompleteUserProfile(String userID) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTHER_USER_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.contains(userID)) {
            return sharedPreferences.getString(userID,null);
        }
        return null;
    }

    @Override
    public String[] RecoverAllCompleteUserProfiles() {
        String[] profiles = null;
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTHER_USER_PROFILES,context.MODE_PRIVATE);
        if (sharedPreferences.getAll().size() > 0)
            profiles = sharedPreferences.getAll().values().toArray(new String[0]);

        return profiles;
    }

    @Override
    public void RemoveCompleteUserProfile(String userID) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTHER_USER_PROFILES,context.MODE_PRIVATE);
        if ((sharedPreferences.getAll().size() > 0) && (sharedPreferences.contains(userID))) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.remove(userID);
            sharedPreferencesEditor.apply();
        }
    }

    @Override
    public void ClearCompleteUserProfiles() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(OTHER_USER_PROFILES,context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }
}
