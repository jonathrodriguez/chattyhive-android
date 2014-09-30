package com.chattyhive.backend.contentprovider.OSStorageProvider;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.local.LocalCommand;
import com.chattyhive.backend.contentprovider.server.ServerCommand;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;

/**
 * Created by Jonathan on 30/09/2014.
 */
public interface LocalStorageInterface {
    public Boolean RunCommand(LocalCommand command,EventHandler<CommandCallbackEventArgs> Callback, Format... formats);
    public Boolean ReceivedData(ServerCommand command,CommandCallbackEventArgs commandCallbackEventArgs);
    public Boolean ReceivedData(FormatReceivedEventArgs formatEventArgs);
}
