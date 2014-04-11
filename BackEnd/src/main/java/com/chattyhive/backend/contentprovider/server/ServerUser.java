package com.chattyhive.backend.contentprovider.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jonathan on 11/12/13.
 * This class defines a server user. A server user contains information to authenticate the user, it also contains
 * cookies provided by server to this user and the server connection status.
 */
public class ServerUser {
    // Login data
    private String _login;
    private String _password;

    private HashMap<String, HttpCookie> _cookies;
    private ServerStatus _status;

    /**
     * Public constructor.
     * @param login the user login. It can be a username or a email address.
     * @param password the user password to login.
     */
    public ServerUser(String login, String password) {
        this._login = login;
        this._password = password;
        this._cookies = new HashMap<String, HttpCookie>();
    }

    /**
     * Access to the user login.
     * @return a string containing the user login.
     */
    public String getLogin() {
        return this._login;
    }

    /**
     * Returns the user password.
     * @return a string containing the user password.
     */
    public String getPassword() {
        return this._password;
    }

    /**
     * Associates a cookie with this user.
     * @param cookie the HttpCookie to be associated.
     */
    public void setCookie (HttpCookie cookie) {
        this._cookies.put(cookie.getName(),cookie);
    }

    /**
     * Returns a cookies string with all cookies as it will be sent in a cookies http header.
     * @return a string representation of the cookies.
     */
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

    /**
     * Returns a cookie from the cookies list of this user, selecting it by name.
     * @param name the name of the cookie to retrieve.
     * @return the cookie retrieved.
     */
    public HttpCookie getCookie(String name) {
        HttpCookie cookie = null;

        if (this._cookies.containsKey(name))
            cookie = this._cookies.get(name);

        return cookie;
    }

    /**
     * Returns the server status of this user.
     * @return a value from the server status enum.
     */
    public ServerStatus getStatus() {
        return this._status;
    }

    /**
     * Sets the server status of this user.
     * @param status a value from the server status enum.
     */
    public void setStatus(ServerStatus status) {
        this._status = status;
    }

    /**
     * Converts the server user to its JSON representation.
     * This is used to perform login before advanced security implementation. The user name and
     * password is sent into body of login request.
     * @return
     */
    public JsonElement toJson() {
        JsonObject jsonMessage = new JsonObject();
        jsonMessage.addProperty("user",this._login);
        jsonMessage.addProperty("pass",this._password);
        return jsonMessage;
    }
}
