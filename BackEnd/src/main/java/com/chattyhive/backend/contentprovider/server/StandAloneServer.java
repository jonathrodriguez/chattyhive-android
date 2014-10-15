package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Chats.Chat;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.BASIC_PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT_LIST;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.CSRF_TOKEN;
import com.chattyhive.backend.contentprovider.formats.EXPLORE_FILTER;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.HIVE_LIST;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_LIST;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.formats.USERNAME;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.chattyhive.backend.contentprovider.formats.USER_PROFILE;
import com.chattyhive.backend.util.RandomString;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.chattyhive.backend.util.events.PubSubChannelEventArgs;
import com.chattyhive.backend.util.formatters.DateFormatter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Jonathan on 15/09/2014.
 */
public class StandAloneServer {

    private static RandomString randomString = new RandomString(10);

    private static String CSRFTokenCookie = "csrftoken";
    private static String SessionIDCookie = "sessionid";

    private static ArrayList<String> CSRFTokens;
    private static HashMap<String,String> LoginPassword;
    private static HashMap<String,String> CSRFTokenSessionID;
    private static HashMap<String,User> SessionIDUser;
    private static HashMap<String,User> LoginUser;

    private static HashMap<String, Hive> Hives;
    private static HashMap<String, ArrayList<String>> HiveUserSubscriptions;
    private static HashMap<String, ArrayList<String>> UserHiveSubscriptions;
    private static HashMap<String, Group> Chats;
    private static HashMap<String, ArrayList<String>> ChatUserSubscriptions;
    private static HashMap<String, ArrayList<String>> UserChatSubscriptions;

    private static HashMap<String, ArrayList<String>> UserFriendList;

    static {
        if (StaticParameters.StandAlone) {
            CSRFTokens = new ArrayList<String>();
            LoginPassword = new HashMap<String, String>();
            CSRFTokenSessionID = new HashMap<String, String>();
            SessionIDUser = new HashMap<String, User>();
            LoginUser = new HashMap<String, User>();
            Hives = new HashMap<String, Hive>();
            HiveUserSubscriptions = new HashMap<String, ArrayList<String>>();
            UserHiveSubscriptions = new HashMap<String, ArrayList<String>>();
            Chats = new HashMap<String, Group>();
            ChatUserSubscriptions = new HashMap<String, ArrayList<String>>();
            UserChatSubscriptions = new HashMap<String, ArrayList<String>>();

            UserFriendList = new HashMap<String, ArrayList<String>>();

            if (StaticParameters.StandAloneDataInitialization) InitializeData();
        }
    }

    private static void InitializeData() {
        /**************/
        /* Inner vars */
        /**************/
        User user;
        Hive hive;

        /********************************************************************/
        /*                            USERS                                 */
        /********************************************************************/
        /*              Login: jonathan    Pass: 12345678                   */
        /*              Login: cassini91   Pass: huygens                    */
        /*              Login: trabuco     Pass: 15081968                   */
        /*              Login: serezy      Pass: Istiklâl Marsi             */
        /********************************************************************/
        user = createUser("jonathan@chattyhive.com", "Jonathan", "Rodriguez", "jonathan", "#AA22AA", "08/12/1987", "Vigo, Pontevedra, Spain", "MALE", true, false, true, true, "es-ES", "fr-FR", "en-UK", "en-US", "ga-ES", "pt-PT");
        LoginUser.put(user.getUserPublicProfile().getPublicName(), user);
        LoginPassword.put(user.getEmail(), "12345678");

        user = createUser("cassini91@hotmail.com", "Cassandra", "Prieto", "cassini91", "#55dd9f", "31/03/1991", "Vigo, Pontevedra, Spain", "FEMALE", false, false, false, false, "es-ES", "ga-ES");
        LoginUser.put(user.getUserPublicProfile().getPublicName(), user);
        LoginPassword.put(user.getEmail(), "huygens");

        user = createUser("monchuco@yahoo.es", "Ramon", "Araujo", "trabuco", "#dfada0", "15/08/1968", "Andorra", "MALE", true, true, true, true, "es-ES", "fr-FR", "en-US", "ca-ES");
        LoginUser.put(user.getUserPublicProfile().getPublicName(), user);
        LoginPassword.put(user.getEmail(), "15081968");

        user = createUser("serpalina@gmail.com", "Serezade", "Agthëãçykn", "serezy", "#16a46a", "04/07/1302", "Ankara, Ankara, Turkey", "FEMALE", true, true, false, false, "en-US", "tr-TR");
        LoginUser.put(user.getUserPublicProfile().getPublicName(), user);
        LoginPassword.put(user.getEmail(), "Istiklâl Marsi");

        /********************************************************************/
        /*                HIVES                                             */
        /********************************************************************/
        hive = createHive("Minecraft - Unofficial chat","Video games - PC","Unofficial chat for minecraft's fans.");
        subscribeHive("jonathan",hive.getNameUrl());
        subscribeHive("cassini91",hive.getNameUrl());

        createChat(hive,"01/01/2000","jonathan","cassini91");

        createChat(hive,"01/05/2001","jonathan","serezy");

        hive = createHive("Chattyhive","Technology & Computers - Software development","Official chattyhive's chat for internal communication.");
        subscribeHive("jonathan",hive.getNameUrl());
        subscribeHive("cassini91",hive.getNameUrl());
        subscribeHive("trabuco",hive.getNameUrl());

        createChat(hive,"15/12/2014","jonathan","cassini91","serezy");

        createChat(hive,"01/01/2010","jonathan","serezy");

        createChat(hive,"08/11/1955","jonathan","trabuco");

        hive = createHive("Test3","Free Time - General","");
        subscribeHive("jonathan",hive.getNameUrl());

        createChat(hive,"08/11/1900","jonathan");

        hive = createHive("Test4","Free Time - General","");
        subscribeHive("cassini91",hive.getNameUrl());

        hive = createHive("Test5","Free Time - General","");
        subscribeHive("cassini91",hive.getNameUrl());
        subscribeHive("trabuco",hive.getNameUrl());

        createChat(hive,"25/02/2013","cassini91","trabuco");

        hive = createHive("Test6","Free Time - General","Empty hive.");

        /********************************************************************/
        /*                OTHER PRIVATE CHATS                               */
        /********************************************************************/
        createChat(null,"06/02/2014","cassini91","jonathan");

        createChat(null,"16/08/2004","cassini91","serezy");

        createChat(null,"16/08/2014","cassini91","serezy","jonathan");

        /********************************************************************/
        /*                USER FRIEND LIST                                  */
        /********************************************************************/

        subscribeUser("cassini91","jonathan");
        subscribeUser("cassini91","serezy");
        subscribeUser("jonathan","serezy");
        subscribeUser("jonathan","trabuco");

        /********************************************************************/
        /*                RANDOM MESSAGE TIMER                              */
        /********************************************************************/

        Thread timer = new Thread() {
            @Override
            public void run() {
                Boolean running = true;
                Random random = new Random();
                Integer messageNumber = 0;
                Integer initialMessageNumber = random.nextInt(990)+10;
                while (running) {
                    try {
                        if (messageNumber >= initialMessageNumber)
                            sleep(random.nextInt(600001));
                        else
                            sleep(500);
                    } catch (InterruptedException e) {
                        running = false;
                        continue;
                    }
                    Group group = Chats.values().toArray(new Group[Chats.size()])[random.nextInt(Chats.size())];
                    User sender = null;
                    if (group.getGroupKind() != GroupKind.HIVE)
                        sender = group.getMembers().get(random.nextInt(group.getMembers().size()));
                    else {
                        if ((HiveUserSubscriptions.containsKey(group.getParentHive().getNameUrl())) && (HiveUserSubscriptions.get(group.getParentHive().getNameUrl()) != null) && (HiveUserSubscriptions.get(group.getParentHive().getNameUrl()).size() > 0))
                            sender = LoginUser.get(HiveUserSubscriptions.get(group.getParentHive().getNameUrl()).get(random.nextInt(HiveUserSubscriptions.get(group.getParentHive().getNameUrl()).size())));
                    }
                    if (sender != null) {
                        //System.out.println(String.format("New message to group. GroupID: %s\tGroupKind: %s", group.getChannelUnicode(), group.getGroupKind().toString()));
                        //System.out.println(String.format("Sender userName: %s",sender.getUserID()));
                        MESSAGE message = new MESSAGE();
                        message.CHANNEL_UNICODE = group.getChannelUnicode();
                        message.CONFIRMED = (messageNumber < initialMessageNumber);
                        message.TIMESTAMP = new Date();
                        message.USER_ID = sender.getUserID();
                        message.CONTENT = new MESSAGE_CONTENT();
                        message.CONTENT.CONTENT_TYPE = "Text";
                        message.CONTENT.CONTENT = String.format("Message #%d",messageNumber++);
                        sendMessage(sender,group,message);
                    }
                }
            }
        };

        timer.start();
    }

    private static MESSAGE_ACK sendMessage(User sender, Group destination, MESSAGE message) {
        Message msg = new Message();
        msg.setUser(sender);
        msg.setChat(destination.getChat());
        msg.setConfirmed(message.CONFIRMED);
        msg.setId(String.format("%d",destination.getChat().getCount()));
        msg.setServerTimeStamp(new Date());
        msg.setTimeStamp(message.TIMESTAMP);
        msg.setMessageContent(new MessageContent(message.CONTENT.CONTENT_TYPE,message.CONTENT.CONTENT));

        destination.getChat().addMessageByID(msg);

        ArrayList<User> members;
        if (destination.getGroupKind() != GroupKind.HIVE)
            members = destination.getMembers();
        else {
            members = new ArrayList<User>();
            if ((ChatUserSubscriptions.containsKey(destination.getChannelUnicode())) && (ChatUserSubscriptions.get(destination.getChannelUnicode()) != null))
                for (String uName : ChatUserSubscriptions.get(destination.getChannelUnicode()))
                    if ((LoginUser.containsKey(uName)) && (LoginUser.get(uName) != null))
                        members.add(LoginUser.get(uName));
        }
        for (User receiver : members) {
            if (receiver == sender) continue;
            if (SessionIDUser.containsValue(receiver)) {
                DataProvider dataProvider = DataProvider.GetDataProvider();
                if (dataProvider != null) {
                    Server server = dataProvider.getServer();
                    if ((server != null) && (server.getServerUser() != null)) {
                        String login = server.getServerUser().getLogin();
                        if ((login != null) && (!login.isEmpty())) {
                            if ((login.equalsIgnoreCase(receiver.getEmail())) || (login.equalsIgnoreCase(receiver.getUserID()))) {
                                dataProvider.onChannelEvent(null, new PubSubChannelEventArgs(destination.getChannelUnicode(), "msg", msg.toJson(new MESSAGE()).toString()));
                            }
                        }
                    }
                }
            }
        }
        return ((MESSAGE_ACK) msg.toFormat(new MESSAGE_ACK()));
    }
    private static Group createChat(Hive hive, String creationDate, String... members) {
        GroupKind groupKind = GroupKind.PUBLIC_SINGLE;
        if ((hive == null) && (members.length > 2))
            groupKind = GroupKind.PRIVATE_GROUP;
        else if ((hive == null) && (members.length < 3))
            groupKind = GroupKind.PRIVATE_SINGLE;
        else if ((hive != null) && (members.length > 2))
            groupKind = GroupKind.PUBLIC_GROUP;
        else if ((hive != null) && (members.length < 3))
            groupKind = GroupKind.PUBLIC_SINGLE;

        Group group = new Group(groupKind,hive);
        group.setChannelUnicode(randomString.nextString());
        group.setPusherChannel(String.format("presence-%s",group.getChannelUnicode()));
        group.setCreationDate(DateFormatter.fromShortHumanReadableString(creationDate));
        Chats.put(group.getChannelUnicode(),group);
        for (String member : members) {
            group.addMember(LoginUser.get(member));
            subscribeChat(member,group.getChannelUnicode());
        }

        return group;
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
    private static Hive createHive(String name, String category, String description) {
        Hive hive = new Hive(name,randomString.nextString());
        Group publicChat = new Group(GroupKind.HIVE,hive);

        hive.setCategory(category);
        hive.setDescription(description);
        hive.setPublicChat(publicChat);

        publicChat.setChannelUnicode(hive.getNameUrl());
        publicChat.setPusherChannel(String.format("presence-%s",hive.getNameUrl()));

        Hives.put(hive.getNameUrl(), hive);
        Chats.put(publicChat.getChannelUnicode(),publicChat);

        return hive;
    }
    private static User createUser(String email, String firstName, String lastName, String publicName, String color, String birthdate, String location, String sex, Boolean privateShowAge, Boolean publicShowAge, Boolean publicShowSex, Boolean publicShowLocation, String... languages) {
        User user = new User(email);
        user.setUserID(publicName);
        user.getUserPrivateProfile().setFirstName(firstName);
        user.getUserPrivateProfile().setLastName(lastName);
        user.getUserPrivateProfile().setShowAge(privateShowAge);
        user.getUserPublicProfile().setShowSex(publicShowSex);
        user.getUserPublicProfile().setShowLocation(publicShowLocation);
        user.getUserPublicProfile().setShowAge(publicShowAge);
        user.getUserPublicProfile().setPublicName(publicName);
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
            }

            int responseCode = (response != null)?response.getKey():500;
            String responseBody = (response != null)?response.getValue():"";

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
                            server.getServerUser().setStatus(ServerStatus.ERROR);
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

                        User user = createUser(local_user_profile.EMAIL,local_user_profile.USER_BASIC_PRIVATE_PROFILE.FIRST_NAME,local_user_profile.USER_BASIC_PRIVATE_PROFILE.LAST_NAME,local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME,"#808080",DateFormatter.toShortHumanReadableString(local_user_profile.USER_PRIVATE_PROFILE.BIRTHDATE),local_user_profile.USER_PRIVATE_PROFILE.LOCATION,local_user_profile.USER_PRIVATE_PROFILE.SEX,local_user_profile.USER_PRIVATE_PROFILE.PRIVATE_SHOW_AGE,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_AGE,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_SEX,local_user_profile.USER_PUBLIC_PROFILE.PUBLIC_SHOW_LOCATION,languages);
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

        /*if (filter == null) {
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
                //Lets EXPLORE
                ArrayList<String> userHives =  null;
                if (UserHiveSubscriptions.containsKey(user.getUserID()))
                    userHives = UserHiveSubscriptions.get(user.getUserID());

                ArrayList<String> allHives = new ArrayList<String>(Hives.keySet());

                if (userHives != null)
                    allHives.removeAll(userHives);

                ArrayList<Hive> resultSet = new ArrayList<Hive>();
                for (String hiveKey : allHives)
                    resultSet.add(Hives.get(hiveKey));

                HIVE_LIST list = new HIVE_LIST();
                list.LIST = new ArrayList<HIVE>();
                responseFormats.add(list);

                for (Hive hive : resultSet)
                    list.LIST.add(((HIVE)hive.toFormat(new HIVE())));

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
                        Group chat = Chats.get(message.CHANNEL_UNICODE);
                        responseFormats.add(sendMessage(user,chat,message));
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
                        Chat chat = Chats.get(chatId.CHANNEL_UNICODE).getChat();
                        ArrayList<Message> resultList = new ArrayList<Message>();

                        String lastMessageID = null;
                        if ((filter.LAST_MESSAGE_ID == null) || (filter.LAST_MESSAGE_ID.isEmpty()) || (filter.LAST_MESSAGE_ID.equalsIgnoreCase("LAST")))
                            lastMessageID = chat.getLastMessage().getId();
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
                            messageCount = lastMessage-firstMessage;

                        for (int i = lastMessage; ((i>firstMessage) && ((lastMessage-i)<messageCount)); i--) {
                            resultList.add(chat.getMessageByID(String.format("%d",i)));
                        }

                        list.MESSAGES = new ArrayList<MESSAGE>();
                        for (Message msg : resultList)
                            list.MESSAGES.add((MESSAGE)msg.toFormat(new MESSAGE()));

                        if ((firstMessage > -1) || (filter.START_MESSAGE_ID.equalsIgnoreCase("FIRST"))) {
                            list.NUMBER_MESSAGES = lastMessage-firstMessage-messageCount;
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
                        Group chatInfo = Chats.get(chatId.CHANNEL_UNICODE);
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
                        if ((Chats.containsKey(channelUnicode)) && (Chats.get(channelUnicode).getChat() != null) && (Chats.get(channelUnicode).getChat().getCount() > 0))
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
