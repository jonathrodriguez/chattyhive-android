package com.chattyhive.chattyhive.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.chattyhive.Util.ApplicationContextProvider;

import java.util.AbstractMap;

/**
 * Created by Jonathan on 25/05/2014.
 */
public class LoginLocalStorage implements LoginLocalStorageInterface {
    private LoginLocalStorage() {}
    static LoginLocalStorage instance = null;

    public static LoginLocalStorage getLoginLocalStorage() {
        if (instance == null) { instance = new LoginLocalStorage(); }
        return instance;
    }

    public void StoreLoginPassword(String username, String password) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("chattyhive",context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains("user")) sharedPreferencesEditor.remove("user");
        if (sharedPreferences.contains("pass")) sharedPreferencesEditor.remove("pass");
        sharedPreferencesEditor.putString("user",username);
        sharedPreferencesEditor.putString("pass",password);
        sharedPreferencesEditor.commit();
        sharedPreferencesEditor.apply();

        sharedPreferencesEditor = null;
        sharedPreferences = null;
        context = null;
    }

    public AbstractMap.SimpleEntry<String,String> RecoverLoginPassword() {
        String username = null;
        String password = null;

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("chattyhive",context.MODE_PRIVATE);

        if (sharedPreferences.contains("user")) username = sharedPreferences.getString("user",null);
        if (sharedPreferences.contains("pass")) password = sharedPreferences.getString("pass",null);

        sharedPreferences = null;
        context = null;

        return new AbstractMap.SimpleEntry<String,String>(username,password);
    }
}