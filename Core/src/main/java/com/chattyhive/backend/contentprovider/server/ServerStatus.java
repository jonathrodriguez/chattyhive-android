package com.chattyhive.backend.ContentProvider.server;

/**
 * Enumerator to define the server status.
 */
public enum ServerStatus {
    OK,  //Transaction OK
    ERROR, //Error has occurred.
    RECEIVED, //Message received by server.
    LOGGED, //New session successfully created.
    EXPIRED, //Session has expired.
    DISCONNECTED //Session is not connected.
}