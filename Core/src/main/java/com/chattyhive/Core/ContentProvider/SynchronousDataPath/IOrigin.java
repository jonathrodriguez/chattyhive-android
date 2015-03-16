package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.Util.CallbackDelegate;

/**
 * Created by Jonathan on 09/03/2015.
 */
public interface IOrigin {
    public void ProcessCommand(Command command,CallbackDelegate Callback,Object... callbackParameters);
}
