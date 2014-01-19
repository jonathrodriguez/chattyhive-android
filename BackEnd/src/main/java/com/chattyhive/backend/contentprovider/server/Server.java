package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Jonathan on 20/11/13.
 * This class represents the communication with the server.
 */
public class Server {
    private ServerUser _serverUser;

    private String _appName = "";
    private String _appProtocol = "";
    private String _host = "";

    /**
     * Retrieves the server application name to which this instance is connected.
     * @return a string containing the server app name.
     */
    public String getAppName() {
        return this._appName;
    }

    /**
     * Changes the server application to which to connect.
     * @param appName the new server application
     */
    public void setAppName(String appName) { this._appName = appName; }

    /**
     * Retrieves the server user.
     * @return
     */
    public ServerUser getServerUser() {
        return this._serverUser;
    }

    /**
     * Establishes the server user.
     * @param serverUser
     */
    public void setServerUser(ServerUser serverUser) {
        this._serverUser = serverUser;
    }
    /**
     * Public constructor.
     * @param serverUser a Server user object with the user data to be used.
     * @param appName a string with the name of the server application to which to connect.
     */
    public Server(ServerUser serverUser, String appName) {
        this._serverUser = serverUser;
        this._appName = appName;
        this._appProtocol = StaticParameters.DefaultServerAppProtocol;
        this._host = StaticParameters.DefaultServerHost;
        this._serverUser.setStatus(ServerStatus.DISCONNECTED);
    }

    /**
     * Public constructor. This will only work with server 0.1 because next sever versions uses passwords.
     * @param username a string with the username to use as login.
     * @param appName a string with the name of the server application to which to connect.
     */
    public Server(String username, String appName) {
        this._serverUser = new ServerUser(username,"");
        this._appName = appName;
        this._appProtocol = StaticParameters.DefaultServerAppProtocol;
        this._host = StaticParameters.DefaultServerHost;
        this._serverUser.setStatus(ServerStatus.DISCONNECTED);
    }

    /**
     * Perform connection to the server.
     * @return a boolean value indicating whether the connection has been made.
     */
    public Boolean Connect() {
        Boolean result = true;
        String _function = "android.login";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

       String _rest = this._serverUser.getLogin();

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection("GET",_url,this._serverUser,"",_rest);

        try {
            ServerResponse response = asyncHttpURLConnection.getServerResponse();
            if (response.getResponseCode() != 200) {
                return false;
            }
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response.getBodyData());
                JsonObject responseJsonObject = jsonElement.getAsJsonObject();
                this._serverUser.setStatus(ServerStatus.valueOf(responseJsonObject.get("status").getAsString()));
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.LOGGED);
            }
        } catch (InterruptedException e) {
            result = false;
        }
        return result;
    }

    /**
     * Sends a message to the server.
     * @param jsonMSG a string representing the message to be sent.
     * @return a boolean value indicating whether the operation has correctly been done.
     */
    public Boolean SendMessage(String jsonMSG) {
        Boolean result = true;
        String _function = "android.chat";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

        //JsonObject jsonObject = new JsonObject();
        //jsonObject.addProperty("timestamp", TimestampFormatter.toString(new Date()));
        //jsonObject.addProperty("message",msg);
        //
        // TODO: Officially message should be sent in JSON.
        //
        //String jsonString = jsonObject.toString();
        //String ts = TimestampFormatter.toString(new Date());
        //String jsonString = "message=".concat(msg.replace("+","%2B").replace(" ", "+")).concat("&timestamp=").concat(ts.replace(":","%3A").replace("+","%2B").replace(" ","+"));

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection("POST",_url,this._serverUser,jsonMSG,"");

        try {
            ServerResponse response = asyncHttpURLConnection.getServerResponse();
            if (response.getResponseCode() != 200) {
                return false;
            }
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response.getBodyData());
                JsonObject responseJsonObject = jsonElement.getAsJsonObject();
                ServerStatus status = ServerStatus.valueOf(responseJsonObject.get("status").getAsString());
                if (status != this._serverUser.getStatus()) {
                    this._serverUser.setStatus(status);
                    this.Connect();
                    this.SendMessage(jsonMSG);
                }
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.LOGGED);
            }
        } catch (InterruptedException e) {
            result = false;
        }
        return result;
    }
}
