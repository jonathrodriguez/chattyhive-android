package com.chattyhive.backend.contentprovider.OSStorageProvider;

/**
 * Created by Jonathan on 23/06/2014.
 */
public interface UserLocalStorageInterface {
    //TODO: Change json parameters into 'USER' format class parameters

    //Local user management
    public void StoreLocalUserProfile(String jsonUser);
    public String RecoverLocalUserProfile();
    public void ClearLocalUserProfile();

    //Other users management
    public void StoreCompleteUserProfile(String userID, String jsonCompleteUser);
    public String RecoverCompleteUserProfile(String userID);
    public String[] RecoverAllCompleteUserProfiles();
    public void RemoveCompleteUserProfile(String userID);
    public void ClearCompleteUserProfiles();
}
