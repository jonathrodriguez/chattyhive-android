package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.Util.CallbackDelegate;

/**
 * Created by Jonathan on 09/03/2015.
 */
public interface IOrigin {
    void ProcessCommand(Command command,
                        CallbackDelegate<ProcessorCallbackArgs> Callback,
                        ProcessorCallbackArgs callbackParameters);
}
