package com.chattyhive.backend.ContentProvider.pubsubservice;


/**
 * Created by Jonathan on 14/11/13.
 * Enumeration which represents the pusher connection state. It has the same values as the corresponding
 * enumeration provided with pusher library.
 */
public enum ConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    DISCONNECTED,
    ALL
}
