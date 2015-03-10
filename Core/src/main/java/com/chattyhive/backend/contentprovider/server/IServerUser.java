package com.chattyhive.backend.ContentProvider.server;

/**
 * Created by Jonathan on 11/02/2015.
 */
public interface IServerUser {
    public String getLogin();
    public String getPassword();

    public String getAuthToken(String name);
    public String getAuthTokens();

    public SessionStatus getStatus();

    public void invalidateAuthToken(String name);
    public void invalidateAuthTokens();

    public Boolean removeUser();

    public void setPassword(String newPassword);
}
