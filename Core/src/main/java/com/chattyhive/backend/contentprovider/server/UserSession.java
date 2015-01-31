package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.contentprovider.OSStorageProvider.LoginLocalStorageInterface;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.google.gson.JsonElement;

import java.util.AbstractMap;
import java.util.HashMap;

/**
 * Created by Jonathan on 11/12/13.
 * This class defines a server user. A server user contains information to authenticate the user, it also contains
 * cookies provided by server to this user and the server connection status.
 */
public class UserSession {
    // Login data
    private String login;
    private String password;

    // Session data
    private HashMap<String, String> authTokens;
    private SessionStatus status;

    /**
     * Public constructor.
     * @param login the user login. It can be a username or a email address.
     * @param password the user password to login.
     */
    public UserSession(String login, String password) {
        this.login = (login != null)?login:"";
        this.password = (password != null)?password:"";
        this.authTokens = new HashMap<String, String>();
        this.status = SessionStatus.DISCONNECTED;
    }

    public UserSession(String login, String password, HashMap<String,String> authTokens) {
        this.login = (login != null)?login:"";
        this.password = (password != null)?password:"";
        this.authTokens = new HashMap<String, String>(authTokens);
        this.status = SessionStatus.CONNECTED;
    }

    /**
     * Access to the user login.
     * @return a string containing the user login.
     */
    public String getLogin() {
        return this.login;
    }

    /**
     * Returns the user password.
     * @return a string containing the user password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Associates a cookie with this user.
     * @param cookie the HttpCookie to be associated.
     */
/*    public void setCookie (HttpCookie cookie) {
        this.cookies.put(cookie.getName(),cookie);
    }*/

    /**
     * Returns a cookies string with all cookies as it will be sent in a cookies http header.
     * @return a string representation of the cookies.
     */
/*    public String getCookies() {
        String cookies = "";

        Iterator<HttpCookie> it = this.cookies.values().iterator();
        while (it.hasNext()) {
            HttpCookie cookie = it.next();
            cookies = cookies.concat(cookie.toString());

            if (it.hasNext()) cookies = cookies.concat("; ");
        }

        return cookies;
    }*/

    /**
     * Returns a cookie from the cookies list of this user, selecting it by name.
     * @param name the name of the cookie to retrieve.
     * @return the cookie retrieved.
     */
/*    public HttpCookie getCookie(String name) {
        HttpCookie cookie = null;

        if (this.cookies.containsKey(name))
            cookie = this.cookies.get(name);

        return cookie;
    }*/

    /**
     * Returns the server status of this user.
     * @return a value from the server status enum.
     */
    public SessionStatus getStatus() {
        return this.status;
    }

    /**
     * Sets the server status of this user.
     * @param status a value from the server status enum.
     */
    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    /**
     * Converts the server user to its JSON representation.
     * This is used to perform login before advanced security implementation. The user name and
     * password is sent into body of login request.
     * @return
     */
    public JsonElement toJson() {
        LOGIN loginFormat = new LOGIN();
        loginFormat.USER = this.login;
        loginFormat.PASS = this.password;
        return loginFormat.toJSON();
    }
}
