package com.chattyhive.backend.ContentProvider.SynchronousDataPath;

import com.chattyhive.backend.Util.CallbackDelegate;

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

    private CallbackDelegate cacheCallback;
    private CallbackDelegate localCallback;
    private CallbackDelegate remoteCallback;

    public CommandExecutor(CommandQueue commandQueue, IOrigin localCache, IOrigin localData, IOrigin remoteServer, IOrigin remoteStorage) {
        this.commandQueue = commandQueue;
        this.dataOrigins = new HashMap<DataOriginTypes, DataOrigin>();

        if (localCache != null) {
            this.dataOrigins.put(DataOriginTypes.Cache, new DataOrigin(localCache));
            this.cacheCallback = new CallbackDelegate(this,"CacheCallback",Command.class, CommandQueue.Priority.class);
        }

        if (localData != null) {
            this.dataOrigins.put(DataOriginTypes.Local, new DataOrigin(localData));
            this.localCallback = new CallbackDelegate(this,"LocalCallback",Command.class, CommandQueue.Priority.class);
        }

        if (remoteServer != null) {
            this.dataOrigins.put(DataOriginTypes.RemoteServer, new DataOrigin(remoteServer));
            this.remoteCallback = new CallbackDelegate(this,"RemoteCallback",Command.class, CommandQueue.Priority.class);
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
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
                case Query:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Cache)) //IF CACHE IS DEFINED: Check cache
                        this.dataOrigins.get(DataOriginTypes.Cache).ProcessCommand(processingCommand,processingRequest.getKey(),cacheCallback,processingRequest.getKey());
                    else if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) //IF CACHE NOT DEFINED: Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
                case Pull:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) //IF LOCAL IS DEFINED: Check local
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),localCallback,processingRequest.getKey());
                    else if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) //IF LOCAL NOT DEFINED: Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
                case ForcePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
                case ImmediateResponsePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
                case DelayedResponsePush:
                    if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Store pending command
                        this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(processingCommand,processingRequest.getKey(),null);

                    if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                        this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(processingCommand,processingRequest.getKey(),remoteCallback,processingRequest.getKey());
                    break;
            }

            //Get next command.
            try { processingRequest = commandQueue.pollRequest(); } catch (InterruptedException e) { processingRequest = null; }
        }
    }

    public void CacheCallback(Command command, CommandQueue.Priority priority) {
        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Query:
                //TODO: GO TO SERVER?
                if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                    this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(command,priority,remoteCallback);
                //TODO: Process callbacks?
                break;
        }
    }

    public void LocalCallback(Command command, CommandQueue.Priority priority) {
        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Pull:
                //TODO: GO TO SERVER?
                if (this.dataOrigins.containsKey(DataOriginTypes.RemoteServer)) // Go to server
                    this.dataOrigins.get(DataOriginTypes.RemoteServer).ProcessCommand(command,priority,remoteCallback);
                //TODO: Process callbacks?
                break;
        }
    }

    public void RemoteCallback(Command command, CommandQueue.Priority priority) {
        CommandDefinition commandDefinition = command.getCommandDefinition();
        switch (commandDefinition.getCommandType()) {
            case Session:
                //TODO: DO SOMETHING?
                //TODO: Process callbacks
                break;
            case Query:
                if (this.dataOrigins.containsKey(DataOriginTypes.Cache)) // Update cache
                    this.dataOrigins.get(DataOriginTypes.Cache).ProcessCommand(command,priority,null);
                //TODO: Process callbacks?
                break;
            case Pull:
            case ForcePush:
            case ImmediateResponsePush:
            case DelayedResponsePush:
                if (this.dataOrigins.containsKey(DataOriginTypes.Local)) // Update local
                    this.dataOrigins.get(DataOriginTypes.Local).ProcessCommand(command,priority,null);
                //TODO: Process callbacks?
                break;
        }
    }
}
