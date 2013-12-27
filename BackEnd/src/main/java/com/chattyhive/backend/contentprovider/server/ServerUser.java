package com.chattyhive.backend.contentprovider.server;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jonathan on 11/12/13.
 */
public class ServerUser {
    // Login data
    private String _login;
    private String _password;

    private HashMap<String, HttpCookie> _cookies;
    private ServerStatus _status;

    public ServerUser(String login, String password) {
        this._login = login;
        this._password = password;
        this._cookies = new HashMap<String, HttpCookie>();
    }

    public String getLogin() {
        return this._login;
    }

    public String getPassword() {
        return this._password;
    }

    public void setCookie (HttpCookie cookie) {
        this._cookies.put(cookie.getName(),cookie);
    }

    public String getCookies() {
        String cookies = "";

        Iterator<HttpCookie> it = this._cookies.values().iterator();
        while (it.hasNext()) {
            HttpCookie cookie = it.next();
            cookies = cookies.concat(cookie.toString());

            if (it.hasNext()) cookies = cookies.concat("; ");
        }

        return cookies;
    }

    public HttpCookie getCookie(String name) {
        HttpCookie cookie = null;

        if (this._cookies.containsKey(name))
            cookie = this._cookies.get(name);

        return cookie;
    }

    public ServerStatus getStatus() {
        return this._status;
    }

    public void setStatus(ServerStatus status) {
        this._status = status;
    }
}
