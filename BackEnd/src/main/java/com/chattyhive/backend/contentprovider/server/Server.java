package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            e.printStackTrace();
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

    public JsonElement ExploreHives(String jsonParams) {
        String method = ((jsonParams != null) && (!jsonParams.isEmpty()))?"POST":"GET";

        String _function = "android.explore";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection(method,_url,this._serverUser,jsonParams,"");

        JsonObject responseJsonObject = null;

        try {
            ServerResponse response = asyncHttpURLConnection.getServerResponse();
            if (response.getResponseCode() != 200) return null;

            String res = response.getBodyData();
            res=res.replace("\\\"","\"");
            res=res.replace("\"{","{");
            res=res.replace("}\"","}");
            try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonResponse = jsonParser.parse(res);
                responseJsonObject = jsonResponse.getAsJsonObject();
                this._serverUser.setStatus(ServerStatus.valueOf(responseJsonObject.get("status").getAsString()));
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.ERROR);
            }
        } catch (InterruptedException e) {
            return null;
        }

        if ((this._serverUser.getStatus() != ServerStatus.OK) && (this._serverUser.getStatus() != ServerStatus.LOGGED)) {
            return null;
        }

        if (responseJsonObject != null) {
            return responseJsonObject.get("hives");
        }

        return null;
    }

    public Boolean JoinHive(String jsonParams) {
        if (StaticParameters.StandAlone) {
            return true;
        }

        Boolean result = true;
        String _function = "android.join";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function);

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection("POST",_url,this._serverUser,jsonParams,"");

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
                    this.JoinHive(jsonParams);
                }
            } catch (Exception e) {
                this._serverUser.setStatus(ServerStatus.ERROR);
            }
        } catch (InterruptedException e) {
            result = false;
        }
        return result;
    }


    private Event<FormatReceivedEventArgs> responseEvent;

    public void SubscribeToFormatReceivedEvent(EventHandler<FormatReceivedEventArgs> eventHandler) {
        if (responseEvent == null)
            responseEvent = new Event<FormatReceivedEventArgs>();
        responseEvent.add(eventHandler);
    }

    public Boolean unsubscribeFromFormatReceivedEvent(EventHandler<FormatReceivedEventArgs> eventHandler) {
        Boolean result = false;
        if (this.responseEvent != null) {
            result = this.responseEvent.remove(eventHandler);
            if (this.responseEvent.count() == 0)
                this.responseEvent = null;
        }
        return result;
    }

    public void RunCommand(ServerCommand.AvailableCommands command, final Format... formats) {
        this.RunCommand(command,null,formats);
    }

    public void RunCommand(ServerCommand.AvailableCommands command, final EventHandler<CommandCallbackEventArgs> Callback, final Format... formats) {
        if (StaticParameters.StandAlone) { return; }

        ServerCommand serverCommand = ServerCommand.GetCommand(command);
        if (serverCommand == null) { return; }
        if (!serverCommand.checkFormats(formats)) { return; }

        final String Url = String.format("%s://%s.%s/%s",_appProtocol,_appName,_host,serverCommand.getUrl(formats));
        final String BodyData = serverCommand.getBodyData(formats);
        final String Method = serverCommand.getMethod();

        new Thread() {
            @Override
            public void run() {

                try {
                    URL url = new URL(Url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod(Method);
                    httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

                    if ((BodyData != null) && (!BodyData.isEmpty()))
                        httpURLConnection.addRequestProperty("Content-Type","application/json");

                    String Cookies = _serverUser.getCookies();
                    httpURLConnection.setRequestProperty("Cookie",Cookies);

                    HttpCookie csrfCookie = _serverUser.getCookie("csrftoken");
                    if (csrfCookie != null) {
                        httpURLConnection.setRequestProperty("X-CSRFToken",csrfCookie.getValue());
                    }

                    if ((Method.equalsIgnoreCase("POST")) && (BodyData != null) && (!BodyData.isEmpty())) {
                        httpURLConnection.setDoOutput(true);
                        DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                        wr.writeUTF(BodyData);
                        wr.flush();
                        wr.close();
                    }

                    int responseCode = httpURLConnection.getResponseCode();

                    BufferedReader inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = inputReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    inputReader.close();

                    String responseBody = response.toString();

                    Format[] receivedFormats = null;

                    if (responseCode == 200) {
                        List<String> setCookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
                        if (setCookies != null) {
                            for (String setCookie : setCookies) {
                                List<HttpCookie> cookies = HttpCookie.parse(setCookie);
                                for (HttpCookie cookie : cookies) {
                                    _serverUser.setCookie(cookie);
                                }
                            }
                        }

                        receivedFormats = Format.getFormat(new JsonParser().parse(responseBody));
                        //TODO: Check COMMON for operation Status.
                    }

                    System.out.println(String.format("Code: %d\n%s",responseCode,responseBody));

                    if (Callback != null)
                        Callback.Invoke(httpURLConnection, new CommandCallbackEventArgs(Arrays.asList(receivedFormats),Arrays.asList(formats)));

                    if (responseEvent != null)
                        responseEvent.fire(httpURLConnection, new FormatReceivedEventArgs(Arrays.asList(receivedFormats)));

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void Disconnect() {
    }
}
