package com.chattyhive.backend.contentprovider.OSStorageProvider;

import java.util.AbstractMap;

/**
 * Created by Jonathan on 26/05/2014.
 */
public interface LoginLocalStorageInterface {
    public void StoreLoginPassword(String username, String password);
    public AbstractMap.SimpleEntry<String,String> RecoverLoginPassword();
    public void ClearStoredLogin();
}
