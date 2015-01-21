package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;

import java.util.AbstractMap;

/**
 * Created by Jonathan on 25/05/2014.
 */
public class LoginLocalStorage implements LoginLocalStorageInterface {
    private LoginLocalStorage() {}
    static LoginLocalStorage instance;

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
        sharedPreferencesEditor.apply();
    }

    public AbstractMap.SimpleEntry<String,String> RecoverLoginPassword() {
        String username = null;
        String password = null;

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("chattyhive",context.MODE_PRIVATE);

        if (sharedPreferences.contains("user")) username = sharedPreferences.getString("user",null);
        if (sharedPreferences.contains("pass")) password = sharedPreferences.getString("pass",null);

        if ((username != null) && (password != null))
            return new AbstractMap.SimpleEntry<String,String>(username,password);

        return null;
    }

    @Override
    public void ClearStoredLogin() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("chattyhive",context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (sharedPreferences.contains("user")) sharedPreferencesEditor.remove("user");
        if (sharedPreferences.contains("pass")) sharedPreferencesEditor.remove("pass");
        sharedPreferencesEditor.apply();
    }
}
