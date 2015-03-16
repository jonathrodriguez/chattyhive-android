package com.chattyhive.Core.ContentProvider.OSStorageProvider;

/**
 * Created by Jonathan on 01/07/2014.
 */
public interface HiveLocalStorageInterface {
    //TODO: Change json parameters into CHAT_INFO_RESPONSE format class parameters
    public void StoreHive(String NAME_URL,String jsonHive);
    public String RecoverHive(String NAME_URL);
    public String[] RecoverHives();
    public void ClearHives();
    public void RemoveHive(String NAME_URL);
}
