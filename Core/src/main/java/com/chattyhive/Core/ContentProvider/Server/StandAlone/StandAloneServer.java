package com.chattyhive.Core.ContentProvider.Server.StandAlone;

import com.chattyhive.Core.ContentProvider.OSStorageProvider.LocalDataBaseInterface;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.IOrigin;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Chats.ChatKind;
import com.chattyhive.Core.BusinessObjects.Chats.Conversation;
import com.chattyhive.Core.BusinessObjects.Chats.Hive;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.MessageContent;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.DataProvider;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.Formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.BASIC_PUBLIC_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.CHAT;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_ID;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_LIST;
import com.chattyhive.Core.ContentProvider.Formats.CHAT_SYNC;
import com.chattyhive.Core.ContentProvider.Formats.COMMON;
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
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_CONTENT;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_INTERVAL;
import com.chattyhive.Core.ContentProvider.Formats.MESSAGE_LIST;
import com.chattyhive.Core.ContentProvider.Formats.PRIVATE_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.PROFILE_ID;
import com.chattyhive.Core.ContentProvider.Formats.PUBLIC_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.USERNAME;
import com.chattyhive.Core.ContentProvider.Formats.USER_EMAIL;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE;
import com.chattyhive.Core.ContentProvider.Formats.USER_PROFILE_LIST;
import com.chattyhive.Core.Util.Data.DataTable;
import com.chattyhive.Core.Util.RandomString;
import com.chattyhive.Core.Util.Events.CommandCallbackEventArgs;
import com.chattyhive.Core.Util.Events.ConnectionEventArgs;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;
import com.chattyhive.Core.Util.Events.FormatReceivedEventArgs;
import com.chattyhive.Core.Util.Events.PubSubChannelEventArgs;
import com.chattyhive.Core.Util.Formatters.DateFormatter;
import com.chattyhive.Core.Util.Formatters.TimestampFormatter;
import com.google.gson.JsonParser;

import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 * Created by Jonathan on 15/09/2014.
 */
public class StandAloneServer implements IOrigin {

    private Boolean StandAloneServerOutput = false;

    private LocalDataBaseInterface standAloneServerDB;

    private RandomString randomString = new RandomString(10);

    private String CSRFTokenCookie = "csrftoken";
    private String SessionIDCookie = "sessionid";

    private ArrayList<String> CSRFTokens;
    private HashMap<String,String> CSRFTokenSessionID;
    private HashMap<String,String> SessionIDUser;

    private String[] Words = new String[]{"Lorem","ipsum","dolor","sit","amet","consectetuer","adipiscing","elit. Aenean","commodo","ligula","eget","dolor. Aenean","massa. Cum","sociis","natoque","penatibus","et","magnis","dis","parturient","montes,","nascetur","ridiculus","mus. Donec","quam","felis,","ultricies","nec,","pellentesque","eu,","pretium","quis,","sem. Nulla","consequat","massa","quis","enim. Donec","pede","justo","fringilla","vel,","aliquet","nec","vulputate","eget,","arcu. In","enim","justo","rhoncus","ut,","imperdiet","a","venenatis","vitae","justo","nullam","dictum","felis","eu","pede","mollis","pretium. Integer","tincidunt. Cras","dapibus","vivamus","elementum","semper","nisi. Aenean","vulputate","eleifend","tellus. Aenean","leo","ligula,","porttitor","eu,","consequat","vitae","eleifend","ac","enim. Aliquam","lorem","ante","dapibus","in,","viverra","quis,","feugiat","a,","tellus. Phasellus","viverra","nulla","ut","metus","varius","laoreet. Quisque","rutrum","aenean","imperdiet","etiam","ultricies","nisi","vel","augue. Curabitur","ullamcorper","ultricies","nisi","nam","eget","dui. Etiam","rhoncus","maecenas","tempus,","tellus","eget","condimentum","rhoncus","sem","quam","semper","libero,","sit","amet","adipiscing","sem","neque","sed","ipsum. Nam","quam","nunc,","blandit","vel","luctus","pulvinar","hendrerit","id","lorem. Maecenas","nec","odio","et","ante","tincidunt","tempus. Donec","vitae","sapien","ut","libero","venenatis","faucibus. Nullam","quis,","ante. Etiam","sit","amet","orci","eget","eros","faucibus","tincidunt","duis","leo","sed","fringilla","mauris","sit","amet","nibh. Donec","sodales","sagittis","magna. Sed","consequat","leo","eget","bibendum","sodales,","augue","velit","cursus","nunc"};
    private String[] Images = new String[]{"file_chat_001.jpg","file_chat_002.jpg","file_chat_003.jpg","file_chat_004.jpg","file_chat_005.jpg","file_chat_006.jpg","file_chat_007.jpg","file_chat_008.jpg","file_chat_009.jpg","file_chat_010.jpg","file_chat_011.jpg","file_chat_012.jpg","file_chat_013.jpg","file_chat_014.jpg","file_chat_015.jpg","file_chat_016.jpg","file_chat_017.jpg","file_chat_018.jpg","file_chat_019.jpg","file_chat_020.jpg","file_chat_021.jpg","file_chat_022.jpg","file_chat_023.jpg","file_chat_024.jpg","file_chat_025.jpg","file_chat_026.jpg","file_chat_027.jpg","file_chat_028.jpg","file_chat_029.jpg","file_chat_030.jpg","file_chat_031.jpg","file_chat_032.jpg","file_chat_033.jpg","file_chat_034.jpg","file_chat_035.jpg","file_chat_036.jpg","file_chat_037.jpg","file_chat_038.jpg","file_chat_039.jpg","file_chat_040.jpg"};

    public StandAloneServer (LocalDataBaseInterface standAloneServerDB) {
        this.standAloneServerDB = standAloneServerDB;

        CSRFTokens = new ArrayList<String>();
        CSRFTokenSessionID = new HashMap<String, String>();
        SessionIDUser = new HashMap<String, String>();

        if (StaticParameters.StandAloneContinuousRandomMessage) {
            final Random random = new Random();

            Thread timer = new Thread() {
                @Override
                public void run() {
                    Boolean running = true;

                    while (running) {
                        try {
                            sleep(random.nextInt(599500)+500);
                        } catch (InterruptedException e) {
                            running = false;
                            continue;
                        }
                        sendRandomMessage(random,true);
                    }
                }
            };

            timer.start();
        }
    }

    private void sendRandomMessage(Random random, Boolean notify) {
        String active_users = "";
        for (String user : SessionIDUser.values())
            active_users = active_users.concat((active_users.isEmpty())?"":",").concat("'").concat(user).concat("'");

        String public_name = "";

        if (active_users.isEmpty())
            public_name = standAloneServerDB.simpleQuerySQL("SELECT public_name FROM user ORDER BY RANDOM() LIMIT 1").toString();
        else
            public_name = standAloneServerDB.simpleQuerySQL("SELECT public_name FROM user WHERE public_name not in ("+active_users+") ORDER BY RANDOM() LIMIT 1").toString();

        if (public_name.isEmpty())
            return;

        DataTable randomChat = standAloneServerDB.tableQuerySQL("SELECT * FROM chat WHERE (hive_chat=1 AND hive in (SELECT hive FROM hive_subscriptions WHERE (profile='"+public_name+"') AND (join_date <= DATE()) AND ((leave_date is null) OR (leave_date > DATE())))) OR (hive_chat=0 AND channel_unicode in (SELECT chat FROM chat_subscriptions WHERE (profile='"+public_name+"') AND(join_date <= DATE()) AND ((leave_date is null) OR (leave_date > DATE())))) ORDER BY RANDOM() LIMIT 1");

        String channel_unicode = randomChat.Rows(0).get("channel_unicode").toString();
        String hive_url = randomChat.Rows(0).get("hive").toString();
        boolean isHive = randomChat.Rows(0).get("hive_chat").toString().equalsIgnoreCase("1");

        if ((channel_unicode == null) || (channel_unicode.isEmpty()) || ((isHive) && ((hive_url == null) || (hive_url.isEmpty()))))
            return;



            String content_type = "";
            String messageContent = "";

            if ((Images.length > 0) && (random.nextDouble() <= ((double)1/15))) {
                //Send Image
                content_type = "IMAGE";
                messageContent = Images[random.nextInt(Images.length)];
            } else {
                //Send text
                content_type = "TEXT";
                int messageLength = random.nextInt(30) + 1;
                for (int wordCount = 0; wordCount < messageLength; wordCount++)
                    messageContent = messageContent.concat(((messageContent.isEmpty())?"":" ")).concat(Words[random.nextInt(Words.length)]);
            }

            Date timestamp = new Date();


            MESSAGE message = new MESSAGE();
            message.CHANNEL_UNICODE = channel_unicode;
            message.CONFIRMED = false;
            message.TIMESTAMP = timestamp;
            message.SERVER_TIMESTAMP = timestamp;
            message.USER_ID = public_name;
            message.CONTENT = new MESSAGE_CONTENT();
            message.CONTENT.CONTENT_TYPE = content_type;
            message.CONTENT.CONTENT = messageContent;
            sendMessage(message,notify);

    }

    private MESSAGE_ACK sendMessage(MESSAGE message, Boolean notify) {
        if (message.SERVER_TIMESTAMP == null)
            message.SERVER_TIMESTAMP = new Date();

        String insertMessage = "INSERT INTO message (profile,chat,user_date,server_date,content_type,message_content) SELECT '"+message.USER_ID+"' as profile, '"+message.CHANNEL_UNICODE+"' as chat, '"+TimestampFormatter.toDbString(message.TIMESTAMP)+"' as user_date, '"+TimestampFormatter.toDbString(message.SERVER_TIMESTAMP)+"' as server_date, content_type_code as content_type, '"+message.CONTENT.CONTENT+"' as message_content FROM content_type WHERE content_type_name='"+message.CONTENT.CONTENT_TYPE+"';";

        String findMessageID = "SELECT message_id FROM message WHERE profile='"+message.USER_ID+"' AND chat='"+message.CHANNEL_UNICODE+"' AND user_date='"+TimestampFormatter.toDbString(message.TIMESTAMP)+"' AND server_date='"+TimestampFormatter.toDbString(message.SERVER_TIMESTAMP)+"' AND content_type=(SELECT content_type_code FROM content_type WHERE content_type_name='"+message.CONTENT.CONTENT_TYPE+"') AND message_content='"+message.CONTENT.CONTENT+"'";

        MESSAGE_ACK result = new MESSAGE_ACK();
        result.SERVER_TIMESTAMP = message.SERVER_TIMESTAMP;

        standAloneServerDB.executeSQL(insertMessage);

        String id = standAloneServerDB.simpleQuerySQL(findMessageID).toString();

        if ((id == null) || (id.isEmpty()))
            result = null;
        else
            result.ID = id;


        if (notify) {
            //TODO: select users from chat who are active
            //TODO: send notification to selected users
        }

        return result;
    }

    private static Chat createChat(Hive hive, String creationDate, String... members) {
        ChatKind chatKind = ChatKind.PUBLIC_SINGLE;
        if ((hive == null) && (members.length > 2))
            chatKind = ChatKind.PRIVATE_GROUP;
        else if ((hive == null) && (members.length < 3))
            chatKind = ChatKind.PRIVATE_SINGLE;
        else if ((hive != null) && (members.length > 2))
            chatKind = ChatKind.PUBLIC_GROUP;
        else if ((hive != null) && (members.length < 3))
            chatKind = ChatKind.PUBLIC_SINGLE;

        Chat chat = new Chat(chatKind,hive);
        chat.setChannelUnicode(randomString.nextString());
        chat.setPusherChannel(String.format("presence-%s", chat.getChannelUnicode()));
        chat.setCreationDate(DateFormatter.fromShortHumanReadableString(creationDate));
        Chats.put(chat.getChannelUnicode(), chat);
        for (String member : members) {
            chat.addMember(LoginUser.get(member));
            subscribeChat(member, chat.getChannelUnicode());
        }

        return chat;
    }
    private static void subscribeHive(String userLogin, String hiveNameURL) {
        if (!LoginUser.containsKey(userLogin)) return;
        if (!Hives.containsKey(hiveNameURL)) return;

        if (!HiveUserSubscriptions.containsKey(hiveNameURL))
            HiveUserSubscriptions.put(hiveNameURL,new ArrayList<String>());
        if (!UserHiveSubscriptions.containsKey(userLogin))
            UserHiveSubscriptions.put(userLogin,new ArrayList<String>());

        HiveUserSubscriptions.get(hiveNameURL).add(userLogin);
        UserHiveSubscriptions.get(userLogin).add(hiveNameURL);

        Hives.get(hiveNameURL).incSubscribedUsers(1);

        subscribeChat(userLogin,Hives.get(hiveNameURL).getPublicChat().getChannelUnicode());
    }
    private static void subscribeChat(String userLogin, String chatChannelUnicode) {
        if (!LoginUser.containsKey(userLogin)) return;
        if (!Chats.containsKey(chatChannelUnicode)) return;

        if (!ChatUserSubscriptions.containsKey(chatChannelUnicode))
            ChatUserSubscriptions.put(chatChannelUnicode,new ArrayList<String>());
        if (!UserChatSubscriptions.containsKey(userLogin))
            UserChatSubscriptions.put(userLogin,new ArrayList<String>());

        ChatUserSubscriptions.get(chatChannelUnicode).add(userLogin);
        UserChatSubscriptions.get(userLogin).add(chatChannelUnicode);
    }
    private static Hive createHive(String name, String hiveImage,String category, String description, String[] languages, String... tags) {
        return createHive(name,hiveImage,category,description,new Date(Math.round((new Random()).nextDouble() * (new Date()).getTime())),languages,tags);
    }
    private static Hive createHive(String name, String hiveImage,String category, String description, Date creationDate, String[] languages, String... tags) {
        Hive hive = new Hive(name,randomString.nextString());
        Chat publicChat = new Chat(ChatKind.HIVE,hive);

        hive.setImageURL(hiveImage);
        hive.setCategory(category);
        hive.setDescription(description);
        hive.setPublicChat(publicChat);
        hive.setChatLanguages(languages);
        hive.setTags(tags);

        publicChat.setChannelUnicode(hive.getNameUrl());
        publicChat.setPusherChannel(String.format("presence-%s",hive.getNameUrl()));
        publicChat.setCreationDate(creationDate);

        Hives.put(hive.getNameUrl(), hive);
        Chats.put(publicChat.getChannelUnicode(),publicChat);

        return hive;
    }
    private static User createUser(String email, String firstName, String lastName, String publicName, String color, String avatarURL, String profileURL, String birthdate, String location, String sex, Boolean privateShowAge, Boolean publicShowAge, Boolean publicShowSex, Boolean publicShowLocation, String... languages) {
        User user = new User(email);
        user.setUserID(publicName);
        user.getUserPrivateProfile().setFirstName(firstName);
        user.getUserPrivateProfile().setLastName(lastName);
        user.getUserPrivateProfile().setImageURL(profileURL);
        user.getUserPrivateProfile().setShowAge(privateShowAge);
        user.getUserPublicProfile().setShowSex(publicShowSex);
        user.getUserPublicProfile().setShowLocation(publicShowLocation);
        user.getUserPublicProfile().setShowAge(publicShowAge);
        user.getUserPublicProfile().setPublicName(publicName);
        user.getUserPublicProfile().setImageURL(avatarURL);
        user.getUserPublicProfile().setColor(color);
        user.getUserPublicProfile().setBirthdate(DateFormatter.fromShortHumanReadableString(birthdate));
        user.getUserPublicProfile().setLocation(location);
        if (languages != null)
            user.getUserPublicProfile().setLanguages(Arrays.asList(languages));
        user.getUserPublicProfile().setSex(sex);
        user.getUserPublicProfile().setID(user.getUserID());
        user.getUserPrivateProfile().setLanguages(user.getUserPublicProfile().getLanguages());
        user.getUserPrivateProfile().setLocation(user.getUserPublicProfile().getLocation());
        user.getUserPrivateProfile().setBirthdate(user.getUserPublicProfile().getBirthdate());
        user.getUserPrivateProfile().setSex(user.getUserPublicProfile().getSex());
        user.getUserPrivateProfile().setID(user.getUserPublicProfile().getID());

        return user;
    }
    private static void subscribeUser(String userLogin, String friendLogin) {
        if (!UserFriendList.containsKey(userLogin))
            UserFriendList.put(userLogin,new ArrayList<String>());
        if (!UserFriendList.containsKey(friendLogin))
            UserFriendList.put(friendLogin,new ArrayList<String>());

        UserFriendList.get(userLogin).add(friendLogin);
        UserFriendList.get(friendLogin).add(userLogin);
    }

    public static Boolean ExecuteCommand(Server server, AvailableCommands command, EventHandler<CommandCallbackEventArgs> Callback,Object CallbackAdditionalData, int retryCount, Format... formats) {
        if (retryCount >= 3) return false;

        Boolean result = false;

        try {

            AbstractMap.SimpleEntry<Integer,String> response = null;

            switch (command) {
                case StartSession:
                    response = StartSession(server,formats);
                    break;
                case Login:
                    response = Login(server,formats);
                    break;
                case Register:
                    response = Register(server,formats);
                    break;
                case EmailCheck:
                    response = EmailCheck(server, formats);
                    break;
                case UsernameCheck:
                    response = UsernameCheck(server, formats);
                    break;
                case Explore:
                    response = Explore(server, formats);
                    break;
                case Join:
                    response = Join(server, formats);
                    break;
                case SendMessage:
                    response = SendMessage(server, formats);
                    break;
                case GetMessages:
                    response = GetMessages(server, formats);
                    break;
                case LocalProfile:
                    response = LocalProfile(server, formats);
                    break;
                case UpdateProfile:
                    response = UpdateProfile(server, formats);
                    break;
                case ChatInfo:
                    response = ChatInfo(server, formats);
                    break;
                case ChatList:
                    response = ChatList(server, formats);
                    break;
                case UserProfile:
                    response = UserProfile(server, formats);
                    break;
                case HiveInfo:
                    response = HiveInfo(server, formats);
                    break;
                case HiveUsers:
                    response = HiveUsers(server,formats);
                    break;
                case CreateHive:
                    response = CreateHive(server, formats);
                    break;
            }

            int responseCode = (response != null)?response.getKey():500;
            String responseBody = (response != null)?response.getValue():"";

            if (StandAloneServerOutput)
                System.out.println(String.format("StandAloneServer -> Request: %s\nCode: %d\n%s", command.toString(), responseCode, responseBody));

            Format[] receivedFormats = null;

            if (responseCode == -1) {
                result = false;
                DataProvider.setConnectionAvailable(false);
            } else if (responseCode == 200) {

                String preparedResponseBody = responseBody;//.replace("\\\"","\"").replace("\"{","{").replace("}\"","}").replaceAll("\"PROFILE\": \"(.*?)\"","\"PROFILE\": {\"PUBLIC_NAME\": \"$1\"}");
                receivedFormats = Format.getFormat(new JsonParser().parse(preparedResponseBody));

                if (command == AvailableCommands.StartSession) {
                    if (server.CsrfTokenChanged != null)
                        server.CsrfTokenChanged.fire(server, EventArgs.Empty());
                    return true;
                }

                for (Format format : receivedFormats)
                    if (format instanceof COMMON) {
                        if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                            result = true;
                            if (Callback != null)
                                Callback.Invoke(server, new CommandCallbackEventArgs(command,Arrays.asList(receivedFormats), (formats!=null)?Arrays.asList(formats):null, CallbackAdditionalData));
                            else if (server.responseEvent != null)
                                server.responseEvent.fire(server, new FormatReceivedEventArgs(Arrays.asList(receivedFormats)));

                            if ((command == AvailableCommands.Login) && (server.onConnected != null)) {
                                server.onConnected.fire(server, new ConnectionEventArgs(true));
                                return true;
                            } else if (command == AvailableCommands.Login)
                                return true;

                        } else if (((COMMON) format).STATUS.equalsIgnoreCase("SESSION EXPIRED")) {
                            //server.getServerUser().setStatus(ServerStatus.EXPIRED);
                            server.Login();
                            result = ExecuteCommand(server, command, Callback, CallbackAdditionalData, retryCount + 1, formats);
                        } else {
                            //TODO: Check COMMON for operation Error and set result here.
                            server.getUserSession().setStatus(ServerStatus.ERROR);
                            System.out.println(String.format("ERROR. Code: %d. Request command: %s. Request URL: %s. Request body: %s",((COMMON) format).ERROR,command.toString(), Command.GetCommand(command).getUrl(formats), Command.GetCommand(command).getBodyData(formats)));
                            result = true;
                        }
                        break;
                    }
            } else if (responseCode == 403) { //CSRF-Token error.
                server.StartSession();
                result = ExecuteCommand(server, command, Callback, CallbackAdditionalData, retryCount + 1, formats);
            }

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static HttpCookie checkCSRFCookie(String appName) {
        HttpCookie csrfCookie = null;
        try {
            CookieStore cookieStore = ((CookieManager) CookieHandler.getDefault()).getCookieStore();
            List<HttpCookie> cookies = cookieStore.get(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol, appName, StaticParameters.DefaultServerHost)));


            for (HttpCookie cookie : cookies)
                if (cookie.getName().equalsIgnoreCase(CSRFTokenCookie))
                    csrfCookie = cookie;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return csrfCookie;
    }

    private static User checkSessionCookie(HttpCookie CSRFCookie, String appName) {
        User user = null;

        try {
            CookieStore cookieStore = ((CookieManager) CookieHandler.getDefault()).getCookieStore();
            List<HttpCookie> cookies = cookieStore.get(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol, appName, StaticParameters.DefaultServerHost)));
            HttpCookie sessionCookie = null;

            for (HttpCookie cookie : cookies)
                if (cookie.getName().equalsIgnoreCase(SessionIDCookie))
                    sessionCookie = cookie;

            if ((sessionCookie != null) && (!sessionCookie.hasExpired()) && (CSRFTokenSessionID.containsKey(CSRFCookie.getValue())) && (CSRFTokenSessionID.get(CSRFCookie.getValue()).equalsIgnoreCase(sessionCookie.getValue())) && (SessionIDUser.containsKey(sessionCookie.getValue()))) {
                user = SessionIDUser.get(sessionCookie.getValue());
                //System.out.println(String.format("SessionID: %s, UserID: %s",sessionCookie.getValue(),user.getUserID()));
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return user;
    }

    private static AbstractMap.SimpleEntry<Integer, String> StartSession(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        String CSRFToken = randomString.nextString();

        CSRF_TOKEN csrf_token = new CSRF_TOKEN();
        csrf_token.CSRF = CSRFToken;

        HttpCookie cookie = new HttpCookie(CSRFTokenCookie,CSRFToken);
        cookie.setMaxAge(Long.MAX_VALUE);

        CookieStore cookieStore = ((CookieManager)CookieHandler.getDefault()).getCookieStore();
        try {
            cookieStore.add(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol,server.getAppName(),StaticParameters.DefaultServerHost)),cookie);
            responseCode = 200;
            responseBody = csrf_token.toJSON().toString();
            CSRFTokens.add(CSRFToken);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            responseCode = -1;
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> Login(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        LOGIN login = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof LOGIN)
                    login = (LOGIN)format;

        COMMON common = new COMMON();

        if (login == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            try {
                CookieStore cookieStore = ((CookieManager) CookieHandler.getDefault()).getCookieStore();
                HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

                if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                    responseCode = 403;
                else {
                    responseCode = 200;
                    if ((LoginUser.containsKey(login.USER)) && (LoginPassword.containsKey(LoginUser.get(login.USER).getEmail())) && (LoginPassword.get(LoginUser.get(login.USER).getEmail()).equals(login.PASS))) {
                        //Login OK
                        String SessionID = randomString.nextString();
                        HttpCookie sessionCookie = new HttpCookie(SessionIDCookie,SessionID);
                        sessionCookie.setMaxAge(Long.MAX_VALUE);
                        cookieStore.add(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol,server.getAppName(),StaticParameters.DefaultServerHost)),sessionCookie);
                        common.STATUS = "OK";

                        CSRFTokenSessionID.put(csrfCookie.getValue(),SessionID);
                        SessionIDUser.put(SessionID,LoginUser.get(login.USER));

                    } else {
                        common.STATUS = "ERROR";
                        common.ERROR = -2;
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        if ((responseCode != null) && (responseCode == 200))
            responseBody = common.toJSON().toString();

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:500,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> Register(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        LOCAL_USER_PROFILE local_user_profile = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof LOCAL_USER_PROFILE)
                    local_user_profile = (LOCAL_USER_PROFILE)format;

        COMMON common = new COMMON();

        if (local_user_profile == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            try {
                CookieStore cookieStore = ((CookieManager) CookieHandler.getDefault()).getCookieStore();
                HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

                if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                    responseCode = 403;
                else {
                    responseCode = 200;
                    if ((LoginUser.containsKey(local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME)) || (LoginPassword.containsKey(local_user_profile.EMAIL))) {
                        //Error
                        common.STATUS = "ERROR";
                        common.ERROR = (LoginUser.containsKey(local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME))?-3:-4;
                    } else {
                        //Register OK
                        String[] languages = null;
                        if (local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE != null)
                            languages = local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE.toArray(new String[local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE.size()]);

                        User user = createUser(local_user_profile.EMAIL,local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME,local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME,local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME,"#808080",null,null,DateFormatter.toShortHumanReadableString(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE),local_user_profile.USER_PRIVATE_PROFILE.LOCATION,local_user_profile.USER_PRIVATE_PROFILE.SEX,local_user_profile.USER_PRIVATE_PROFILE.PRIVATE_SHOW_AGE,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_AGE,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_SEX,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_LOCATION,languages);
                        LoginUser.put(user.getUserPublicProfile().getPublicName(), user);
                        LoginPassword.put(user.getEmail(), local_user_profile.PASS);

                        String SessionID = randomString.nextString();
                        HttpCookie sessionCookie = new HttpCookie(SessionIDCookie,SessionID);
                        sessionCookie.setMaxAge(Long.MAX_VALUE);
                        cookieStore.add(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol,server.getAppName(),StaticParameters.DefaultServerHost)),sessionCookie);
                        common.STATUS = "OK";

                        CSRFTokenSessionID.put(csrfCookie.getValue(),SessionID);
                        SessionIDUser.put(SessionID,user);
                    }
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
                responseCode = -1;
            }
        }

        if ((responseCode != null) && (responseCode == 200))
            responseBody = common.toJSON().toString();

        return new AbstractMap.SimpleEntry<Integer, String>((responseCode != null) ? responseCode : -1, (responseBody != null) ? responseBody : "");
    }

    private static AbstractMap.SimpleEntry<Integer, String> EmailCheck(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        USER_EMAIL user_email = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof USER_EMAIL)
                    user_email = (USER_EMAIL)format;

        COMMON common = new COMMON();

        if (user_email == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                String userEmail = user_email.EMAIL_USER_PART.concat("@").concat(user_email.EMAIL_SERVER_PART);
                if (LoginPassword.containsKey(userEmail)) {
                    //Email exists
                    common.STATUS = "ERROR";
                    common.ERROR = -5;
                } else {
                    //OK
                    common.STATUS = "OK";

                }
            }
        }

        if ((responseCode != null) && (responseCode == 200))
            responseBody = common.toJSON().toString();

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> UsernameCheck(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        USERNAME username = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof USERNAME)
                    username = (USERNAME)format;

        COMMON common = new COMMON();

        if (username == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                if (LoginUser.containsKey(username.PUBLIC_NAME)) {
                    //Username exists
                    common.STATUS = "ERROR";
                    common.ERROR = -6;
                } else {
                    //OK
                    common.STATUS = "OK";

                }
            }
        }

        if ((responseCode != null) && (responseCode == 200))
            responseBody = common.toJSON().toString();

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> Explore(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        EXPLORE_FILTER filter = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof EXPLORE_FILTER)
                    filter = (EXPLORE_FILTER)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (filter == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
        HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

        if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
            responseCode = 403;
        else {
            responseCode = 200;
            User user = checkSessionCookie(csrfCookie,server.getAppName());

            if (user != null) {
                //Lets EXPLORE

                ArrayList<String> allHives = new ArrayList<String>(Hives.keySet());

                /*ArrayList<String> userHives =  null;
                if (UserHiveSubscriptions.containsKey(user.getUserID()))
                    userHives = UserHiveSubscriptions.get(user.getUserID());

                if ((userHives != null) && (!filter.TYPE.equalsIgnoreCase("CREATION_DATE")))
                    allHives.removeAll(userHives);*/

                Comparator<Hive> comparator = null;

                if (filter.TYPE.equalsIgnoreCase("OUTSTANDING")) {
                    comparator = null;
                } else if (filter.TYPE.equalsIgnoreCase("USERS")) {
                    comparator = new Comparator<Hive>() {
                        @Override
                        public int compare(Hive o1, Hive o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                            if ((o1 == null) && (o2 != null))
                                return -1;
                            else if ((o1 != null) && (o2 == null))
                                return 1;
                            else if ((o1 == null) && (o2 == null))
                                return 0;

                            int u1 = 0;
                            int u2 = 0;

                            if (HiveUserSubscriptions != null) {
                                if ((HiveUserSubscriptions.containsKey(o1.getNameUrl())) && (HiveUserSubscriptions.get(o1.getNameUrl()) != null))
                                    u1 = HiveUserSubscriptions.get(o1.getNameUrl()).size();
                                if ((HiveUserSubscriptions.containsKey(o2.getNameUrl())) && (HiveUserSubscriptions.get(o2.getNameUrl()) != null))
                                    u2 = HiveUserSubscriptions.get(o2.getNameUrl()).size();
                            }

                            int res = u2-u1;

                            return ((res==0)?o1.getNameUrl().compareToIgnoreCase(o2.getNameUrl()):res);
                        }
                    };
                } else if (filter.TYPE.equalsIgnoreCase("CREATION_DATE")) {
                    comparator = new Comparator<Hive>() {
                        @Override
                        public int compare(Hive o1, Hive o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                            if ((o1 == null) && (o2 != null))
                                return -1;
                            else if ((o1 != null) && (o2 == null))
                                return 1;
                            else if ((o1 == null) && (o2 == null))
                                return 0;

                            long d1 = 0;
                            long d2 = 0;

                            if (o1.getCreationDate() != null)
                                d1 = o1.getCreationDate().getTime();
                            if (o2.getCreationDate() != null)
                                d2 = o2.getCreationDate().getTime();

                            long res = d2-d1;

                            return ((res > Integer.MAX_VALUE)?Integer.MAX_VALUE:((res < Integer.MIN_VALUE)?Integer.MIN_VALUE:((res==0)?o1.getNameUrl().compareToIgnoreCase(o2.getNameUrl()):(int)res)));
                        }
                    };
                } else if (filter.TYPE.equalsIgnoreCase("TRENDING")) {
                    comparator = new Comparator<Hive>() {
                        @Override
                        public int compare(Hive o1, Hive o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                            if ((o1 == null) && (o2 != null))
                                return -1;
                            else if ((o1 != null) && (o2 == null))
                                return 1;
                            else if ((o1 == null) && (o2 == null))
                                return 0;

                            int m1 = 0;
                            int m2 = 0;

                            if ((o1.getPublicChat() != null) && (o1.getPublicChat().getConversation() != null))
                                m1 = o1.getPublicChat().getConversation().getCount();
                            if ((o2.getPublicChat() != null) && (o2.getPublicChat().getConversation() != null))
                                m2 = o2.getPublicChat().getConversation().getCount();

                            int res = m2-m1;

                            return ((res==0)?o1.getNameUrl().compareToIgnoreCase(o2.getNameUrl()):res);
                        }
                    };
                } else {
                    comparator = null;
                }

                Collection<Hive> resultSet = null;
                if (comparator != null)
                    resultSet = new TreeSet<Hive>(comparator);
                else
                    resultSet = new ArrayList<Hive>();


                String categoryFilter = "";
                if ((filter.CATEGORY != null) && (!filter.CATEGORY.isEmpty())) {
                    categoryFilter = filter.CATEGORY;
                    if (!categoryFilter.contains("."))
                        categoryFilter += ".";
                }

                for (String hiveKey : allHives)
                    if ((categoryFilter.isEmpty()) || (Hives.get(hiveKey).getCategory().startsWith(categoryFilter)))
                        resultSet.add(Hives.get(hiveKey));

                HIVE_LIST list = new HIVE_LIST();
                list.LIST = new ArrayList<HIVE>();

                responseFormats.add(list);

                Hive[] results = resultSet.toArray(new Hive[resultSet.size()]);

                int length = results.length;
                if (length > 0) {
                    int start = -1;
                    int count = -1;
                    int end = -1;

                    if ((filter.RESULT_INTERVAL != null) && (filter.RESULT_INTERVAL.START_INDEX != null) && (!filter.RESULT_INTERVAL.START_INDEX.isEmpty()))
                        start = ((filter.RESULT_INTERVAL.START_INDEX.equalsIgnoreCase("FIRST")) ? 0 : ((filter.RESULT_INTERVAL.START_INDEX.equalsIgnoreCase("LAST")) ? length : Integer.parseInt(filter.RESULT_INTERVAL.START_INDEX)));

                    if ((filter.RESULT_INTERVAL != null) && (filter.RESULT_INTERVAL.END_INDEX != null) && (!filter.RESULT_INTERVAL.END_INDEX.isEmpty()))
                        end = ((filter.RESULT_INTERVAL.END_INDEX.equalsIgnoreCase("FIRST")) ? 0 : ((filter.RESULT_INTERVAL.END_INDEX.equalsIgnoreCase("LAST")) ? length : Integer.parseInt(filter.RESULT_INTERVAL.END_INDEX)));

                    if ((filter.RESULT_INTERVAL != null) && (filter.RESULT_INTERVAL.COUNT != null))
                        count = filter.RESULT_INTERVAL.COUNT;

                    int finalStart = 0;
                    int finalEnd = length;

                    if ((start < 0) && (count >= 0) && (end >= 0)) {
                        if (end < finalEnd)
                            finalEnd = end;

                        finalStart = finalEnd - count;
                    } else if ((start >= 0) && (count >= 0) && (end < 0)) {
                        if (start > finalStart)
                            finalStart = start;

                        finalEnd = finalStart + count;
                    } else if ((start >= 0) && (count < 0) && (end >= 0)) {
                        finalEnd = end;
                        finalStart = start;
                    } else if ((start >= 0) && (count >= 0) && (end >= 0)) {
                        if (start > finalStart)
                            finalStart = start;

                        finalEnd = finalStart + count;

                        if (end < finalEnd)
                            finalEnd = end;
                    }

                    if (finalStart < 0)
                        finalStart = 0;
                    else if (finalStart >= length)
                        finalStart = length - 1;

                    if (finalEnd < 0)
                        finalEnd = 0;
                    else if (finalEnd > length)
                        finalEnd = length;

                    if (finalStart > finalEnd) {
                        int tmp = finalStart;
                        finalStart = finalEnd;
                        finalEnd = tmp;
                    }

                    if ((finalEnd - finalStart) > 0) {
                        results = Arrays.copyOfRange(results, finalStart, finalEnd);

                        for (Hive hive : results)
                            list.LIST.add(((HIVE) hive.toFormat(new HIVE())));
                    }
                }

                common.STATUS = "OK";
            } else {
                common.STATUS = "SESSION EXPIRED";
            }
        }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> Join(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        HIVE_ID hiveId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof HIVE_ID)
                    hiveId = (HIVE_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (hiveId == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets JOIN
                    if ((Hives.containsKey(hiveId.NAME_URL)) && ((!UserHiveSubscriptions.containsKey(user.getUserID())) || (!UserHiveSubscriptions.get(user.getUserID()).contains(hiveId.NAME_URL)))) {
                        subscribeHive(user.getUserID(),hiveId.NAME_URL);
                        responseFormats.add(Hives.get(hiveId.NAME_URL).getPublicChat().toFormat(new CHAT()));
                        common.STATUS = "OK";
                    } else {
                        common.STATUS = "ERROR";
                        common.ERROR = -7;
                    }

                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> SendMessage(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        MESSAGE message = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof MESSAGE)
                    message = (MESSAGE) format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (message == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie, server.getAppName());

                if (user != null) {
                    //Lets SendMessage
                    if ((message.CHANNEL_UNICODE == null) || (message.CHANNEL_UNICODE.isEmpty()) || (message.USER_ID == null) || (!message.USER_ID.equalsIgnoreCase(user.getUserID())) || (!Chats.containsKey(message.CHANNEL_UNICODE)) || (!UserChatSubscriptions.containsKey(user.getUserID())) || (!UserChatSubscriptions.get(user.getUserID()).contains(message.CHANNEL_UNICODE))) {
                        common.STATUS = "ERROR";
                        common.ERROR = -8;
                    } else {
                        Chat chat = Chats.get(message.CHANNEL_UNICODE);
                        responseFormats.add(sendMessage(user,chat,message,true));
                        common.STATUS = "OK";
                    }

                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty()) ? "{" : ", ") + format.toJSON().toString().substring(1, format.toJSON().toString().length() - 1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer, String>((responseCode != null) ? responseCode : -1, (responseBody != null) ? responseBody : "");
    }

    private static AbstractMap.SimpleEntry<Integer, String> GetMessages(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        MESSAGE_INTERVAL filter = null;
        CHAT_ID chatId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof MESSAGE_INTERVAL)
                    filter = (MESSAGE_INTERVAL)format;
                else if (format instanceof CHAT_ID)
                    chatId = (CHAT_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if ((filter == null) || (chatId == null)) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets Get Messages

                    if ((chatId.CHANNEL_UNICODE != null) && (Chats.containsKey(chatId.CHANNEL_UNICODE))) {
                        MESSAGE_LIST list = new MESSAGE_LIST();
                        responseFormats.add(list);
                        common.STATUS = "OK";
                        Conversation conversation = Chats.get(chatId.CHANNEL_UNICODE).getConversation();
                        ArrayList<Message> resultList = new ArrayList<Message>();
                        if (conversation.getCount() > 0) {
                            String lastMessageID = null;
                            if ((filter.LAST_MESSAGE_ID == null) || (filter.LAST_MESSAGE_ID.isEmpty()) || (filter.LAST_MESSAGE_ID.equalsIgnoreCase("LAST")))
                                lastMessageID = conversation.getLastMessage().getId();
                            else
                                lastMessageID = filter.LAST_MESSAGE_ID;
                            int lastMessage = Integer.parseInt(lastMessageID);

                            String firstMessageID = null;
                            if ((filter.START_MESSAGE_ID == null) || (filter.START_MESSAGE_ID.isEmpty()) || (filter.START_MESSAGE_ID.equalsIgnoreCase("FIRST")))
                                firstMessageID = "-1";
                            else
                                firstMessageID = filter.START_MESSAGE_ID;
                            int firstMessage = Integer.parseInt(firstMessageID);

                            int messageCount;
                            if (filter.COUNT != null)
                                messageCount = filter.COUNT;
                            else
                                messageCount = lastMessage - firstMessage;

                            for (int i = lastMessage; ((i > firstMessage) && ((lastMessage - i) < messageCount)); i--) {
                                resultList.add(conversation.getMessageByID(String.format("%d", i)));
                            }

                            list.MESSAGES = new ArrayList<MESSAGE>();
                            for (Message msg : resultList)
                                list.MESSAGES.add((MESSAGE) msg.toFormat(new MESSAGE()));

                            /*if ((firstMessage > -1) || (filter.START_MESSAGE_ID.equalsIgnoreCase("FIRST"))) {
                                list.NUMBER_MESSAGES = lastMessage - firstMessage - messageCount;
                            }*/
                            list.NUMBER_MESSAGES = list.MESSAGES.size();
                        } else {
                            list.MESSAGES = null;
                            list.NUMBER_MESSAGES = 0;
                        }

                    } else {
                        common.STATUS = "ERROR";
                        common.ERROR = -9;
                    }
                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> LocalProfile(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

/*        EXPLORE_FILTER message = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof EXPLORE_FILTER)
                    message = (EXPLORE_FILTER)format;*/

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        /*if (message == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {*/
        HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

        if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
            responseCode = 403;
        else {
            responseCode = 200;
            User user = checkSessionCookie(csrfCookie,server.getAppName());

            if (user != null) {
                //Lets return profile
                LOCAL_USER_PROFILE result = (LOCAL_USER_PROFILE)user.toFormat(new LOCAL_USER_PROFILE());
                responseFormats.add(result);
                if (user.getUserPublicProfile() != null) {
                    result.USER_BASIC_PUBLIC_PROFILE = ((BASIC_PUBLIC_PROFILE) user.getUserPublicProfile().toFormat(new BASIC_PUBLIC_PROFILE()));
                    result.USER_PUBLIC_PROFILE = ((PUBLIC_PROFILE) user.getUserPublicProfile().toFormat(new PUBLIC_PROFILE()));
                }
                if (user.getUserPrivateProfile() != null) {
                    result.USER_BASIC_PRIVATE_PROFILE = ((BASIC_PRIVATE_PROFILE) user.getUserPrivateProfile().toFormat(new BASIC_PRIVATE_PROFILE()));
                    result.USER_PRIVATE_PROFILE = ((PRIVATE_PROFILE) user.getUserPrivateProfile().toFormat(new PRIVATE_PROFILE()));
                }
                if ((UserHiveSubscriptions != null) && (UserHiveSubscriptions.containsKey(user.getUserID()))) {
                    ArrayList<String> subscriptions = UserHiveSubscriptions.get(user.getUserID());
                    if ((subscriptions != null) && (subscriptions.size() > 0)) {
                        result.HIVES_SUBSCRIBED = new ArrayList<HIVE_ID>();
                        for (String hive : subscriptions) {
                            HIVE_ID hive_id = new HIVE_ID();
                            hive_id.NAME_URL = hive;
                            result.HIVES_SUBSCRIBED.add(hive_id);
                        }
                    }
                }
                common.STATUS = "OK";
            } else {
                common.STATUS = "SESSION EXPIRED";
            }
        }
        //}

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> UpdateProfile(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        LOCAL_USER_PROFILE local_user_profile = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof LOCAL_USER_PROFILE)
                    local_user_profile = (LOCAL_USER_PROFILE)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (local_user_profile == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if ((user != null) && (user.getUserPublicProfile().getPublicName().equalsIgnoreCase(local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME))) {
                    //Lets update profile. Compare field by field user with received format and update fields. EMPTY is to clear field. NULL is to not modify.
                    //PUBLIC_NAME can't be changed. EMAIL and PASSWORD can be changed, but can not be cleared. BIRTHDAY, SEX, and COLOR can't be cleared.
                    //FIRST_NAME and LAST_NAME can't be cleared simultaneously.

                    try {
                        boolean anyUpdate = false;
                        boolean emailChanged = false;
                        boolean passwordChanged = false;

                        boolean birthdayChanged = false;
                        boolean languagesChanged = false;
                        boolean sexChanged = false;
                        boolean locationChanged = false;

                        boolean colorChanged = false;
                        boolean statusMsgPublicChanged = false;

                        boolean showSexPublicChanged = false;
                        boolean showAgePublicChanged = false;
                        boolean showLocationPublicChanged = false;

                        boolean statusMsgPrivateChanged = false;
                        boolean firstNameChanged = false;
                        boolean lastNameChanged = false;

                        boolean showAgePrivateChanged = false;

                        if ((local_user_profile.EMAIL != null) && (!user.getEmail().equalsIgnoreCase(local_user_profile.EMAIL)) && (!LoginPassword.containsKey(local_user_profile.EMAIL))) {
                            if (local_user_profile.EMAIL.isEmpty())
                                throw new IllegalArgumentException();
                            emailChanged = true;
                            anyUpdate = true;
                        }

                        if ((local_user_profile.PASS != null) && (!LoginPassword.get(user.getEmail()).equalsIgnoreCase(local_user_profile.PASS))) {
                            if (local_user_profile.PASS.isEmpty())
                                throw new IllegalArgumentException();
                            passwordChanged = true;
                            anyUpdate = true;
                        }

                        if (local_user_profile.USER_PRIVATE_PROFILE != null) {
                            if ((local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE != null) && (user.getUserPrivateProfile().getBirthdate() != local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE)) {
                                if (local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE.compareTo(TimestampFormatter.toDate("1970-01-01T00:00:00.000")) == 0)
                                    throw new IllegalArgumentException();

                                birthdayChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PRIVATE_PROFILE.LOCATION != null) && (!user.getUserPrivateProfile().getLocation().equalsIgnoreCase(local_user_profile.USER_PRIVATE_PROFILE.LOCATION))) {
                                locationChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PRIVATE_PROFILE.SEX != null) && (!user.getUserPrivateProfile().getSex().equalsIgnoreCase(local_user_profile.USER_PRIVATE_PROFILE.SEX))) {
                                if (local_user_profile.USER_PRIVATE_PROFILE.SEX.isEmpty())
                                    throw new IllegalArgumentException();

                                sexChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PRIVATE_PROFILE.PRIVATE_SHOW_AGE != null) && (user.getUserPrivateProfile().getShowAge() != local_user_profile.USER_PRIVATE_PROFILE.PRIVATE_SHOW_AGE)) {
                                showAgePrivateChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE != null) && ((!user.getUserPrivateProfile().getLanguages().containsAll(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE)) || (local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE.containsAll(user.getUserPrivateProfile().getLanguages())))) {
                                languagesChanged = true;
                                anyUpdate = true;
                            }
                        }

                        if (local_user_profile.USER_BASIC_PRIVATE_PROFILE != null) {
                            if ((local_user_profile.USER_BASIC_PRIVATE_PROFILE.STATUS_MESSAGE != null) && (!user.getUserPrivateProfile().getStatusMessage().equalsIgnoreCase(local_user_profile.USER_BASIC_PRIVATE_PROFILE.STATUS_MESSAGE))) {
                                statusMsgPrivateChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME != null) && (!user.getUserPrivateProfile().getLastName().equalsIgnoreCase(local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME))) {
                                lastNameChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME != null) && (!user.getUserPrivateProfile().getFirstName().equalsIgnoreCase(local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME))) {
                                firstNameChanged = true;
                                anyUpdate = true;
                            }
                        }

                        if (local_user_profile.USER_BASIC_PUBLIC_PROFILE != null) {
                            if ((local_user_profile.USER_BASIC_PUBLIC_PROFILE.STATUS_MESSAGE != null) && (!user.getUserPublicProfile().getStatusMessage().equalsIgnoreCase(local_user_profile.USER_BASIC_PUBLIC_PROFILE.STATUS_MESSAGE))) {
                                statusMsgPublicChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_BASIC_PUBLIC_PROFILE.USER_COLOR != null) && (!user.getUserPublicProfile().getColor().equalsIgnoreCase(local_user_profile.USER_BASIC_PUBLIC_PROFILE.USER_COLOR))) {
                                if (local_user_profile.USER_BASIC_PUBLIC_PROFILE.USER_COLOR.isEmpty())
                                    throw new IllegalArgumentException();
                                colorChanged = true;
                                anyUpdate = true;
                            }
                        }

                        if (local_user_profile.USER_PUBLIC_PROFILE != null) {
                            if ((local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE != null) && (user.getUserPublicProfile().getBirthdate() != local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE)) {
                                if (local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE.compareTo(TimestampFormatter.toDate("1970-01-01T00:00:00.000")) == 0)
                                    throw new IllegalArgumentException();
                                birthdayChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.LOCATION != null) && (!user.getUserPublicProfile().getLocation().equalsIgnoreCase(local_user_profile.USER_PUBLIC_PROFILE.LOCATION))) {
                                locationChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.SEX != null) && (!user.getUserPublicProfile().getSex().equalsIgnoreCase(local_user_profile.USER_PUBLIC_PROFILE.SEX))) {
                                if (local_user_profile.USER_PUBLIC_PROFILE.SEX.isEmpty())
                                    throw new IllegalArgumentException();

                                sexChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_AGE != null) && (user.getUserPublicProfile().getShowAge() != local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_AGE)) {
                                showAgePublicChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_LOCATION != null) && (user.getUserPublicProfile().getShowLocation() != local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_LOCATION)) {
                                showLocationPublicChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_SEX != null) && (user.getUserPublicProfile().getShowSex() != local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_SEX)) {
                                showSexPublicChanged = true;
                                anyUpdate = true;
                            }

                            if ((local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE != null) && ((!user.getUserPublicProfile().getLanguages().containsAll(local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE)) || (local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE.containsAll(user.getUserPrivateProfile().getLanguages())))) {
                                languagesChanged = true;
                                anyUpdate = true;
                            }
                        }

                        if ((firstNameChanged && lastNameChanged && local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME.isEmpty() && local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME.isEmpty()) ||
                            (firstNameChanged && local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME.isEmpty() && !lastNameChanged && user.getUserPrivateProfile().getLastName().isEmpty()) ||
                            (lastNameChanged && local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME.isEmpty() && !firstNameChanged && user.getUserPrivateProfile().getFirstName().isEmpty())) {
                            throw new IllegalArgumentException();
                        }

                        if (!anyUpdate) {
                            common.STATUS = "ERROR";
                            common.ERROR = -16;
                        } else {
                            //Lets update profile. Compare field by field user with received format and update fields. EMPTY is to clear field. NULL is to not modify.
                            //PUBLIC_NAME can't be changed. EMAIL and PASSWORD can be changed, but can not be cleared. BIRTHDAY, SEX, and COLOR can't be cleared.
                            //FIRST_NAME and LAST_NAME can't be cleared simultaneously.

                            if (emailChanged) {
                                String actualPass = LoginPassword.get(user.getEmail());
                                LoginPassword.remove(user.getEmail());
                                user.setEmail(local_user_profile.EMAIL);
                                LoginPassword.put(user.getEmail(),actualPass);
                            }

                            if (passwordChanged) {
                                LoginPassword.put(user.getEmail(),local_user_profile.PASS);
                            }

                            if (colorChanged) {
                                user.getUserPublicProfile().setColor(local_user_profile.USER_BASIC_PUBLIC_PROFILE.USER_COLOR);
                            }

                            if (statusMsgPublicChanged) {
                                user.getUserPublicProfile().setStatusMessage(local_user_profile.USER_BASIC_PUBLIC_PROFILE.STATUS_MESSAGE);
                            }

                            if (showAgePublicChanged) {
                                user.getUserPublicProfile().setShowAge(local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_AGE);
                            }

                            if (showSexPublicChanged) {
                                user.getUserPublicProfile().setShowSex(local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_SEX);
                            }

                            if (showLocationPublicChanged) {
                                user.getUserPublicProfile().setShowLocation(local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_LOCATION);
                            }

                            if (firstNameChanged) {
                                user.getUserPrivateProfile().setFirstName(local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME);
                            }

                            if (lastNameChanged) {
                                user.getUserPrivateProfile().setLastName(local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME);
                            }

                            if (statusMsgPrivateChanged) {
                                user.getUserPrivateProfile().setStatusMessage(local_user_profile.USER_BASIC_PRIVATE_PROFILE.STATUS_MESSAGE);
                            }

                            if (showAgePrivateChanged) {
                                user.getUserPrivateProfile().setShowAge(local_user_profile.USER_PRIVATE_PROFILE.PRIVATE_SHOW_AGE);
                            }

                            if (sexChanged) {
                                if (local_user_profile.USER_PRIVATE_PROFILE == null) {
                                    user.getUserPublicProfile().setSex(local_user_profile.USER_PUBLIC_PROFILE.SEX);
                                    user.getUserPrivateProfile().setSex(local_user_profile.USER_PUBLIC_PROFILE.SEX);
                                } else if (local_user_profile.USER_PUBLIC_PROFILE == null) {
                                    user.getUserPublicProfile().setSex(local_user_profile.USER_PRIVATE_PROFILE.SEX);
                                    user.getUserPrivateProfile().setSex(local_user_profile.USER_PRIVATE_PROFILE.SEX);
                                } else if (local_user_profile.USER_PRIVATE_PROFILE.SEX.equalsIgnoreCase(local_user_profile.USER_PUBLIC_PROFILE.SEX)) {
                                    user.getUserPublicProfile().setSex(local_user_profile.USER_PRIVATE_PROFILE.SEX);
                                    user.getUserPrivateProfile().setSex(local_user_profile.USER_PRIVATE_PROFILE.SEX);
                                } else
                                    throw new IllegalArgumentException();
                            }

                            if (locationChanged) {
                                if (local_user_profile.USER_PRIVATE_PROFILE == null) {
                                    user.getUserPublicProfile().setLocation(local_user_profile.USER_PUBLIC_PROFILE.LOCATION);
                                    user.getUserPrivateProfile().setLocation(local_user_profile.USER_PUBLIC_PROFILE.LOCATION);
                                } else if (local_user_profile.USER_PUBLIC_PROFILE == null) {
                                    user.getUserPublicProfile().setLocation(local_user_profile.USER_PRIVATE_PROFILE.LOCATION);
                                    user.getUserPrivateProfile().setLocation(local_user_profile.USER_PRIVATE_PROFILE.LOCATION);
                                } else if (local_user_profile.USER_PRIVATE_PROFILE.LOCATION.equalsIgnoreCase(local_user_profile.USER_PUBLIC_PROFILE.LOCATION)) {
                                    user.getUserPublicProfile().setLocation(local_user_profile.USER_PRIVATE_PROFILE.LOCATION);
                                    user.getUserPrivateProfile().setLocation(local_user_profile.USER_PRIVATE_PROFILE.LOCATION);
                                } else
                                    throw new IllegalArgumentException();
                            }

                            if (birthdayChanged) {
                                if (local_user_profile.USER_PRIVATE_PROFILE == null) {
                                    user.getUserPublicProfile().setBirthdate(local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE);
                                    user.getUserPrivateProfile().setBirthdate(local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE);
                                } else if (local_user_profile.USER_PUBLIC_PROFILE == null) {
                                    user.getUserPublicProfile().setBirthdate(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE);
                                    user.getUserPrivateProfile().setBirthdate(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE);
                                } else if (local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE == local_user_profile.USER_PUBLIC_PROFILE.BIRTHDATE) {
                                    user.getUserPublicProfile().setBirthdate(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE);
                                    user.getUserPrivateProfile().setBirthdate(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE);
                                } else
                                    throw new IllegalArgumentException();
                            }

                            if (languagesChanged) {
                                if (local_user_profile.USER_PRIVATE_PROFILE == null) {
                                    user.getUserPublicProfile().setLanguages(local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE);
                                    user.getUserPrivateProfile().setLanguages(local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE);
                                } else if (local_user_profile.USER_PUBLIC_PROFILE == null) {
                                    user.getUserPublicProfile().setLanguages(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE);
                                    user.getUserPrivateProfile().setLanguages(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE);
                                } else if ((local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE.containsAll(local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE)) && (local_user_profile.USER_PUBLIC_PROFILE.LANGUAGE.containsAll(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE))) {
                                    user.getUserPublicProfile().setLanguages(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE);
                                    user.getUserPrivateProfile().setLanguages(local_user_profile.USER_PRIVATE_PROFILE.LANGUAGE);
                                } else
                                    throw new IllegalArgumentException();
                            }
                        }
                    } catch (Exception e) {
                        common.STATUS = "ERROR";
                        common.ERROR = -15;
                    }

                    common.STATUS = "OK";
                } else if (user != null) {
                    common.STATUS = "ERROR";
                    common.ERROR = -14;
                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> ChatInfo(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        CHAT_ID chatId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof CHAT_ID)
                    chatId = (CHAT_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (chatId == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets GET the info
                    if ((chatId.CHANNEL_UNICODE == null) || (!Chats.containsKey(chatId.CHANNEL_UNICODE)) || (!UserChatSubscriptions.containsKey(user.getUserID())) || (!UserChatSubscriptions.get(user.getUserID()).contains(chatId.CHANNEL_UNICODE))) {
                        common.STATUS = "ERROR";
                        common.ERROR = -10;
                    } else {
                        Chat chatInfo = Chats.get(chatId.CHANNEL_UNICODE);
                        responseFormats.add(chatInfo.toFormat(new CHAT()));
                        common.STATUS = "OK";
                    }

                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> ChatList(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

/*        EXPLORE_FILTER message = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof EXPLORE_FILTER)
                    message = (EXPLORE_FILTER)format;*/

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        /*if (message == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {*/
        HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

        if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
            responseCode = 403;
        else {
            responseCode = 200;
            User user = checkSessionCookie(csrfCookie,server.getAppName());

            if (user != null) {
                //Lets GET CHAT LIST
                common.STATUS = "OK";

                CHAT_LIST list = new CHAT_LIST();
                responseFormats.add(list);

                if (UserChatSubscriptions.containsKey(user.getUserID())) {
                    list.LIST = new ArrayList<CHAT_SYNC>();

                    for (String channelUnicode : UserChatSubscriptions.get(user.getUserID())) {
                        if ((Chats.containsKey(channelUnicode)) && (Chats.get(channelUnicode).getConversation() != null) && (Chats.get(channelUnicode).getConversation().getCount() > 0))
                            list.LIST.add((CHAT_SYNC)Chats.get(channelUnicode).toFormat(new CHAT_SYNC()));
                    }
                }

            } else {
                common.STATUS = "SESSION EXPIRED";
            }
        }
        //}

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> UserProfile(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        PROFILE_ID profileId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof PROFILE_ID)
                    profileId = (PROFILE_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (profileId == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets EXPLORE
                    if ((profileId.USER_ID == null) || (profileId.USER_ID.isEmpty()) || (profileId.PROFILE_TYPE == null) || (profileId.PROFILE_TYPE.isEmpty()) ||
                            ((!profileId.PROFILE_TYPE.startsWith("BASIC_")) && (!profileId.PROFILE_TYPE.startsWith("EXTENDED_")) && (!profileId.PROFILE_TYPE.startsWith("COMPLETE_"))) ||
                            ((!profileId.PROFILE_TYPE.endsWith("_PUBLIC")) && (!profileId.PROFILE_TYPE.endsWith("_PRIVATE")))) {
                        common.STATUS = "ERROR";
                        common.ERROR = -11;
                    } else {
                        common.STATUS = "OK";

                        if ((profileId.PROFILE_TYPE.endsWith("_PRIVATE")) && ((!UserFriendList.containsKey(user.getUserID())) || (!UserFriendList.get(user.getUserID()).contains(profileId.USER_ID)))) {
                            common.STATUS = "ERROR";
                            common.ERROR = -12;
                        } else {
                            User requestedUser = LoginUser.get(profileId.USER_ID);
                            USER_PROFILE result = new USER_PROFILE();
                            responseFormats.add(result);
                            if (requestedUser.getUserPublicProfile() != null) {
                                result.USER_BASIC_PUBLIC_PROFILE = ((BASIC_PUBLIC_PROFILE) requestedUser.getUserPublicProfile().toFormat(new BASIC_PUBLIC_PROFILE()));
                                result.USER_PUBLIC_PROFILE = ((PUBLIC_PROFILE) requestedUser.getUserPublicProfile().toFormat(new PUBLIC_PROFILE()));
                            }
                            if (requestedUser.getUserPrivateProfile() != null) {
                                result.USER_BASIC_PRIVATE_PROFILE = ((BASIC_PRIVATE_PROFILE) requestedUser.getUserPrivateProfile().toFormat(new BASIC_PRIVATE_PROFILE()));
                                result.USER_PRIVATE_PROFILE = ((PRIVATE_PROFILE) requestedUser.getUserPrivateProfile().toFormat(new PRIVATE_PROFILE()));
                            }

                            if (profileId.PROFILE_TYPE.endsWith("_PRIVATE")) {
                                result.USER_PUBLIC_PROFILE = null;
                                if (!profileId.PROFILE_TYPE.startsWith("COMPLETE_"))
                                    result.USER_BASIC_PUBLIC_PROFILE = null;
                                if (profileId.PROFILE_TYPE.startsWith("BASIC_"))
                                    result.USER_PRIVATE_PROFILE = null;
                            } else {
                                result.USER_PRIVATE_PROFILE = null;
                                result.USER_BASIC_PRIVATE_PROFILE = null;
                                if (profileId.PROFILE_TYPE.startsWith("BASIC_"))
                                    result.USER_PUBLIC_PROFILE = null;
                            }
                        }
                    }
                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> HiveInfo(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        HIVE_ID hiveId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof HIVE_ID)
                    hiveId = (HIVE_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (hiveId == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets GET HIVE INFO
                    if ((hiveId.NAME_URL == null) || (!Hives.containsKey(hiveId.NAME_URL)) || (!UserHiveSubscriptions.containsKey(user.getUserID())) || (!UserHiveSubscriptions.get(user.getUserID()).contains(hiveId.NAME_URL))) {
                        common.STATUS = "ERROR";
                        common.ERROR = -13;
                    } else {
                        Hive hiveInfo = Hives.get(hiveId.NAME_URL);
                        responseFormats.add(hiveInfo.toFormat(new HIVE()));
                        common.STATUS = "OK";
                    }

                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> HiveUsers(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        HIVE_USERS_FILTER hiveUsersFilter = null;
        HIVE_ID hiveId = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof HIVE_USERS_FILTER)
                    hiveUsersFilter = (HIVE_USERS_FILTER)format;
                else if (format instanceof HIVE_ID)
                    hiveId = (HIVE_ID)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if ((hiveUsersFilter == null) || (hiveId == null)) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets GET HIVE USERS
                    if ((hiveId.NAME_URL == null) || (!Hives.containsKey(hiveId.NAME_URL)) || (!UserHiveSubscriptions.containsKey(user.getUserID())) || (!UserHiveSubscriptions.get(user.getUserID()).contains(hiveId.NAME_URL))) {
                        common.STATUS = "ERROR";
                        common.ERROR = -13;
                    } else {
                        Hive hive = Hives.get(hiveId.NAME_URL);

                        Comparator<User> comparator = null;

                        if (hiveUsersFilter.TYPE.equalsIgnoreCase("OUTSTANDING")) {
                            comparator = new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                                    if ((o1 == null) && (o2 != null))
                                        return -1;
                                    else if ((o1 != null) && (o2 == null))
                                        return 1;
                                    else if ((o1 == null) && (o2 == null))
                                        return 0;

                                    String u1 = o1.getUserID();
                                    String u2 = o2.getUserID();

                                    int res = u1.compareTo(u2);

                                    return ((res==0)?o1.getEmail().compareToIgnoreCase(o2.getEmail()):res);
                                }
                            };
                        } else if (hiveUsersFilter.TYPE.equalsIgnoreCase("LOCATION")) {
                            comparator = new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                                    if ((o1 == null) && (o2 != null))
                                        return -1;
                                    else if ((o1 != null) && (o2 == null))
                                        return 1;
                                    else if ((o1 == null) && (o2 == null))
                                        return 0;

                                    String l1 = o1.getUserPublicProfile().getLocation();
                                    String l2 = o2.getUserPublicProfile().getLocation();

                                    return l1.compareTo(l2);
                                }
                            };
                        } else if (hiveUsersFilter.TYPE.equalsIgnoreCase("RECENTLY_ONLINE")) {
                            comparator = new Comparator<User>() {
                                @Override
                                public int compare(User o1, User o2) { //o1 < o2 => res < 0 | o1 = o2 => res = 0 | o1 > o2 => res > 0
                                    if ((o1 == null) && (o2 != null))
                                        return -1;
                                    else if ((o1 != null) && (o2 == null))
                                        return 1;
                                    else if ((o1 == null) && (o2 == null))
                                        return 0;

                                    long d1 = o1.getUserPublicProfile().getBirthdate().getTime();
                                    long d2 = o2.getUserPublicProfile().getBirthdate().getTime();

                                    long res = d2-d1;

                                    return ((res > Integer.MAX_VALUE)?Integer.MAX_VALUE:((res < Integer.MIN_VALUE)?Integer.MIN_VALUE:((res==0)?o1.getUserID().compareToIgnoreCase(o2.getUserID()):(int)res)));
                                }
                            };
                        } else {
                            comparator = null;
                        }

                        Collection<User> resultSet = null;
                        if (comparator != null)
                            resultSet = new TreeSet<User>(comparator);
                        else
                            resultSet = new ArrayList<User>();

                        ArrayList<String> allUsers = null;
                        if (HiveUserSubscriptions.containsKey(hive.getNameUrl()))
                            allUsers = HiveUserSubscriptions.get(hive.getNameUrl());
                        else
                            allUsers = new ArrayList<String>();

                        for (String userLogin : allUsers)
                            if (!userLogin.equalsIgnoreCase(user.getUserID()))
                                resultSet.add(LoginUser.get(userLogin));

                        USER_PROFILE_LIST list = new USER_PROFILE_LIST();
                        list.LIST = new ArrayList<USER_PROFILE>();

                        responseFormats.add(list);

                        User[] results = resultSet.toArray(new User[resultSet.size()]);

                        int length = results.length;
                        if (length > 0) {
                            int start = -1;
                            int count = -1;
                            int end = -1;

                            if ((hiveUsersFilter.RESULT_INTERVAL != null) && (hiveUsersFilter.RESULT_INTERVAL.START_INDEX != null) && (!hiveUsersFilter.RESULT_INTERVAL.START_INDEX.isEmpty()))
                                start = ((hiveUsersFilter.RESULT_INTERVAL.START_INDEX.equalsIgnoreCase("FIRST")) ? 0 : ((hiveUsersFilter.RESULT_INTERVAL.START_INDEX.equalsIgnoreCase("LAST")) ? length : Integer.parseInt(hiveUsersFilter.RESULT_INTERVAL.START_INDEX)));

                            if ((hiveUsersFilter.RESULT_INTERVAL != null) && (hiveUsersFilter.RESULT_INTERVAL.END_INDEX != null) && (!hiveUsersFilter.RESULT_INTERVAL.END_INDEX.isEmpty()))
                                end = ((hiveUsersFilter.RESULT_INTERVAL.END_INDEX.equalsIgnoreCase("FIRST")) ? 0 : ((hiveUsersFilter.RESULT_INTERVAL.END_INDEX.equalsIgnoreCase("LAST")) ? length : Integer.parseInt(hiveUsersFilter.RESULT_INTERVAL.END_INDEX)));

                            if ((hiveUsersFilter.RESULT_INTERVAL != null) && (hiveUsersFilter.RESULT_INTERVAL.COUNT != null))
                                count = hiveUsersFilter.RESULT_INTERVAL.COUNT;

                            int finalStart = 0;
                            int finalEnd = length;

                            if ((start < 0) && (count >= 0) && (end >= 0)) {
                                if (end < finalEnd)
                                    finalEnd = end;

                                finalStart = finalEnd - count;
                            } else if ((start >= 0) && (count >= 0) && (end < 0)) {
                                if (start > finalStart)
                                    finalStart = start;

                                finalEnd = finalStart + count;
                            } else if ((start >= 0) && (count < 0) && (end >= 0)) {
                                finalEnd = end;
                                finalStart = start;
                            } else if ((start >= 0) && (count >= 0) && (end >= 0)) {
                                if (start > finalStart)
                                    finalStart = start;

                                finalEnd = finalStart + count;

                                if (end < finalEnd)
                                    finalEnd = end;
                            }

                            if (finalStart < 0)
                                finalStart = 0;
                            else if (finalStart >= length)
                                finalStart = length - 1;

                            if (finalEnd < 0)
                                finalEnd = 0;
                            else if (finalEnd > length)
                                finalEnd = length;

                            if (finalStart > finalEnd) {
                                int tmp = finalStart;
                                finalStart = finalEnd;
                                finalEnd = tmp;
                            }

                            if ((finalEnd - finalStart) > 0) {
                                results = Arrays.copyOfRange(results, finalStart, finalEnd);

                                for (User u : results) {
                                    USER_PROFILE user_profile = new USER_PROFILE();
                                    user_profile.USER_BASIC_PUBLIC_PROFILE = (BASIC_PUBLIC_PROFILE)u.getUserPublicProfile().toFormat(new BASIC_PUBLIC_PROFILE());
                                    list.LIST.add(user_profile);
                                }
                            }
                        }

                        common.STATUS = "OK";
                    }
                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    private static AbstractMap.SimpleEntry<Integer, String> CreateHive(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        HIVE newHive = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof HIVE)
                    newHive = (HIVE)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (newHive == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //createHive(String name, String hiveImage,String category, String description, String[] languages, String... tags)

                    String name = newHive.NAME;
                    String hiveImage = newHive.IMAGE_URL;
                    String category = newHive.CATEGORY;
                    String description = newHive.DESCRIPTION;
                    String[] languages = null;
                    String[] tags = null;
                    if ((newHive.CHAT_LANGUAGES != null) && (!newHive.CHAT_LANGUAGES.isEmpty()))
                        languages = newHive.CHAT_LANGUAGES.toArray(new String[newHive.CHAT_LANGUAGES.size()]);
                    else
                        languages = new String[] {"English"};

                    if ((newHive.TAGS != null) && (!newHive.TAGS.isEmpty()))
                        tags = newHive.TAGS.toArray(new String[newHive.TAGS.size()]);

                    try {
                        Hive hive = createHive(name, hiveImage, category, description, new Date(), languages, tags);

                        subscribeHive(user.getUserID(),hive.getNameUrl());

                        responseFormats.add(hive.toFormat(new HIVE_ID()));
                        responseFormats.add(hive.getPublicChat().toFormat(new CHAT()));

                        common.STATUS = "OK";
                    } catch (Exception e) {
                        common.STATUS = "ERROR";
                        common.ERROR = -14;
                    }


                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    /*private static AbstractMap.SimpleEntry<Integer, String> Command(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;

        EXPLORE_FILTER message = null;
        if (formats != null)
            for (Format format : formats)
                if (format instanceof EXPLORE_FILTER)
                    message = (EXPLORE_FILTER)format;

        COMMON common = new COMMON();

        ArrayList<Format> responseFormats = new ArrayList<Format>();
        responseFormats.add(common);

        if (message == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            HttpCookie csrfCookie = checkCSRFCookie(server.getAppName());

            if ((csrfCookie == null) || (csrfCookie.hasExpired()) || (!CSRFTokens.contains(csrfCookie.getValue())))
                responseCode = 403;
            else {
                responseCode = 200;
                User user = checkSessionCookie(csrfCookie,server.getAppName());

                if (user != null) {
                    //Lets EXPLORE

                } else {
                    common.STATUS = "SESSION EXPIRED";
                }
            }
        }

        if ((responseCode != null) && (responseCode == 200) && (responseFormats.size() > 0)) {
            responseBody = "";
            for (Format format : responseFormats)
                responseBody += ((responseBody.isEmpty())?"{":", ")+format.toJSON().toString().substring(1,format.toJSON().toString().length()-1);
            responseBody += "}";
        }

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }*/



}
