package com.chattyhive.Core.ContentProvider.OSStorageProvider.OLD;

/**
 * Created by Jonathan on 26/05/2014.
 */
public interface MessageLocalStorageInterface {
    public void StoreMessage(String PusherChannel,String messageId,String jsonMessage);
    public String[] RecoverMessages(String PusherChannel);
    public String RecoverMessage(String PusherChannel,String messageId);
    public void RemoveMessage(String PusherChannel,String messageId);
    public void ClearMessages(String PusherChannel);
    public void TrimStoredMessages(String PusherChannel,int numberOfMessages);
}
