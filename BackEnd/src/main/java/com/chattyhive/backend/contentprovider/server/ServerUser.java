package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.util.Cookie;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Jonathan on 11/12/13.
 */
public class ServerUser {
    // Login data
    private String _login;
    private String _password;

    private Map<String, Cookie> _cookies;
    private ServerStatus _status;

    public ServerUser(String login, String password) {
        this._login = login;
        this._password = password;
    }

    public String getLogin() {
        return this._login;
    }

    public String getPassword() {
        return this._password;
    }

    /*public String getStatus() {
        return this._status.toString();
    }*/

    public void setCookie (Cookie cookie) {
        this._cookies.put(cookie.getName(),cookie);
    }

    public String getCookies() {
        String cookies = "";

        Iterator<Cookie> it = this._cookies.values().iterator();
        while (it.hasNext()) {
            Cookie cookie = it.next();
            cookies = cookies.concat(cookie.getName()).concat("=").concat(cookie.getValue());

            if (it.hasNext()) cookies = cookies.concat("; ");
        }

        return cookies;
    }

    public Cookie getCookie(String name) {
        Cookie cookie = null;

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
