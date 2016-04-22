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
import com.chattyhive.Core.ContentProvider.Formats.REQ_EMAIL_CHECK;
import com.chattyhive.Core.ContentProvider.Formats.REQ_JOIN;
import com.chattyhive.Core.ContentProvider.Formats.REQ_LOGIN;
import com.chattyhive.Core.ContentProvider.Formats.REQ_PUBLIC_NAME_CHECK;
import com.chattyhive.Core.ContentProvider.Formats.REQ_REGISTER_USER;
import com.chattyhive.Core.ContentProvider.Formats.REQ_SEND_MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.REQ_UPDATE_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.RES_CHAT_INFO;
import com.chattyhive.Core.ContentProvider.Formats.RES_CHAT_MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.RES_EXPLORE;
import com.chattyhive.Core.ContentProvider.Formats.RES_GET_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.RES_HIVE_INFO;
import com.chattyhive.Core.ContentProvider.Formats.RES_HIVE_USERS_LIST;
import com.chattyhive.Core.ContentProvider.Formats.RES_JOIN;
import com.chattyhive.Core.ContentProvider.Formats.RES_LOGIN;
import com.chattyhive.Core.ContentProvider.Formats.RES_PROFILE_CHAT_LIST;
import com.chattyhive.Core.ContentProvider.Formats.RES_PUBLIC_NAME_CHECK;
import com.chattyhive.Core.ContentProvider.Formats.RES_SEND_MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.RES_START_SESSION;
import com.chattyhive.Core.ContentProvider.Formats.URL_CHAT_INFO;
import com.chattyhive.Core.ContentProvider.Formats.URL_CHAT_MESSAGES;
import com.chattyhive.Core.ContentProvider.Formats.URL_EXPLORE;
import com.chattyhive.Core.ContentProvider.Formats.URL_GET_USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.URL_HIVE_INFO;
import com.chattyhive.Core.ContentProvider.Formats.URL_HIVE_USERS_LIST;
import com.chattyhive.Core.ContentProvider.Formats.URL_JOIN;
import com.chattyhive.Core.ContentProvider.Formats.URL_PROFILE_CHAT_LIST;
import com.chattyhive.Core.ContentProvider.Formats.URL_SEND_MESSAGE;
import com.chattyhive.Core.ContentProvider.Formats.URL_UPDATE_USER_PROFILE;
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

    public enum Method { GET, POST, PATCH, DELETE } //Http protocol method.
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
        URLGenerator url;
        List<Class<?>> inputFormats;
        List<Class<?>> paramFormats;
        List<Class<?>> returningFormats;
        List<String> requiredCookies;
        List<String> returningCookies;

        // StartSession
        command = AvailableCommands.StartSession;
        method = Method.GET;
        commandType = CommandType.Session;
        url = params -> "sessions/start/";
        paramFormats = null;
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_START_SESSION.class);}};
        requiredCookies = null;
        returningCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Login
        command = AvailableCommands.Login;
        method = Method.POST;
        commandType = CommandType.Session;
        url = params -> "sessions/login/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_LOGIN.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(RES_LOGIN.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Register
        command = AvailableCommands.Register;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = params -> "users/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_REGISTER_USER.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = new ArrayList<String>() {{add(SessionCookie);}};
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // EmailCheck
        command = AvailableCommands.EmailCheck;
        method = Method.POST;
        commandType = CommandType.Query;
        url = params -> "users/email/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_EMAIL_CHECK.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // UsernameCheck
        command = AvailableCommands.UsernameCheck;
        method = Method.POST;
        commandType = CommandType.Query;
        url = params -> "users/public_name/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_PUBLIC_NAME_CHECK.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(RES_PUBLIC_NAME_CHECK.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // UserProfile
        command = AvailableCommands.UserProfile;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = params -> {
            String resultURL = "profiles/";
            URL_GET_USER_PROFILE urlData = null;
            for (Object p : params) {
                if (p instanceof URL_GET_USER_PROFILE) {
                    urlData = (URL_GET_USER_PROFILE) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_GET_USER_PROFILE expected.");
            }

            if ((urlData.getPublicName() == null) || (urlData.getPublicName().isEmpty())) {
                throw new IllegalArgumentException("Must specify public_name.");
            }

            if (urlData.getType() == null) {
                throw new IllegalArgumentException("Must specify search type.");
            }

            resultURL += urlData.getPublicName() + "/" + urlData.getType().toString() + "/";

            String queryURL = "";

            // Query parameters
            if (urlData.getPackage() != null) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "package=" + urlData.getPackage().toString();
            }

            // End query parameters

            if (!queryURL.isEmpty()) {
                resultURL += queryURL;
            }

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_GET_USER_PROFILE.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_GET_USER_PROFILE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // UpdateProfile
        command = AvailableCommands.UpdateProfile;
        method = Method.PATCH;
        commandType = CommandType.ImmediateResponsePush;
        url =params -> {
            String resultURL = "profiles/";
            URL_UPDATE_USER_PROFILE urlData = null;
            for (Object p : params) {
                if (p instanceof URL_UPDATE_USER_PROFILE) {
                    urlData = (URL_UPDATE_USER_PROFILE) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_UPDATE_USER_PROFILE expected.");
            }

            if ((urlData.getPublicName() == null) || (urlData.getPublicName().isEmpty())) {
                throw new IllegalArgumentException("Must specify public_name.");
            }

            resultURL += urlData.getPublicName() + "/";

            return resultURL;
        };;
        paramFormats = new ArrayList<Class<?>>() {{add(URL_UPDATE_USER_PROFILE.class);}};
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_UPDATE_USER_PROFILE.class);}};
        returningFormats = null;
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Explore
        command = AvailableCommands.Explore;
        method = Method.GET;
        commandType = CommandType.Query;
        url = params -> {
            String resultURL = "hives/";
            URL_EXPLORE urlData = null;
            for (Object p : params) {
                if (p instanceof URL_EXPLORE) {
                    urlData = (URL_EXPLORE) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_EXPLORE expected.");
            }

            if ((urlData.getSort() == null) || (urlData.getSort().isEmpty())) {
                throw new IllegalArgumentException("Must specify sort type.");
            }

            resultURL += urlData.getSort() + "/";

            String queryURL = "";

            // Pagination
            if ((urlData.getStart() != null) && (!urlData.getStart().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "start=" + urlData.getStart();
            }

            if ((urlData.getEnd() != null) && (!urlData.getEnd().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "end=" + urlData.getEnd();
            }

            if ((urlData.getElements() != null) && (urlData.getElements() > 0)) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "elements=" + urlData.getElements().toString();
            }
            // End pagination

            if ((urlData.getCountry() != null) && (!urlData.getCountry().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "country=" + urlData.getCountry();
            }

            if ((urlData.getRegion() != null) && (!urlData.getRegion().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "region=" + urlData.getRegion();
            }

            if ((urlData.getCity() != null) && (!urlData.getCity().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "city=" + urlData.getCity();
            }

            if ((urlData.getCoordinates() != null) && (!urlData.getCoordinates().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "coordinates=" + urlData.getCoordinates();
            }

            if ((urlData.getSearch_string() != null) && (!urlData.getSearch_string().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "search_string=" + urlData.getSearch_string();
            }

            if (urlData.getInclude_subscribed() != null) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "include_subscribed=" + urlData.getInclude_subscribed().toString();
            }

            if ((urlData.getTags() != null) && (!urlData.getTags().isEmpty())) {
                for (String tag : urlData.getTags()) {
                    if ((tag != null) && (!tag.isEmpty())) {
                        queryURL += ((queryURL.isEmpty())?"?":"&") + "tags=" + tag;
                    }
                }
            }

            if (!queryURL.isEmpty()) {
                resultURL += queryURL;
            }


            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_EXPLORE.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_EXPLORE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // Join
        command = AvailableCommands.Join;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = params -> {
            String resultURL = "profiles/";
            URL_JOIN urlData = null;
            for (Object p : params) {
                if (p instanceof URL_JOIN) {
                    urlData = (URL_JOIN) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_JOIN expected.");
            }

            if ((urlData.getPublic_name() == null) || (urlData.getPublic_name().isEmpty())) {
                throw new IllegalArgumentException("Must specify public_name.");
            }

            resultURL += urlData.getPublic_name() + "/hives/";

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_JOIN.class);}};
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_JOIN.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(RES_JOIN.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // SendMessage
        command = AvailableCommands.SendMessage;
        method = Method.POST;
        commandType = CommandType.ForcePush;
        url = params -> {
            String resultURL = "chats/";
            URL_SEND_MESSAGE urlData = null;
            for (Object p : params) {
                if (p instanceof URL_SEND_MESSAGE) {
                    urlData = (URL_SEND_MESSAGE) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_SEND_MESSAGE expected.");
            }

            if ((urlData.getChat_id() == null) || (urlData.getChat_id().isEmpty())) {
                throw new IllegalArgumentException("Must specify chat_id.");
            }

            resultURL += urlData.getChat_id() + "/messages/";

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_SEND_MESSAGE.class);}};
        inputFormats = new ArrayList<Class<?>>() {{add(REQ_SEND_MESSAGE.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(RES_SEND_MESSAGE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // GetMessages //TODO: Add optional parameters
        command = AvailableCommands.GetMessages;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = params -> {
            String resultURL = "chats/";
            URL_CHAT_MESSAGES urlData = null;
            for (Object p : params) {
                if (p instanceof URL_CHAT_MESSAGES) {
                    urlData = (URL_CHAT_MESSAGES) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_CHAT_MESSAGES expected.");
            }

            if ((urlData.getChat_id() == null) || (urlData.getChat_id().isEmpty())) {
                throw new IllegalArgumentException("Must specify chat_id.");
            }

            resultURL += urlData.getChat_id() + "/messages/";

            String queryURL = "";

            // Pagination
            if ((urlData.getStart() != null) && (!urlData.getStart().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "start=" + urlData.getStart();
            }

            if ((urlData.getEnd() != null) && (!urlData.getEnd().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "end=" + urlData.getEnd();
            }

            if ((urlData.getElements() != null) && (urlData.getElements() > 0)) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "elements=" + urlData.getElements().toString();
            }
            // End pagination

            if (!queryURL.isEmpty()) {
                resultURL += queryURL;
            }

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_CHAT_MESSAGES.class); }};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_CHAT_MESSAGE.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // ChatInfo
        command = AvailableCommands.ChatInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = params -> {
            String resultURL = "chats/";
            URL_CHAT_INFO urlData = null;
            for (Object p : params) {
                if (p instanceof URL_CHAT_INFO) {
                    urlData = (URL_CHAT_INFO) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_CHAT_INFO expected.");
            }

            if ((urlData.getChat_id() == null) || (urlData.getChat_id().isEmpty())) {
                throw new IllegalArgumentException("Must specify chat_id.");
            }

            resultURL += urlData.getChat_id() + "/";

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_CHAT_INFO.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_CHAT_INFO.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // ChatList
        command = AvailableCommands.ChatList;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = params -> {
            String resultURL = "profiles/";
            URL_PROFILE_CHAT_LIST urlData = null;
            for (Object p : params) {
                if (p instanceof URL_PROFILE_CHAT_LIST) {
                    urlData = (URL_PROFILE_CHAT_LIST) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_PROFILE_CHAT_LIST expected.");
            }

            if ((urlData.getProfileID() == null) || (urlData.getProfileID().isEmpty())) {
                throw new IllegalArgumentException("Must specify profile_id.");
            }

            resultURL += urlData.getProfileID() + "/chats/";

            String queryURL = "";

            // Pagination
            if ((urlData.getStart() != null) && (!urlData.getStart().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "start=" + urlData.getStart();
            }

            if ((urlData.getEnd() != null) && (!urlData.getEnd().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "end=" + urlData.getEnd();
            }

            if ((urlData.getElements() != null) && (urlData.getElements() > 0)) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "elements=" + urlData.getElements().toString();
            }
            // End pagination

            if (!queryURL.isEmpty()) {
                resultURL += queryURL;
            }

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_PROFILE_CHAT_LIST.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_PROFILE_CHAT_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);


        // HiveInfo
        command = AvailableCommands.HiveInfo;
        method = Method.GET;
        commandType = CommandType.Pull;
        url = params -> {
            String resultURL = "hives/";
            URL_HIVE_INFO urlData = null;
            for (Object p : params) {
                if (p instanceof URL_HIVE_INFO) {
                    urlData = (URL_HIVE_INFO) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_HIVE_INFO expected.");
            }

            if ((urlData.getSlug() == null) || (urlData.getSlug().isEmpty())) {
                throw new IllegalArgumentException("Must specify slug.");
            }

            resultURL += urlData.getSlug() + "/";

            return resultURL;
        };;
        paramFormats = new ArrayList<Class<?>>() {{add(URL_HIVE_INFO.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_HIVE_INFO.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // HiveUsers
        command = AvailableCommands.HiveUsers;
        method = Method.GET;
        commandType = CommandType.Query;
        url = params -> {
            String resultURL = "hives/";
            URL_HIVE_USERS_LIST urlData = null;
            for (Object p : params) {
                if (p instanceof URL_HIVE_USERS_LIST) {
                    urlData = (URL_HIVE_USERS_LIST) p;
                    break;
                }
            }
            if (urlData == null) {
                throw new IllegalArgumentException("URL_HIVE_USERS_LIST expected.");
            }

            if ((urlData.getHive_slug() == null) || (urlData.getHive_slug().isEmpty())) {
                throw new IllegalArgumentException("Must specify slug.");
            }

            if (urlData.getSort() == null) {
                throw new IllegalArgumentException("Must specify search type.");
            }

            resultURL += urlData.getHive_slug() + "/users/" + urlData.getSort().toString() + "/";

            String queryURL = "";

            // Pagination
            if ((urlData.getStart() != null) && (!urlData.getStart().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "start=" + urlData.getStart();
            }

            if ((urlData.getEnd() != null) && (!urlData.getEnd().isEmpty())) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "end=" + urlData.getEnd();
            }

            if ((urlData.getElements() != null) && (urlData.getElements() > 0)) {
                queryURL += ((queryURL.isEmpty())?"?":"&") + "elements=" + urlData.getElements().toString();
            }
            // End pagination

            if (!queryURL.isEmpty()) {
                resultURL += queryURL;
            }

            return resultURL;
        };
        paramFormats = new ArrayList<Class<?>>() {{add(URL_HIVE_USERS_LIST.class);}};
        inputFormats = null;
        returningFormats = new ArrayList<Class<?>>() {{add(RES_HIVE_USERS_LIST.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);

        // CreateHive
        command = AvailableCommands.CreateHive;
        method = Method.POST;
        commandType = CommandType.ImmediateResponsePush;
        url = params -> "hives/";
        paramFormats = null;
        inputFormats = new ArrayList<Class<?>>() {{add(RES_HIVE_INFO.class);}};
        returningFormats = new ArrayList<Class<?>>() {{add(RES_HIVE_INFO.class);}};
        requiredCookies = new ArrayList<String>() {{add(CSRFTokenCookie); add(SessionCookie);}};
        returningCookies = null;
        CommandDefinition.AddCommandDefinition(command, method, commandType, url, paramFormats, inputFormats, returningFormats, requiredCookies, returningCookies);
    }

    /**********************************************************************************/

    private static void AddCommandDefinition(AvailableCommands command,
                                             Method method,
                                             CommandType commandType,
                                             URLGenerator url,
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
    private final URLGenerator url;

    private List<Class<?>> inputFormats;
    private List<Class<?>> paramFormats;
    private List<Class<?>> returningFormats;

    private List<String> requiredCookies;
    private List<String> returningCookies;

    private CommandDefinition(AvailableCommands command,
                              Method method,
                              CommandType commandType,
                              URLGenerator url) {
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
    public String getUrl(List<Object> paramFormats) {
        return this.url.generateURL(paramFormats);
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

    interface URLGenerator {
        public String generateURL(List<Object> paramFormats);
    }
}
