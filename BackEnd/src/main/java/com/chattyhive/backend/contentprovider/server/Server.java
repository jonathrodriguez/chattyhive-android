package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Jonathan on 20/11/13.
 */
public class Server {
    private ServerUser _serverUser;

    private String _username;
    private String _appName = "";
    private String _appProtocol = "";
    private String _host = "";

    private ServerStatus _status;

    public String getAppName() {
        return this._appName;
    }

    public Server(ServerUser serverUser, String appName) {
        this._serverUser = serverUser;
        this._appName = appName;
        this._appProtocol = StaticParameters.DefaultServerAppProtocol;
        this._host = StaticParameters.DefaultServerHost;
        this._status = ServerStatus.DISCONNECTED;
    }

    public Server(String username, String appName) {
        this._serverUser = new ServerUser(username,"");
        this._appName = appName;
        this._appProtocol = StaticParameters.DefaultServerAppProtocol;
        this._host = StaticParameters.DefaultServerHost;
        this._status = ServerStatus.DISCONNECTED;
    }

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
