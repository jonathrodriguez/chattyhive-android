package com.chattyhive.backend.server;

public enum ServerStatus {
    ERROR, //Error has occurred.
    RECEIVED, //Message received by server.
    LOGGED, //New session successfully created.
    EXPIRED //Session has expired.
}