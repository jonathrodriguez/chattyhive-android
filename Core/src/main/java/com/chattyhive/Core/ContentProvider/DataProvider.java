package com.chattyhive.Core.ContentProvider;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalStorageInterface;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandQueue;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.SynchronousProvider;

/**
 * Created by Jonathan on 11/12/13.
 * This class is intended to provide a generic interface to access data independently
 * from where data comes from. Possible data origins are local, server and pusher.
 */
public class DataProvider {

    SynchronousProvider synchronousDataPath;
    LocalStorageInterface settingsStorage;

    public DataProvider(LocalStorageInterface settingsStorage) {
        this.settingsStorage = settingsStorage;
        this.synchronousDataPath = new SynchronousProvider(this.settingsStorage);
    }

    public void runCommand(Command command,CommandQueue.Priority priority) {
        this.synchronousDataPath.runCommand(command,priority);
    }

    @Override
    protected void finalize() throws Throwable {
        this.synchronousDataPath.stopProcess();
        this.synchronousDataPath = null;
        this.settingsStorage = null;
        super.finalize();
    }
}

