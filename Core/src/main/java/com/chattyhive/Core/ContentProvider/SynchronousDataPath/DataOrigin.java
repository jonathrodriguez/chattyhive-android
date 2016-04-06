package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.Util.CallbackDelegate;

import java.util.HashMap;

/**
 * Created by Jonathan on 08/03/2015.
 */
public class DataOrigin implements Runnable {
    private CommandQueue commandQueue;
    private final HashMap<Command,CallbackDelegate<ProcessorCallbackArgs>> commandCallbacks;
    private final HashMap<Command,ProcessorCallbackArgs> commandCallbackParameters;

    private IOrigin origin;

    public DataOrigin(IOrigin origin) {
        this.commandQueue = new CommandQueue();
        this.commandCallbacks = new HashMap<>();
        this.commandCallbackParameters = new HashMap<>();
    }

    public void ProcessCommand(Command command,CommandQueue.Priority priority, CallbackDelegate<ProcessorCallbackArgs> callback) {
        CommandQueue.Priority finalPriority = (priority != null)?priority: CommandQueue.Priority.Medium;
        try { this.commandQueue.put(command, finalPriority); } catch (InterruptedException e) { e.printStackTrace(); return; }
        if (callback != null) {
            synchronized (this.commandCallbacks) {
                this.commandCallbacks.put(command, callback);
                this.commandCallbackParameters.put(command, new ProcessorCallbackArgs(command,priority));
                this.commandCallbacks.notify();
            }
        }
    }

    @Override
    public void run() {
        Command processingCommand;
        try { processingCommand = commandQueue.poll(); } catch (InterruptedException e) { processingCommand = null; }
        while (processingCommand != null) {
            CallbackDelegate<ProcessorCallbackArgs> callback;
            ProcessorCallbackArgs callbackParameters = null;
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
