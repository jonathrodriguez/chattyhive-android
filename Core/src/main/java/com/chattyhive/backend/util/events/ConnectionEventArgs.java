package com.chattyhive.backend.util.events;

/*
 * Created by Jonathan on 11/04/2014.
 */
public class ConnectionEventArgs extends EventArgs {
    private Boolean connected;

    public ConnectionEventArgs() { super(); }
    public ConnectionEventArgs(Boolean connected) {
        this();
        this.connected = connected;
    }

    public Boolean getConnected() { return this.connected; }
}
