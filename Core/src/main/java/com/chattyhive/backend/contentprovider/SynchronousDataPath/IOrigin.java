package com.chattyhive.backend.ContentProvider.SynchronousDataPath;

import com.chattyhive.backend.Util.CallbackDelegate;

/**
 * Created by Jonathan on 09/03/2015.
 */
public interface IOrigin {
    public void ProcessCommand(Command command,CallbackDelegate Callback,Object... callbackParameters);
}
