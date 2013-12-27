package com.chattyhive.backend;

/**
 * Created by Jonathan on 22/11/13.
 */
public final class StaticParameters {
    private StaticParameters() {}

    public static final String ProjectName = "chattyhive";
    public static final String ApplicationBase = "android";
    public static final String ApplicationVersion = "0.1.0";
    public static final String ServerBase = "server";
    public static final String ServerVersion = "0.1.0";

    public static final String DefaultServerAppName = "chdev2";
    public static final String DefaultServerHost = "herokuapp.com";
    public static final String DefaultServerAppProtocol = "http";

    public static final String UserAgent() {
        return ProjectName.concat(";").concat(ApplicationBase).concat("/").concat(ApplicationVersion).concat(";").concat(ServerBase).concat("/").concat(ServerVersion);
    }
}
