package com.chattyhive.backend.contentprovider.OSStorageProvider;

/**
 * Created by Jonathan on 26/05/2014.
 */
public interface MessageLocalStorageInterface {
    public void StoreMessage(String channel,String jsonMessage);
    public String[] RecoverMessage(String channel);
}
