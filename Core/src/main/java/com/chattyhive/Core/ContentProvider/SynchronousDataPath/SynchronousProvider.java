package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalStorageInterface;
import com.chattyhive.Core.ContentProvider.Server.RemoteServer;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;

/**
 * Created by Jonathan on 22/05/2015.
 */
public class SynchronousProvider {
    CommandQueue commandQueue;
    Thread commandExecutorThread;
    LocalStorageInterface settingsStorage;

    public Event<EventArgs> onCSRFTokenChanged;

    public SynchronousProvider(LocalStorageInterface settingsStorage) {
        this.settingsStorage = settingsStorage;

        this.onCSRFTokenChanged = new Event<EventArgs>();
        this.commandQueue = new CommandQueue();

        RemoteServer remoteServer = new RemoteServer(StaticParameters.DefaultServerAppProtocol,StaticParameters.DefaultServerAppName+"."+StaticParameters.DefaultServerHost,settingsStorage);

        CommandExecutor commandExecutor = new CommandExecutor(this.commandQueue,null,null,remoteServer,null);
        commandExecutorThread = new Thread(commandExecutor);
        commandExecutorThread.start();
    }

    public void runCommand(Command command, CommandQueue.Priority priority) {
        try {
            this.commandQueue.put(command, priority);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    public void stopProcess() {
        this.commandExecutorThread.interrupt();
    }
}
