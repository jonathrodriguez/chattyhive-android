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
        /**
         * Commands used to set up a server session. (In this category there are only a few
         * commands, Start_Session and Login).
         */
        Session,
        /**
         * Commands used to query server. Returned data IS catchable but CAN NOT BE locally stored.
         * (Like exploring hives or retrieving a country list, a category list, ...).
         */
        Query,
        /**
         * Commands used to get data from server. Returned data IS NOT catchable but CAN BE
         * locally stored. (Like retrieving information about chats, hives or users).
         */
        Pull,
        /**
         * Commands used to send data to server. Data HAS TO accepted by server, and no error
         * is acceptable. Response HAS TO be immediate. (Like sending a message).
         */
        ForcePush,
        /**
         * Commands used to send data to server. Data COULD NOT be accepted by server if there
         * is newer data or any error. Response HAS TO be immediate. (Like joining new hives. An
         * error may occur when trying to join already joined hives).
         */
        ImmediateResponsePush,
        /**
         * Commands used to send data to server. Data COULD NOT be accepted by server if there
         * is newer data or any error. Response CAN delay for any amount of time. (Like soliciting
         * the change of the description of a hive).
         */
        DelayedResponsePush
    }

    private static HashMap<AvailableCommands,CommandDefinition> CommandDefinitions;

    static {
        CommandDefinition.Initialize();
    }

    private static void Initialize() {
        CommandDefinition.CommandDefinitions = new HashMap<>();

        AvailableCommands command;
        Method method;
        CommandType commandType;
        String url;
        List<Class<?>> inputFormats;
        List<Class<?>> paramFormats;
        List<Class<?>> returningFormats;
        List<String> requiredCookies;
        List<String> returningCookies;

        // StartSession
        command = AvailableCommands.StartSession;
        method = Method.GET;
        commandType = CommandType.Session;
        url = "android.start_session";
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(CSRF_TOKEN.class);}};
        requiredCookies = null;
        returningCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Login
        command = AvailableCommands.Login;
        method = Method.POST;
        commandType = CommandType.Session;
        url = "android.login/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOGIN.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Register
        command = AvailableCommands.Register;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.register/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // EmailCheck
        command = AvailableCommands.EmailCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.email_check/[USER_EMAIL.EMAIL_USER_PART]/[USER_EMAIL.EMAIL_SERVER_PART]";
        paramFormats = new ArrayList<Class<?>>() {{add(USER_EMAIL.class);}};
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // UsernameCheck
        command = AvailableCommands.UsernameCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.username_check/[USERNAME.PUBLIC_NAME]";
        paramFormats = new ArrayList<Class<?>>() {{add(USERNAME.class);}};
        inputFormats = null;
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // Explore //TODO: Add optional parameters
        command = AvailableCommands.Explore;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.explore/[EXPLORE_FILTER.TYPE]";
        paramFormats = new ArrayList<Class<?>>() {{add(EXPLORE_FILTER.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(HIVE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Join
        command = AvailableCommands.Join;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.join/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // SendMessage
        command = AvailableCommands.SendMessage;
        method = Method.POST;
        commandType = CommandType.ForcePush;
        url = "android.chat/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(MESSAGE.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(MESSAGE_ACK.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // GetMessages //TODO: Add optional parameters
        command = AvailableCommands.GetMessages;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.messages/[CHAT_ID.CHANNEL_UNICODE]/[MESSAGE_INTERVAL.LAST_MESSAGE_ID]/[MESSAGE_INTERVAL.COUNT]";
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class); add(MESSAGE_INTERVAL.class); }};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(MESSAGE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // LocalProfile
        command = AvailableCommands.LocalProfile;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.recover_local_user_profile";
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // UpdateProfile
        command = AvailableCommands.UpdateProfile;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.update_local_user_profile/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // ChatInfo
        command = AvailableCommands.ChatInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_context/[CHAT_ID.CHANNEL_UNICODE]";
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // ChatList
        command = AvailableCommands.ChatList;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_list";
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(CHAT_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // UserProfile
        command = AvailableCommands.UserProfile;
        method = Method.POST;
        commandType = CommandType.Pull;
        url = "android.???/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(PROFILE_ID.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // HiveInfo
        command = AvailableCommands.HiveInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_hive_info/[HIVE_ID.NAME_URL]";
        paramFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(HIVE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // HiveUsers
        command = AvailableCommands.HiveUsers;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.get_hive_users/[HIVE_ID.NAME_URL]";
        paramFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class); add(HIVE_USERS_FILTER.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(USER_PROFILE_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // CreateHive
        command = AvailableCommands.CreateHive;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.hives";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(HIVE.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class); add(CHAT.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);
    }
    private static void AddCommandDefinition(AvailableCommands command,
                                             Method method,
                                             CommandType commandType,
                                             String url,
                                             List<Class<?>> paramFormats,
                                             List<Class<?>> inputFormats,
                                             List<Class<?>> returningFormats,
                                             List<String> requiredCookies,
                                             List<String> returningCookies) {
        CommandDefinition commandDefinition = new CommandDefinition(command, method, commandType, url)
            .withParamFormats(paramFormats)
            .withInputFormats(inputFormats)
            .withReturningFormats(returningFormats)
            .withRequiredCookies(requiredCookies)
            .withReturningCookies(returningCookies);
        CommandDefinition.CommandDefinitions.put(command, commandDefinition);
    }
    public static CommandDefinition GetCommand(AvailableCommands command) {
        if (!CommandDefinition.CommandDefinitions.containsKey(command)) {
            throw new IllegalArgumentException(
                    String.format("Command (%s) is not defined.",command.toString()));
        }
        return CommandDefinition.CommandDefinitions.get(command);
    }


    /*******************************************************************************************/
    private final AvailableCommands command;
    private final Method method;
    private final CommandType commandType;
    private final String url;

    private List<Class<?>> inputFormats;
    private List<Class<?>> paramFormats;
    private List<Class<?>> returningFormats;

    private List<String> requiredCookies;
    private List<String> returningCookies;

    private CommandDefinition(AvailableCommands command,
                              Method method,
                              CommandType commandType,
                              String url) {
        this.command = command;
        this.method = method;
        this.commandType = commandType;
        this.url = url;
    }

    public CommandDefinition withInputFormats(List<Class<?>> inputFormats) {
        if (inputFormats != null)
            this.inputFormats = Collections.unmodifiableList(inputFormats);
        else
            this.inputFormats = Collections.emptyList();

        return this;
    }
    public CommandDefinition withParamFormats(List<Class<?>> paramFormats) {
        if (paramFormats != null)
            this.paramFormats = Collections.unmodifiableList(paramFormats);
        else
            this.paramFormats = Collections.emptyList();

        return this;
    }
    public CommandDefinition withReturningFormats(List<Class<?>> returningFormats) {
        if (returningFormats != null)
            this.returningFormats = Collections.unmodifiableList(returningFormats);
        else
            this.returningFormats = Collections.emptyList();

        return this;
    }
    public CommandDefinition withRequiredCookies(List<String> requiredCookies) {
        if (requiredCookies != null)
            this.requiredCookies = Collections.unmodifiableList(requiredCookies);
        else
            this.requiredCookies = Collections.emptyList();

        return this;
    }
    public CommandDefinition withReturningCookies(List<String> returningCookies) {
        if (returningCookies != null)
            this.returningCookies = Collections.unmodifiableList(returningCookies);
        else
            this.returningCookies = Collections.emptyList();

        return this;
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
    public List<Class<?>> getInputFormats() {
        return this.inputFormats;
    }
    public List<Class<?>> getParamFormats() {
        return this.paramFormats;
    }
    public List<Class<?>> getReturningFormats() {
        return this.returningFormats;
    }
    public List<String> getRequiredCookies() {
        return this.requiredCookies;
    }
    public List<String> getReturningCookies() {
        return this.returningCookies;
    }
}
