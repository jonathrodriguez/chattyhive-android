package com.chattyhive.backend.ContentProvider.OSStorageProvider;

/**
 * Created by Jonathan on 23/06/2014.
 */
public interface ChatLocalStorageInterface {
//TODO: Change json parameters into CHAT_INFO_RESPONSE format class parameters
    public void StoreGroup(String CHANNEL_UNICODE,String jsonGroup);
    public String RecoverGroup(String CHANNEL_UNICODE);
    public String[] RecoverGroups();
    public void ClearGroups();
    public void RemoveGroup(String CHANNEL_UNICODE);
}
