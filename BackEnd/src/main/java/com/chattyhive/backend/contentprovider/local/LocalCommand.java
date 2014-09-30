package com.chattyhive.backend.contentprovider.local;

import com.chattyhive.backend.contentprovider.AvailableCommands;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jonathan on 30/09/2014.
 */
public class LocalCommand {
    public enum CommandLocation {
        Users,
        Messages,
        Groups,
        Hives
    }

    private static HashMap<AvailableCommands,LocalCommand> CommandDefinitions;

    static {

    }

    private static void AddLocalCommand(AvailableCommands command, CommandLocation commandLocation, String url,ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats, ArrayList<String> requiredCookies, ArrayList<String> returningCookies) {
        LocalCommand localCommand = new LocalCommand(command, method, commandType, url, paramFormats, inputFormats, requiredCookies, returningCookies);
        LocalCommand.CommandDefinitions.put(command,localCommand);
    }

    public static LocalCommand GetCommand(AvailableCommands command) {
        if (!LocalCommand.CommandDefinitions.containsKey(command)) throw new IllegalArgumentException(String.format("Command (%s) is not defined.",command.toString()));
        return LocalCommand.CommandDefinitions.get(command);
    }

    /*************************************/

    /*************************************/
    /*       LOCAL COMMAND CLASS         */
    /*************************************/

    private AvailableCommands command;
    private CommandLocation commandLocation;
    private ArrayList<Class<?>> inputFormats;
    private ArrayList<Class<?>> paramFormats;

}
