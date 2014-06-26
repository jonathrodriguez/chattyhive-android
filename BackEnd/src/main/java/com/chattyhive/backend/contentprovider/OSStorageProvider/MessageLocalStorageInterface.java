package com.chattyhive.backend.contentprovider.OSStorageProvider;

/**
 * Created by Jonathan on 26/05/2014.
 */
public interface MessageLocalStorageInterface {
    //TODO: Change json parameters into MESSAGE_RESPONSE format class parameters
    public void StoreMessage(String PusherChannel,String jsonMessage);
    public String[] RecoverMessage(String PusherChannel);
    public void ClearMessages(String PusherChannel);
    public void RemoveMessage(String PusherChannel,String jsonMessage);
    public void TrimStoredMessages(String PusherChannel,int numberOfMessages);
}
