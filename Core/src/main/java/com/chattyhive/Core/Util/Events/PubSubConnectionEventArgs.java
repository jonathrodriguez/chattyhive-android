package com.chattyhive.Core.Util.Events;

import com.chattyhive.Core.ContentProvider.pubsubservice.ConnectionStateChange;

/**
 * Created by Jonathan on 30/12/13.
 * This class contains arguments for the Channel Event. The argument is the connection state change
 * which happened.
 */
public class PubSubConnectionEventArgs extends EventArgs {
    private ConnectionStateChange _change;

    public PubSubConnectionEventArgs() { super(); }
    public PubSubConnectionEventArgs(ConnectionStateChange change) {
        super();
        this._change = change;
    }

    public void setChange(ConnectionStateChange change) { this._change = change; }

    public ConnectionStateChange getChange () { return this._change; }
}
