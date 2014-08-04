package com.chattyhive.backend;

/**
 * Created by Jonathan on 22/11/13.
 * This is a static class to provide access to different application parameters.
 * There are also static methods to provide constructed application information.
 */
public final class StaticParameters {
    private StaticParameters() {}

    public static final int IntervalToChatSync = 300000; //Milliseconds between chat_sync commands.

    public static final Boolean StandAlone = true; //Used for debug purposes. When set to TRUE application will not connect to server.

    public static final Boolean BackgroundService = true; //When set to false there will not be a background service, so no notifications available.

    public static final String ProjectName = "chattyhive"; //The name of the project.
    public static final String ApplicationBase = "android"; //The name of the application.
    public static final String ApplicationVersion = "0.3.0"; //Application version.
    public static final String ServerBase = "server"; //A name to identify the server.
    public static final String ServerVersion = "0.5.0"; //The server version for which application is designed.

    public static final String DefaultServerAppName = "chtest2"; //Default server application name. In final release this will be set to "public" and in final beta to "private".
    public static final String DefaultServerHost = "herokuapp.com"; //Domain where server application can be found.
    public static final String DefaultServerAppProtocol = "http"; //Connection protocol. This may evolve to "https".

    public static final int MaxLocalMessages = -1; //Maximum number of local stored messages. Any negative number indicates to save all messages. 0 indicates to not use local storage. Any positive number is interpreted as the exact number of messages to save.
    public static final int NumberMessagesQuery = 4; //Number of message to retrieve at each query. Default value will be 100.

    /**
     * This method provides a string with a UserAgent to be set in http/https connections. This string provides information about application version and server version supported.
     * In future server versions, this string MAY be get into account to provide responses according to supported version.
     * @return String. User-Agent header value.
     */
    public static final String UserAgent() {
        return ProjectName.concat(";").concat(ApplicationBase).concat("/").concat(ApplicationVersion).concat(";").concat(ServerBase).concat("/").concat(ServerVersion);
    }
}
