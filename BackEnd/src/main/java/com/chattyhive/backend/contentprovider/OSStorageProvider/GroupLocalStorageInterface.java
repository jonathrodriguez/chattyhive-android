package com.chattyhive.backend.contentprovider.OSStorageProvider;

/**
 * Created by Jonathan on 23/06/2014.
 */
public interface GroupLocalStorageInterface {
//TODO: Change json parameters into CHAT_INFO_RESPONSE format class parameters
    public void StoreGroup(String CHANNEL_UNICODE,String jsonGroup);
    public String RecoverGroup(String CHANNEL_UNICODE);
    public String[] RecoverGroups();
    public void ClearGroups();
    public void RemoveGroup(String CHANNEL_UNICODE);
}
