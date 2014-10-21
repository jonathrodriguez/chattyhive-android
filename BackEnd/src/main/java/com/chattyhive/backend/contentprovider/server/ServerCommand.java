package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.USERNAME;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;

import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * Created by Jonathan on 11/07/2014.
 */
public class ServerCommand {
    public enum Method { GET, POST } //Http protocol method.

    public enum CommandType {
        Session, //Commands used to set up a server session. (In this category there are only two commands, Start_Session and Login).
        Query, //Commands used to query server. Returned data IS NOT catchable. (Like exploring hives or retrieving a country list, a category list, ...).
        Pull, //Commands used to get data from server. Returned data IS catchable. (Like retrieving information about chats, hives or users).
        ForcePush, //Commands used to send data to server. Data HAS TO accepted by server, and no error is acceptable. Response HAS TO be immediate. (Like sending a message).
        ImmediateResponsePush, //Commands used to send data to server. Data COULD NOT be accepted by server if there is newer data or any error. Response HAS TO be immediate. (Like joining new hives. An error may occur when trying to join already joined hives).
        DelayedResponsePush //Commands used to send data to server. Data COULD NOT be accepted by server if there is newer data or any error. Response CAN delay for any amount of time. (Like soliciting the change of the description of a hive).
    }

    /*************************************/
    /*     STATIC COMMAND DEFINITION     */
    /*************************************/

    private static HashMap<AvailableCommands,ServerCommand> CommandDefinitions;
    private static HashMap<String,ArrayList<AvailableCommands>> CookieProductionCommands;

    static {
        ServerCommand.Initialize();
    }

    private static void Initialize() {
        final String CSRFTokenCookie = "csrftoken";
        final String SessionCookie = "sessionid";

        ServerCommand.CommandDefinitions = new HashMap<AvailableCommands, ServerCommand>();
        ServerCommand.CookieProductionCommands = new HashMap<String, ArrayList<AvailableCommands>>();

        AvailableCommands command;
        Method method;
        CommandType commandType;
        String url;
        ArrayList<Class<?>> inputFormats;
        ArrayList<Class<?>> paramFormats;
        ArrayList<String> requiredCookies;
        ArrayList<String> returningCookies;

        // StartSession
        command = AvailableCommands.StartSession;
        method = Method.GET;
        commandType = CommandType.Session;
        url = "android.start_session";
        paramFormats = null;
        inputFormats = null;
        requiredCookies = null;
        returningCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // Login
        command = AvailableCommands.Login;
        method = Method.POST;
        commandType = CommandType.Session;
        url = "android.login/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOGIN.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // Register
        command = AvailableCommands.Register;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.register/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // EmailCheck
        command = AvailableCommands.EmailCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.email_check/[USER_EMAIL.EMAIL_USER_PART]/[USER_EMAIL.EMAIL_SERVER_PART]";
        paramFormats = new ArrayList<Class<?>>() {{add(USER_EMAIL.class);}};
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);

        // UsernameCheck
        command = AvailableCommands.UsernameCheck;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.username_check/[USERNAME.PUBLIC_NAME]";
        paramFormats = new ArrayList<Class<?>>() {{add(USERNAME.class);}};
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);

        // Explore
        command = AvailableCommands.Explore;
        method = Method.GET;
        commandType = CommandType.Query;
        url = "android.explore/";
        paramFormats = null;
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // Join
        command = AvailableCommands.Join;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.join/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // SendMessage
        command = AvailableCommands.SendMessage;
        method = Method.POST;
        commandType = CommandType.ForcePush;
        url = "android.chat/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(MESSAGE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // GetMessages
        command = AvailableCommands.GetMessages;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.messages/[CHAT_ID.CHANNEL_UNICODE]/[MESSAGE_INTERVAL.LAST_MESSAGE_ID]/[MESSAGE_INTERVAL.COUNT]";
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class); add(MESSAGE_INTERVAL.class); }};
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // LocalProfile
        command = AvailableCommands.LocalProfile;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.recover_local_user_profile";
        paramFormats = null;
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);

        // UpdateProfile
        command = AvailableCommands.UpdateProfile;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = "android.update_local_user_profile/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);

        // ChatInfo
        command = AvailableCommands.ChatInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_context/[CHAT_ID.CHANNEL_UNICODE]";
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class);}};
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // ChatList
        command = AvailableCommands.ChatList;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_chat_list";
        paramFormats = null;
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // UserProfile
        command = AvailableCommands.UserProfile;
        method = Method.POST;
        commandType = CommandType.Pull;
        url = "android.???/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(PROFILE_ID.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);


        // HiveInfo
        command = AvailableCommands.HiveInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = "android.get_hive_info/[HIVE_ID.NAME_URL]";
        paramFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        inputFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        ServerCommand.AddServerCommand(command,method,commandType,url,paramFormats,inputFormats,requiredCookies,returningCookies);
    }

    private static void AddServerCommand(AvailableCommands command, Method method, CommandType commandType, String url,ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats, ArrayList<String> requiredCookies, ArrayList<String> returningCookies) {
        ServerCommand serverCommand = new ServerCommand(command, method, commandType, url, paramFormats, inputFormats, requiredCookies, returningCookies);
        ServerCommand.CommandDefinitions.put(command,serverCommand);
        if (returningCookies != null)
            for (String returningCookie : returningCookies) {
                if (!ServerCommand.CookieProductionCommands.containsKey(returningCookie))
                    ServerCommand.CookieProductionCommands.put(returningCookie,new ArrayList<AvailableCommands>());
                ServerCommand.CookieProductionCommands.get(returningCookie).add(command);
            }
    }

    public static ServerCommand GetCommand(AvailableCommands command) {
        if (!ServerCommand.CommandDefinitions.containsKey(command)) throw new IllegalArgumentException(String.format("Command (%s) is not defined.",command.toString()));
        return ServerCommand.CommandDefinitions.get(command);
    }

    public static ArrayList<AvailableCommands> GetCommandForCookie(String cookie) {
        if (!ServerCommand.CookieProductionCommands.containsKey(cookie)) return null;
        return ServerCommand.CookieProductionCommands.get(cookie);
    }

    /*************************************/

    /*************************************/
    /*      SERVER COMMAND CLASS         */
    /*************************************/

    private AvailableCommands command;
    private Method method;
    private CommandType commandType;
    private String url;
    private ArrayList<Class<?>> inputFormats;
    private ArrayList<Class<?>> paramFormats;

    private ArrayList<String> requiredCookies;
    private ArrayList<String> returningCookies;

    private ServerCommand (AvailableCommands command, Method method, CommandType commandType, String url, ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats, ArrayList<String> requiredCookies, ArrayList<String> returningCookies) {
        this.command = command;
        this.method = method;
        this.commandType = commandType;
        this.url = url;
        this.paramFormats = paramFormats;
        this.inputFormats = inputFormats;
        this.requiredCookies = requiredCookies;
        this.returningCookies = returningCookies;
    }

    public AvailableCommands getCommand() {
        return this.command;
    }
    public CommandType getCommandType() {
        return this.commandType;
    }
    public String getMethod() {
        return this.method.toString();
    }
    public String getUrl(Format... formats) {
        String url = this.url;
        int paramIndex = url.indexOf('[');
        while (paramIndex > -1) {
            int endParamIndex = url.indexOf(']');
            if (endParamIndex > -1) {
                String parameter = url.substring(paramIndex + 1, endParamIndex);
                int dotIndex = parameter.indexOf('.');
                if (dotIndex > -1) {
                    try {
                        String value = this.getUrlParameterValue(parameter, formats);
                        url = url.replace(String.format("[%s]", parameter), value);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            paramIndex = url.indexOf('[',paramIndex);
        }
        return url;
    }
    private String getUrlParameterValue(String parameter, Format... formats) throws NoSuchFieldException, IllegalAccessException {
        Format parameterFormat = null;

        int dotIndex = parameter.indexOf('.');
        int preIndex = 0;

        String formatName = parameter.substring(preIndex,dotIndex);

        for (Format f : formats)
            if (this.paramFormats.contains(f.getClass()))
                if (f.getClass().getSimpleName().equalsIgnoreCase(formatName)) {
                    parameterFormat = f;
                    break;
                }

        if (parameterFormat == null)
            throw new NullPointerException(String.format("No format specified for parameter %s.",parameter));

        preIndex = dotIndex+1;
        dotIndex = parameter.indexOf('.',preIndex);
        String fieldName;
        while (dotIndex > -1) {
            fieldName = parameter.substring(preIndex,dotIndex);
            Field field = parameterFormat.getClass().getField(fieldName);
            if (field.getType().getSuperclass().equals(Format.class)) {
                parameterFormat = (Format)field.get(parameterFormat);
            } else {
                throw new ClassCastException("Parametrized URLs can only access sub-fields of Format type fields.");
            }
            preIndex = dotIndex+1;
            dotIndex = parameter.indexOf('.',preIndex);
        }

        fieldName = parameter.substring(preIndex);
        Field field = parameterFormat.getClass().getField(fieldName);

        return field.get(parameterFormat).toString();
    }
    public String getBodyData(Format... formats) {
        if ((formats == null) || (formats.length == 0) || (this.inputFormats == null)) return null;

        //JsonObject bodyData = new JsonObject();

        String bodyData = "";

        for (Format f : formats) {
            if (this.inputFormats.contains(f.getClass())) {
                //bodyData.add(f.getClass().getSimpleName(), f.toJSON());
                String jsonString = f.toJSON().toString();
                bodyData += ((bodyData.isEmpty())?"{":", ") + jsonString.substring(1,jsonString.length()-1);
            }
        }

        bodyData += "}";

        return (!bodyData.equalsIgnoreCase("}"))?bodyData:"";//.toString();
    }

    public Boolean checkCookies() {
        if (this.requiredCookies == null) return true;

        TreeMap<String,Boolean> requiredCookiesCheck = new TreeMap<String, Boolean>();
        for (String cookie : this.requiredCookies)
            requiredCookiesCheck.put(cookie,false);

        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies != null) {
            for (HttpCookie cookie : cookies)
                if (requiredCookiesCheck.containsKey(cookie.getName()))
                    requiredCookiesCheck.put(cookie.getName(),true);
        }

        if (requiredCookiesCheck.size() > 0) {
            for (Boolean value : requiredCookiesCheck.values())
                if (!value)
                    return false;
        }

        return true;
    }
    public ArrayList<String> getUnsatisfyingCookies() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.requiredCookies == null) return null;

        TreeMap<String,Boolean> requiredCookiesCheck = new TreeMap<String, Boolean>();
        for (String cookie : this.requiredCookies)
            requiredCookiesCheck.put(cookie,false);

        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
        CookieStore cookieStore = cookieManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();

        if (cookies != null) {
            for (HttpCookie cookie : cookies)
                if (requiredCookiesCheck.containsKey(cookie.getName()))
                    requiredCookiesCheck.put(cookie.getName(),true);
        }

        if (requiredCookiesCheck.size() > 0) {
            for (Map.Entry<String,Boolean> requirement : requiredCookiesCheck.entrySet())
                if (!requirement.getValue())
                    result.add(requirement.getKey());
        }

        return (result.size() > 0)?result:null;
    }

    //TODO: Find a way to use this verification in compilation time instead of run time.
    public Boolean checkFormats(Format... formats) {
        TreeMap<String,Boolean> requiredFormats = new TreeMap<String,Boolean>();
        if (this.inputFormats != null)
            for (Class<?> format : this.inputFormats)
                requiredFormats.put(format.getName(),false);
        if (this.paramFormats != null)
            for (Class<?> format : this.paramFormats)
                requiredFormats.put(format.getName(),false);

        if (formats != null) {
            for (Format format : formats)
                if (requiredFormats.containsKey(format.getClass().getName()))
                    requiredFormats.put(format.getClass().getName(), true);
        }

        if (requiredFormats.size() > 0) {
            for (Boolean value : requiredFormats.values())
                if (!value)
                    return false;
        }

        return true;
    }
}
