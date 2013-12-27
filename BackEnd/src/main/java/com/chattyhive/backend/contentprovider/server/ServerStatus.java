package com.chattyhive.backend.contentprovider.server;

public enum ServerStatus {
    ERROR, //Error has occurred.
    RECEIVED, //Message received by server.
    LOGGED, //New session successfully created.
    EXPIRED, //Session has expired.
    DISCONNECTED //Session is not connected.
}