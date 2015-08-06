package com.chattyhive.Core.ContentProvider.Server;

/**
 * Created by Jonathan on 11/02/2015.
 */
public interface IServerUser {
    public static final String userIDKey = "public_name";

    public String getLogin();
    public String getPassword();

    public String getAuthToken(String name);
    public String getAuthTokens();

    public void updateAuthToken(String name, String value);

    public SessionStatus getStatus();

    public void invalidateAuthToken(String name);
    public void invalidateAuthTokens();

    public Boolean removeUser();

    public void setPassword(String newPassword);

    public String getUserData(String key);
    public void setUserData(String key,String value);
    @Override
    public int hashCode();
}
