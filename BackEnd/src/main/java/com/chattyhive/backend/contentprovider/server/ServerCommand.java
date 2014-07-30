package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/*
 * Created by Jonathan on 11/07/2014.
 */
public class ServerCommand {
    public enum AvailableCommands { Register, EmailCheck, Explore, Join, SendMessage, GetMessages, LocalProfile, ChatContext, ChatList, UserProfile }
    public enum Method { GET, POST }

    /*************************************/
    /*     STATIC COMMAND DEFINITION     */
    /*************************************/

    private static HashMap<AvailableCommands,ServerCommand> CommandDefinitions;

    public static void Initialize() {
        ServerCommand.CommandDefinitions = new HashMap<AvailableCommands, ServerCommand>();

        ServerCommand serverCommand;

        ArrayList<Class<?>> inputFormats;
        ArrayList<Class<?>> paramFormats;

        // REGISTER
        inputFormats = new ArrayList<Class<?>>() {{add(LOCAL_USER_PROFILE.class);}};
        serverCommand = new ServerCommand(Method.POST,"android.register/", null, inputFormats);
        ServerCommand.CommandDefinitions.put(AvailableCommands.Register,serverCommand);

        // EmailCheck
        inputFormats = new ArrayList<Class<?>>() {{add(USER_EMAIL.class);}};
        serverCommand = new ServerCommand(Method.GET,"android.email_check", null, inputFormats);
        ServerCommand.CommandDefinitions.put(AvailableCommands.EmailCheck,serverCommand);

        // Explore
        serverCommand = new ServerCommand(Method.GET,"android.explore", null, null);
        ServerCommand.CommandDefinitions.put(AvailableCommands.Explore,serverCommand);

        // Join
        inputFormats = new ArrayList<Class<?>>() {{add(HIVE_ID.class);}};
        serverCommand = new ServerCommand(Method.POST,"android.join/", null, inputFormats);
        ServerCommand.CommandDefinitions.put(AvailableCommands.Join,serverCommand);

        // SendMessage
        inputFormats = new ArrayList<Class<?>>() {{add(MESSAGE.class);}};
        serverCommand = new ServerCommand(Method.POST,"android.chat/", null, inputFormats);
        ServerCommand.CommandDefinitions.put(AvailableCommands.SendMessage,serverCommand);

        // GetMessages
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class); add(MESSAGE_INTERVAL.class); }};
        serverCommand = new ServerCommand(Method.GET,"android.messages/[CHAT_ID.CHANNEL_UNICODE]/[MESSAGE_INTERVAL.LAST_MESSAGE_ID]/[MESSAGE_INTERVAL.COUNT]", paramFormats, null);
        ServerCommand.CommandDefinitions.put(AvailableCommands.GetMessages,serverCommand);

        // LocalProfile
        serverCommand = new ServerCommand(Method.GET,"android.recover_local_user_profile", null, null);
        ServerCommand.CommandDefinitions.put(AvailableCommands.LocalProfile,serverCommand);

        // ChatContext
        paramFormats = new ArrayList<Class<?>>() {{add(CHAT_ID.class);}};
        serverCommand = new ServerCommand(Method.GET,"android.get_chat_context/[CHAT_ID.CHANNEL_UNICODE]", paramFormats, null);
        ServerCommand.CommandDefinitions.put(AvailableCommands.ChatContext,serverCommand);

        // ChatList
        serverCommand = new ServerCommand(Method.GET,"android.get_chat_list", null, null);
        ServerCommand.CommandDefinitions.put(AvailableCommands.ChatList,serverCommand);

        // UserProfile
        inputFormats = new ArrayList<Class<?>>() {{add(PROFILE_ID.class);}};
        serverCommand = new ServerCommand(Method.POST,"android.???/", null, inputFormats);
        ServerCommand.CommandDefinitions.put(AvailableCommands.UserProfile,serverCommand);
    }

    public static ServerCommand GetCommand(AvailableCommands command) {
        if (ServerCommand.CommandDefinitions == null) throw new NullPointerException("Command definitions not initialized.");
        if (!ServerCommand.CommandDefinitions.containsKey(command)) throw new IllegalArgumentException(String.format("Command (%s) is not defined.",command.toString()));

        return ServerCommand.CommandDefinitions.get(command);
    }

    /*************************************/

    /*************************************/
    /*      SERVER COMMAND CLASS         */
    /*************************************/

    private Method method;
    private String url;
    private ArrayList<Class<?>> inputFormats;
    private ArrayList<Class<?>> paramFormats;

    private ServerCommand (Method method, String url, ArrayList<Class<?>> paramFormats, ArrayList<Class<?>> inputFormats) {
        this.method = method;
        this.url = url;
        this.paramFormats = paramFormats;
        this.inputFormats = inputFormats;
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
                if (f.getClass().getName().equalsIgnoreCase(formatName)) {
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
        if ((formats == null) || (formats.length == 0)) return null;

        JsonObject bodyData = new JsonObject();

        for (Format f : formats) {
            if (this.inputFormats.contains(f.getClass())) {
                bodyData.add(f.getClass().getName(), f.toJSON());
            }
        }

        return bodyData.toString();
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
