package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventHandler;
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


    private Event<ConnectionEventArgs> onConnected;

    public void SubscribeToOnConnected(EventHandler<ConnectionEventArgs> eventHandler){
        if (onConnected == null)
            onConnected = new Event<ConnectionEventArgs>();
        onConnected.add(eventHandler);
    }

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

        if (StaticParameters.StandAlone) {
            this._serverUser.setStatus(ServerStatus.LOGGED);
            return true;
        }

        String _function = "android.start_session";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection("GET",_url,this._serverUser,"","");

        try {
            ServerResponse response = asyncHttpURLConnection.getServerResponse();
            if (response.getResponseCode() != 200)
                return false;
        } catch (InterruptedException e) {
            return false;
        }


        _function = "android.login";
        _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

       String _bodyData = this._serverUser.toJson().toString();

        asyncHttpURLConnection = new AsyncHttpURLConnection("POST",_url,this._serverUser,_bodyData,"");

        JsonElement jsonElement = null;

        try {
            ServerResponse response = asyncHttpURLConnection.getServerResponse();
            if (response.getResponseCode() != 200) {
                return false;
            }
            String res = response.getBodyData();
            res=res.replace("\\\"","\"");
            res=res.replace("\"{","{");
            res=res.replace("}\"","}");
            try {
                JsonParser jsonParser = new JsonParser();
                jsonElement = jsonParser.parse(res);
                JsonObject responseJsonObject = jsonElement.getAsJsonObject();
                this._serverUser.setStatus(ServerStatus.valueOf(responseJsonObject.get("status").getAsString()));
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.ERROR);
            }
        } catch (InterruptedException e) {
            result = false;
        }

        if ((this._serverUser.getStatus() != ServerStatus.OK) && (this._serverUser.getStatus() != ServerStatus.LOGGED)) {
            result = false;
        }

        if ((result) && (this.onConnected != null))
            this.onConnected.fire(this,new ConnectionEventArgs(jsonElement));

        return result;
    }

    /**
     * Sends a message to the server.
     * @param jsonMSG a string representing the message to be sent.
     * @return a boolean value indicating whether the operation has correctly been done.
     */
    public Boolean SendMessage(String jsonMSG) {

        if (StaticParameters.StandAlone) {
            return true;
        }

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
                    this.Connect(); // !!!!?????
                    this.SendMessage(jsonMSG);
                }
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.ERROR);
            }
        } catch (InterruptedException e) {
            result = false;
        }
        return result;
    }
}
