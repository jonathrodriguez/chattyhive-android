package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_LIST;
import com.chattyhive.Core.ContentProvider.Formats.CSRF_TOKEN;
import com.chattyhive.Core.ContentProvider.Formats.EXPLORE_FILTER;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Formats.HIVE;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_ID;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_LIST;
import com.chattyhive.Core.ContentProvider.Formats.HIVE_USERS_FILTER;
import com.chattyhive.Core.ContentProvider.Formats.LOCAL_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.LOGIN;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_ACK;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_INTERVAL;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_LIST;
import com.chattyhive.Core.ContentProvider.Formats.PROFILE_ID;
import com.chattyhive.Core.ContentProvider.Formats.USERNAME;
import com.chattyhive.Core.ContentProvider.Formats.USER_EMAIL;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE_LIST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jonathan on 28/01/2015.
 */
public class CommandDefinition {

    public static final String CSRFTokenCookie = "csrftoken";
    public static final String SessionCookie = "sessionid";

    public enum Method { GET, POST } //Http protocol method.
    public enum CommandType {
        Session, //Commands used to set up a server session. (In this category there are only a few commands, Start_Session and Login).
        Query, //Commands used to query server. Returned data IS catchable but CAN NOT BE locally stored. (Like exploring hives or retrieving a country list, a category list, ...).
        Pull, //Commands used to get data from server. Returned data IS NOT catchable but CAN BE locally stored. (Like retrieving information about chats, hives or users).
        ForcePush, //Commands used to send data to server. Data HAS TO accepted by server, and no error is acceptable. Response HAS TO be immediate. (Like sending a message).
        ImmediateResponsePush, //Commands used to send data to server. Data COULD NOT be accepted by server if there is newer data or any error. Response HAS TO be immediate. (Like joining new hives. An error may occur when trying to join already joined hives).
        DelayedResponsePush //Commands used to send data to server. Data COULD NOT be accepted by server if there is newer data or any error. Response CAN delay for any amount of time. (Like soliciting the change of the description of a hive).
    }

    private static HashMap<AvailableCommands,CommandDefinition> CommandDefinitions;
    private static HashMap<String,ArrayList<AvailableCommands>> CookieProductionCommands;

    static {
        CommandDefinition.Initialize();
    }
    private static void Initialize() {
        CommandDefinition.CommandDefinitions = new HashMap<AvailableCommands, CommandDefinition>();
        CommandDefinition.CookieProductionCommands = new HashMap<String, ArrayList<AvailableCommands>>();

        AvailableCommands command;
        Method method;
        CommandType commandType;
        String url;
        ArrayList<Class<?>> requiredInputFormats;
        ArrayList<Class<?>> requiredParamFormats;
        ArrayList<Class<?>> inputFormats;
        ArrayList<Class<?>> paramFormats;
        ArrayList<Class<? extends Format>> returningFormats;
        ArrayList<String> requiredCookies;
        ArrayList<String> returningCookies;

        // StartSession
        command = AvailableCommands.StartSession;
        method = Method.GET;
        commandType = CommandType.Session;
        url = "android.start_session";
        requiredParamFormats = null;
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(CSRF_TOKEN.class);}};
        requiredCookies = null;
        returningCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Login
        command = AvailableCommands.Login;
        method = Method.POST;
        commandType = CommandType.Session;
        url = "android.login/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(LOGIN.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Register
        command = AvailableCommands.Register;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.register/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // EmailCheck
        command = AvailableCommands.EmailCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.email_check/[USER_EMAIL.EMAIL_USER_PART]/[USER_EMAIL.EMAIL_SERVER_PART]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(USER_EMAIL.class);}};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // UsernameCheck
        command = AvailableCommands.UsernameCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.username_check/[USERNAME.PUBLIC_NAME]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(USERNAME.class);}};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // Explore //TODO: Add optional parameters
        command = AvailableCommands.Explore;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.explore/[EXPLORE_FILTER.TYPE]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(EXPLORE_FILTER.class);}};;
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(HIVE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Join
        command = AvailableCommands.Join;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.join/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // SendMessage
        command = AvailableCommands.SendMessage;
        method = Method.POST;
        commandType = CommandType.ForcePush;
        url = "android.chat/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(MESSAGE.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(MESSAGE_ACK.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // GetMessages //TODO: Add optional parameters
        command = AvailableCommands.GetMessages;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.messages/[CHAT_ID.CHANNEL_UNICODE]/[MESSAGE_INTERVAL.LAST_MESSAGE_ID]/[MESSAGE_INTERVAL.COUNT]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class); add(MESSAGE_INTERVAL.class); }};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(MESSAGE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // LocalProfile
        command = AvailableCommands.LocalProfile;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.recover_local_user_profile";
        requiredParamFormats = null;
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(LOCAL_USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // UpdateProfile
        command = AvailableCommands.UpdateProfile;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.update_local_user_profile/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // ChatInfo
        command = AvailableCommands.ChatInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_context/[CHAT_ID.CHANNEL_UNICODE]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class);}};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // ChatList
        command = AvailableCommands.ChatList;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_list";
        requiredParamFormats = null;
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(CHAT_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // UserProfile
        command = AvailableCommands.UserProfile;
        method = Method.POST;
        commandType = CommandType.Pull;
        url = "android.???/";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(PROFILE_ID.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // HiveInfo
        command = AvailableCommands.HiveInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_hive_info/[HIVE_ID.NAME_URL]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(HIVE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // HiveUsers
        command = AvailableCommands.HiveUsers;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.get_hive_users/[HIVE_ID.NAME_URL]";
        requiredParamFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class); add(HIVE_USERS_FILTER.class);}};
        requiredInputFormats = null;
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(USER_PROFILE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // CreateHive
        command = AvailableCommands.CreateHive;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.hives";
        requiredParamFormats = null;
        requiredInputFormats = new ArrayList<Class<?>>() {{add(HIVE.class);}};
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<? extends Format>>() {{add(HIVE_ID.class); add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddServerCommand(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);
    }
    private static void AddServerCommand(AvailableCommands command, Method method, CommandType commandType, String url, ArrayList<Class<?>> requiredParamFormats, ArrayList<Class<?>> requiredInputFormats, ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats, ArrayList<Class<? extends Format>> returningFormats, ArrayList<String> requiredCookies, ArrayList<String> returningCookies) {
        CommandDefinition commandDefinition = new CommandDefinition(command, method, commandType, url, requiredParamFormats, requiredInputFormats, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);
        CommandDefinition.CommandDefinitions.put(command, commandDefinition);
        if (returningCookies != null)
            for (String returningCookie : returningCookies) {
                if (!CommandDefinition.CookieProductionCommands.containsKey(returningCookie))
                    CommandDefinition.CookieProductionCommands.put(returningCookie,new ArrayList<AvailableCommands>());
                CommandDefinition.CookieProductionCommands.get(returningCookie).add(command);
            }
    }
    public static CommandDefinition GetCommand(AvailableCommands command) {
        if (!CommandDefinition.CommandDefinitions.containsKey(command)) throw new IllegalArgumentException(String.format("Command (%s) is not defined.",command.toString()));
        return CommandDefinition.CommandDefinitions.get(command);
    }
    public static ArrayList<AvailableCommands> GetCommandForCookie(String cookie) {
        if (!CommandDefinition.CookieProductionCommands.containsKey(cookie)) return null;
        return CommandDefinition.CookieProductionCommands.get(cookie);
    }

    /*******************************************************************************************/
    private final AvailableCommands command;
    private final Method method;
    private final CommandType commandType;
    private final String url;
    private final List<Class<?>> requiredInputFormats;
    private final List<Class<?>> requiredParamFormats;

    private final List<Class<?>> inputFormats;
    private final List<Class<?>> paramFormats;

    private final List<Class<? extends Format>> returningFormats;

    private final List<String> requiredCookies;
    private final List<String> returningCookies;

    private CommandDefinition(AvailableCommands command, Method method, CommandType commandType, String url, ArrayList<Class<?>> requiredParamFormats, ArrayList<Class<?>> requiredInputFormats, ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats, ArrayList<Class<? extends Format>> returningFormats, ArrayList<String> requiredCookies, ArrayList<String> returningCookies) {
        this.command = command;
        this.method = method;
        this.commandType = commandType;
        this.url = url;
        if (requiredParamFormats != null)
            this.requiredParamFormats = Collections.unmodifiableList(requiredParamFormats);
        else
            this.requiredParamFormats = Collections.emptyList();

        if (requiredInputFormats != null)
            this.requiredInputFormats = Collections.unmodifiableList(requiredInputFormats);
        else
            this.requiredInputFormats = Collections.emptyList();

        if (paramFormats != null)
            this.paramFormats = Collections.unmodifiableList(paramFormats);
        else
            this.paramFormats = Collections.emptyList();

        if (inputFormats != null)
            this.inputFormats = Collections.unmodifiableList(inputFormats);
        else
            this.inputFormats = Collections.emptyList();

        if (returningFormats != null)
            this.returningFormats = Collections.unmodifiableList(returningFormats);
        else
            this.returningFormats = Collections.emptyList();

        if (requiredCookies != null)
            this.requiredCookies = Collections.unmodifiableList(requiredCookies);
        else
            this.requiredCookies = Collections.emptyList();

        if (returningCookies != null)
            this.returningCookies = Collections.unmodifiableList(returningCookies);
        else
            this.returningCookies = Collections.emptyList();
    }

    public AvailableCommands getCommand() {
        return this.command;
    }
    public Method getMethod() {
        return this.method;
    }
    public CommandType getCommandType() {
        return this.commandType;
    }
    public String getUrl() {
        return this.url;
    }
    public List<Class<?>> getRequiredInputFormats() {
        return this.requiredInputFormats;
    }
    public List<Class<?>> getRequiredParamFormats() {
        return this.requiredParamFormats;
    }
    public List<Class<?>> getInputFormats() {
        return this.inputFormats;
    }
    public List<Class<?>> getParamFormats() {
        return this.paramFormats;
    }
    public List<Class<? extends Format>> getReturningFormats() {
        return this.returningFormats;
    }
    public List<String> getRequiredCookies() {
        return this.requiredCookies;
    }
    public List<String> getReturningCookies() {
        return this.returningCookies;
    }
}
