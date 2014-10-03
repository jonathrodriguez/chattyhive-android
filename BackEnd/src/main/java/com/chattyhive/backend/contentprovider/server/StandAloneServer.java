package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Chats.Group;
import com.chattyhive.backend.businessobjects.Chats.GroupKind;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.businessobjects.Chats.Messages.Message;
import com.chattyhive.backend.businessobjects.Chats.Messages.MessageContent;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.CSRF_TOKEN;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.util.RandomString;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
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

            if (StaticParameters.StandAloneDataInitialization) InitializeData();
        }
    }

    private static void InitializeData() {
        /**************/
        /* Inner vars */
        /**************/
        User user;
        Hive hive;
        Group group;

        /********************************************************************/
        /*                USERS                                             */
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

        group = createChat(hive,"01/01/2000","jonathan","cassini91");

        group = createChat(hive,"01/05/2001","jonathan","serezy");

        hive = createHive("Chattyhive","Technology & Computers - Software development","Official chattyhive's chat for internal communication.");
        subscribeHive("jonathan",hive.getNameUrl());
        subscribeHive("cassini91",hive.getNameUrl());
        subscribeHive("trabuco",hive.getNameUrl());

        group = createChat(hive,"15/12/2014","jonathan","cassini91","serezy");

        group = createChat(hive,"01/01/2010","jonathan","serezy");

        group = createChat(hive,"08/11/1955","jonathan","trabuco");

        hive = createHive("Test3","Free Time - General","");
        subscribeHive("jonathan",hive.getNameUrl());

        group = createChat(hive,"08/11/1900","jonathan");

        hive = createHive("Test4","Free Time - General","");
        subscribeHive("cassini91",hive.getNameUrl());

        hive = createHive("Test5","Free Time - General","");
        subscribeHive("cassini91",hive.getNameUrl());
        subscribeHive("trabuco",hive.getNameUrl());

        group = createChat(hive,"25/02/2013","cassini91","trabuco");

        hive = createHive("Test6","Free Time - General","Empty hive.");

        /********************************************************************/
        /*                OTHER PRIVATE CHATS                               */
        /********************************************************************/
        group = createChat(null,"06/02/2014","cassini91","jonathan");

        group = createChat(null,"16/08/2004","cassini91","serezy");

        group = createChat(null,"16/08/2014","cassini91","serezy","jonathan");

        /********************************************************************/
        /*                RANDOM MESSAGE TIMER                              */
        /********************************************************************/
        Thread timer = new Thread() {
            @Override
            public void run() {
                Boolean running = true;
                Random random = new Random();
                Integer messageNumber = 0;
                Integer initialMessageNumber = random.nextInt(999)*10;
                while (running) {
                    try {
                        if (messageNumber < initialMessageNumber)
                            sleep(random.nextInt(600001));
                    } catch (InterruptedException e) {
                        running = false;
                        continue;
                    }
                    Group group = Chats.values().toArray(new Group[Chats.size()])[random.nextInt(Chats.size())];
                    User sender = null;
                    if (group.getGroupKind() != GroupKind.HIVE)
                        sender = group.getMembers().get(random.nextInt(group.getMembers().size()));
                    else {
                        sender = LoginUser.get(HiveUserSubscriptions.get(group.getParentHive().getNameUrl()).get(random.nextInt(HiveUserSubscriptions.get(group.getParentHive().getNameUrl()).size())));
                    }
                    if (sender != null) {
                        MESSAGE message = new MESSAGE();
                        message.CHANNEL_UNICODE = group.getChannelUnicode();
                        message.CONFIRMED = false;
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

        return ((MESSAGE_ACK)msg.toFormat(new MESSAGE_ACK()));
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
        for (String member : members) {
            group.addMember(LoginUser.get(member));
            subscribeChat(member,group.getChannelUnicode());
        }

        return group;
    }
    private static void subscribeHive(String userLogin, String hiveNameURL) {
        if (!HiveUserSubscriptions.containsKey(hiveNameURL))
            HiveUserSubscriptions.put(hiveNameURL,new ArrayList<String>());
        if (!UserHiveSubscriptions.containsKey(userLogin))
            UserHiveSubscriptions.put(userLogin,new ArrayList<String>());

        HiveUserSubscriptions.get(hiveNameURL).add(userLogin);
        UserHiveSubscriptions.get(userLogin).add(hiveNameURL);
    }
    private static void subscribeChat(String userLogin, String chatChannelUnicode) {
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

    public static Boolean ExecuteCommand(Server server, ServerCommand serverCommand, EventHandler<CommandCallbackEventArgs> Callback,int retryCount, Format... formats) {
        if (retryCount >= 3) return false;

        Boolean result = false;

        try {

        AbstractMap.SimpleEntry<Integer,String> response;

        switch (serverCommand.getCommand()) {
            case StartSession:
                response = StartSession(server,formats);
                break;
            case Login:
                response = Login(server,formats);
                break;
            default:
                response = null;
                break;
        }

        int responseCode = (response != null)?response.getKey():-1;
        String responseBody = (response != null)?response.getValue():"";

        System.out.println(String.format("StandAloneServer -> Request: %s\nCode: %d\n%s",serverCommand.getCommand().toString(), responseCode, responseBody));

        Format[] receivedFormats = null;

        if (responseCode == -1) {
            result = false;
            DataProvider.setConnectionAvailable(false);
        } else if (responseCode == 200) {

            String preparedResponseBody = responseBody.replace("\\\"","\"").replace("\"{","{").replace("}\"","}").replaceAll("\"PROFILE\": \"(.*?)\"","\"PROFILE\": {\"PUBLIC_NAME\": \"$1\"}");
            receivedFormats = Format.getFormat(new JsonParser().parse(preparedResponseBody));

            if (serverCommand.getCommand() == AvailableCommands.StartSession) {
                if (server.CsrfTokenChanged != null)
                    server.CsrfTokenChanged.fire(server, EventArgs.Empty());
                return true;
            }

            for (Format format : receivedFormats)
                if (format instanceof COMMON) {
                    if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                        result = true;
                        if (Callback != null)
                            Callback.Invoke(server, new CommandCallbackEventArgs(serverCommand.getCommand(),Arrays.asList(receivedFormats), (formats!=null)?Arrays.asList(formats):null, null));
                        else if ((serverCommand.getCommand() == AvailableCommands.Login) && (server.onConnected != null)) {
                            server.onConnected.fire(server, new ConnectionEventArgs(true));
                            return true;
                        } else if (serverCommand.getCommand() == AvailableCommands.Login)
                            return true;
                        else if (server.responseEvent != null)
                            server.responseEvent.fire(server, new FormatReceivedEventArgs(Arrays.asList(receivedFormats)));
                    } else if (((COMMON) format).STATUS.equalsIgnoreCase("SESSION EXPIRED")) {
                        server.getServerUser().setStatus(ServerStatus.EXPIRED);
                        server.Login();
                        result = ExecuteCommand(server, serverCommand, Callback, retryCount + 1, formats);
                    } else {
                        //TODO: Check COMMON for operation Error and set result here.
                        server.getServerUser().setStatus(ServerStatus.ERROR);
                    }
                    break;
                }
        } else if (responseCode == 403) { //CSRF-Token error.
            server.StartSession();
            result = ExecuteCommand(server, serverCommand, Callback, retryCount + 1, formats);
        }

        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return result;
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
        for (Format format : formats)
            if (format instanceof LOGIN)
                login = (LOGIN)format;

        COMMON common = new COMMON();

        if (login == null) {
            common.STATUS = "ERROR";
            common.ERROR = -1;
        } else {
            try {
                CookieStore cookieStore = ((CookieManager)CookieHandler.getDefault()).getCookieStore();
                List<HttpCookie> cookies = cookieStore.get(new URI(String.format("%s://%s.%s", StaticParameters.DefaultServerAppProtocol,server.getAppName(),StaticParameters.DefaultServerHost)));
                HttpCookie csrfCookie = null;


                for (HttpCookie cookie : cookies)
                    if (cookie.getName().equalsIgnoreCase(CSRFTokenCookie))
                        csrfCookie = cookie;

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
                responseCode = -1;
            }
        }

        if (responseCode == 200)
            responseBody = common.toJSON().toString();

        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }

    /*private static AbstractMap.SimpleEntry<Integer, String> Command(Server server, Format... formats) {
        Integer responseCode = null;
        String responseBody = null;



        return new AbstractMap.SimpleEntry<Integer,String>((responseCode != null)?responseCode:-1,(responseBody != null)?responseBody:"");
    }*/



}
