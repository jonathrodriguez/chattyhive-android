package com.chattyhive.backend.contentprovider.OSStorageProvider;

/**
 * Created by Jonathan on 26/05/2014.
 */
public interface MessageLocalStorageInterface { //TODO: Change json parameters into Message class parameters
    public void StoreMessage(String channel,String jsonMessage);
    public String[] RecoverMessage(String channel);
    public void ClearMessages(String channel);
    public void RemoveMessage(String channel,String jsonMessage);
    public void TrimStoredMessages(String channel,int numberOfMessages);
}
