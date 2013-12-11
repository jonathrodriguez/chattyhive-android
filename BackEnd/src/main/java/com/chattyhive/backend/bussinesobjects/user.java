package com.chattyhive.backend.bussinesobjects;

/**
 * Created by Jonathan on 11/12/13.
 */
public class User {
    String _username;

    public User (String username) {
        this._username = username;
    }

    public String getUsername() {
        return this._username;
    }

    public void setUsername(String username) {
        this._username = username;
    }
}
