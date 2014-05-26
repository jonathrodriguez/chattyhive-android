package com.chattyhive.backend;

/**
 * Created by Jonathan on 22/11/13.
 * This is a static class to provide access to different application parameters.
 * There are also static methods to provide constructed application information.
 */
public final class StaticParameters {
    private StaticParameters() {}

    public static final Boolean BackgroundService = true;

    public static final String ProjectName = "chattyhive"; //The name of the project.
    public static final String ApplicationBase = "android"; //The name of the application.
    public static final String ApplicationVersion = "0.2.0"; //Application version.
    public static final String ServerBase = "server"; //A name to identify the server.
    public static final String ServerVersion = "0.2.0"; //The server version for which application is designed.

    public static final String DefaultServerAppName = "chtest3"; //Default server application name. In final release this will be set to "public" and in final beta to "private".
    public static final String DefaultServerHost = "herokuapp.com"; //Domain where server application can be found.
    public static final String DefaultServerAppProtocol = "http"; //Connection protocol. This may evolve to "https".

    public static final Boolean StandAlone = false; //Used for debug purposes. When set to TRUE application will not connect to server.
    /**
     * This method provides a string with a UserAgent to be set in http/https connections. This string provides information about application version and server version supported.
     * In future server versions, this string MAY be get into account to provide responses according to supported version.
     * @return String. User-Agent header value.
     */
    public static final String UserAgent() {
        return ProjectName.concat(";").concat(ApplicationBase).concat("/").concat(ApplicationVersion).concat(";").concat(ServerBase).concat("/").concat(ServerVersion);
    }
}
