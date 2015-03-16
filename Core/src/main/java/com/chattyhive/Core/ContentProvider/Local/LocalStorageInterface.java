package com.chattyhive.Core.ContentProvider.local;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.formats.Format;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;

import java.util.Collection;

/**
 * Created by Jonathan on 30/09/2014.
 */
public interface LocalStorageInterface {
    public Boolean PreRunCommand(AvailableCommands command,EventHandler<CommandCallbackEventArgs> Callback, Object CallbackAdditionalData, Format... formats);
    public Boolean PostRunCommand(AvailableCommands command, Format... formats);
    public Boolean FormatsReceived(Collection<Format> receivedFormats);

    public java.io.InputStream getImage(String url);
}
