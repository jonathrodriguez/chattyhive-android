package com.chattyhive.backend.ContentProvider.SynchronousDataPath;

import com.chattyhive.backend.Util.CallbackDelegate;

import java.util.HashMap;

/**
 * Created by Jonathan on 08/03/2015.
 */
public class DataOrigin implements Runnable {
    private CommandQueue commandQueue;
    private final HashMap<Command,CallbackDelegate> commandCallbacks;
    private final HashMap<Command,Object[]> commandCallbackParameters;

    private IOrigin origin;

    public DataOrigin(IOrigin origin) {
        this.commandQueue = new CommandQueue();
        this.commandCallbacks = new HashMap<Command, CallbackDelegate>();
        this.commandCallbackParameters = new HashMap<Command, Object[]>();
    }

    public void ProcessCommand(Command command,CommandQueue.Priority priority, CallbackDelegate callback, Object... callbackParameters) {
        CommandQueue.Priority finalPriority = (priority != null)?priority: CommandQueue.Priority.Medium;
        try { this.commandQueue.put(command, finalPriority); } catch (InterruptedException e) { e.printStackTrace(); return; }
        synchronized (this.commandCallbacks) {
            this.commandCallbacks.put(command, callback);
            if (callbackParameters != null)
                this.commandCallbackParameters.put(command,callbackParameters);
            this.commandCallbacks.notify();
        }
    }

    @Override
    public void run() {
        Command processingCommand;
        try { processingCommand = commandQueue.poll(); } catch (InterruptedException e) { processingCommand = null; }
        while (processingCommand != null) {
            CallbackDelegate callback;
            Object[] callbackParameters = null;
            synchronized (commandCallbacks) {
                try { while (!commandCallbacks.containsKey(processingCommand)) commandCallbacks.wait(); } catch (InterruptedException e) { break; }
                callback = commandCallbacks.get(processingCommand);
                commandCallbacks.remove(processingCommand);
                if (commandCallbackParameters.containsKey(processingCommand)) {
                    callbackParameters = commandCallbackParameters.get(processingCommand);
                    commandCallbackParameters.remove(processingCommand);
                }
            }
            origin.ProcessCommand(processingCommand,callback,callbackParameters);
            //Get next command.
            try { processingCommand = commandQueue.poll(); } catch (InterruptedException e) { processingCommand = null; }
        }
    }
}
