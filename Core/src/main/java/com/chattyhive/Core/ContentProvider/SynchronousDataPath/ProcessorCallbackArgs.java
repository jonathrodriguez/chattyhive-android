package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

/**
 * Created by jonathrodriguez on 03/04/2016.
 */
public class ProcessorCallbackArgs {
    private Command command;

    private CommandQueue.Priority priority;

    ProcessorCallbackArgs(Command command, CommandQueue.Priority priority) {
        this.command = command;
        this.priority = priority;
    }

    Command getCommand() {
        return command;
    }

    CommandQueue.Priority getPriority() {
        return priority;
    }
}
