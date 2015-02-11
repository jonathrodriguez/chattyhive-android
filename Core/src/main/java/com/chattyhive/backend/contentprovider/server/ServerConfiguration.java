package com.chattyhive.backend.contentprovider.server;

/**
 * Created by Jonathan on 11/02/2015.
 */
public final class ServerConfiguration {
    private ServerConfiguration() {}

    public static final String sessionCookie = "";
    public static final String csrfTokenCookie = "";

    public static final String[] activeSessionCookies = { csrfTokenCookie, sessionCookie };
}
