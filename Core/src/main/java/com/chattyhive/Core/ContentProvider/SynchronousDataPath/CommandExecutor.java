package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.Util.CallbackDelegate;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonathan on 08/03/2015.
 */
public class CommandExecutor implements Runnable {
    private CommandQueue commandQueue;
    private enum DataOriginTypes { Cache, Local, RemoteServer, RemoteStorage }
    private HashMap<DataOriginTypes, DataOrigin> dataOrigins;

    private CallbackDelegate<ProcessorCallbackArgs> cacheCallback;
    private CallbackDelegate<ProcessorCallbackArgs> localCallback;
    private CallbackDelegate<ProcessorCallbackArgs> remoteCallback;

    public CommandExecutor(CommandQueue commandQueue, IOrigin localCache, IOrigin localData, IOrigin remoteServer, IOrigin remoteStorage) {
        this.commandQueue = commandQueue;
        this.dataOrigins = new HashMap<DataOriginTypes, DataOrigin>();

        if (localCache != null) {
            this.dataOrigins.put(DataOriginTypes.Cache, new DataOrigin(localCache));
            this.cacheCallback = new CallbackDelegate<>(this::CacheCallback);
        }

        if (localData != null) {
            this.dataOrigins.put(DataOriginTypes.Local, new DataOrigin(localData));
            this.localCallback = new CallbackDelegate<>(this::LocalCallback);
        }

        if (remoteServer != null) {
            this.dataOrigins.put(DataOriginTypes.RemoteServer, new DataOrigin(remoteServer));
            this.remoteCallback = new CallbackDelegate<>(this::RemoteCallback);
        }

        if (remoteStorage != null) {
            this.dataOrigins.put(DataOriginTypes.RemoteStorage, new DataOrigin(remoteStorage));
        }

        for (Map.Entry<DataOriginTypes,DataOrigin> entry : dataOrigins.entrySet()) {
            Thread t = new Thread(entry.getValue());
            t.setName(String.format("%sThread.",entry.getKey().name()));
            t.start();
        }
    }

    @Override
    public void run() {
        AbstractMap.Entry<CommandQueue.Priority,Command> processingRequest;
        Command processingCommand;
        try { processingRequest = commandQueue.pollRequest(); } catch (InterruptedException e) { processingRequest = null; }
        while (processingRequest != null) {
            //Route command
            processingCommand = processingRequest.getValue();
            CommandDefinition commandDefinition = processingCommand.getCommandDefinition();
            switch (commandDefinition.getCommandType()) {
                case Session:
                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
                case Query:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Cache)) //IF CACHE IS DEFINED: Check cache
                        this.dataOrigins.get(DataOriginTypes.Cache).ProcessCommand(processingCommand,processingRequest.getKey(),cacheCallback);
                    else if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) //IF CACHE NOT DEFINED: Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
                case Pull:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) //IF LOCAL IS DEFINED: Check local
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),localCallback);
                    else if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) //IF LOCAL NOT DEFINED: Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
                case ForcePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
                case ImmediateResponsePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
                case DelayedResponsePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback);
                    break;
            }

            //Get next command.
            try { processingRequest = commandQueue.pollRequest(); } catch (InterruptedException e) { processingRequest = null; }
        }
    }

    public void CacheCallback(ProcessorCallbackArgs processorCallbackArgs) {
        Command command = processorCallbackArgs.getCommand();
        CommandQueue.Priority priority = processorCallbackArgs.getPriority();

        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Query:
                //TODO: GO TO SERVER?
                if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                    this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(command,priority,remoteCallback);
                //Process callbacks
                while (command.countCallbackDelegates() > 0) {
                    CallbackDelegate<Command> callbackDelegate = command.popCallbackDelegate(10000000);
                    callbackDelegate.Run(command);
                }
                break;
        }
    }

    public void LocalCallback(ProcessorCallbackArgs processorCallbackArgs) {
        Command command = processorCallbackArgs.getCommand();
        CommandQueue.Priority priority = processorCallbackArgs.getPriority();

        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Pull:
                //TODO: GO TO SERVER?
                if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                    this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(command,priority,remoteCallback);
                //Process callbacks
                while (command.countCallbackDelegates() > 0) {
                    CallbackDelegate<Command> callbackDelegate = command.popCallbackDelegate(10000000);
                    callbackDelegate.Run(command);
                }
                break;
        }
    }

    public void RemoteCallback(ProcessorCallbackArgs processorCallbackArgs) {
        Command command = processorCallbackArgs.getCommand();
        CommandQueue.Priority priority = processorCallbackArgs.getPriority();

        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Session:
                //TODO: DO SOMETHING?
                //Process callbacks
                while (command.countCallbackDelegates() > 0) {
                    CallbackDelegate<Command> callbackDelegate = command.popCallbackDelegate(10000000);
                    callbackDelegate.Run(command);
                }
                break;
            case Query:
                if (this.dataOrigins.containsKey(DataOriginTypes.Cache)) // Update cache
                    this.dataOrigins.get(DataOriginTypes.Cache).ProcessCommand(command,priority,null);
                //Process callbacks
                while (command.countCallbackDelegates() > 0) {
                    CallbackDelegate<Command> callbackDelegate = command.popCallbackDelegate(10000000);
                    callbackDelegate.Run(command);
                }
                break;
            case Pull:
            case ForcePush:
            case ImmediateResponsePush:
            case DelayedResponsePush:
                if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Update local
                    this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(command,priority,null);
                //Process callbacks
                while (command.countCallbackDelegates() > 0) {
                    CallbackDelegate<Command> callbackDelegate = command.popCallbackDelegate(10000000);
                    callbackDelegate.Run(command);
                }
                break;
        }
    }

}
