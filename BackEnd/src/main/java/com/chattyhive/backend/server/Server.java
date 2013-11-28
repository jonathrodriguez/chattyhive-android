package com.chattyhive.backend.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.serverside.Cookie;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jonathan on 20/11/13.
 */
public class Server {

    public enum ServerStatus {
        ERROR, //Error has occurred.
        RECEIVED, //Message received by server.
        LOGGED, //New session successfully created.
        EXPIRED //Session has expired.
    }

    private String _username;
    private String _appName = "chdev2";
    private String _appProtocol = "http";
    private String _host = "herokuapp.com";

    private Map<String, Cookie> _cookies;
    private ServerStatus _status;

    public Server(String username, String appName) {
        this._username = username;
        this._appName = appName;
        this._cookies = new HashMap<String, Cookie>();
    }

    public Boolean Connect() {
        Boolean result = false;
        String _function = "android.login";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function).concat("/").concat(this._username);

        try {
            URL url = new URL(_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                result = false;
            } else {
                result = true;

                try {
                    //String setCookies = httpURLConnection.getHeaderField("Set-Cookie");
                    List<String> setCookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
                    for (int i = 0; i < setCookies.size(); i++) {
                        Cookie cookie = new Cookie(setCookies.get(i));
                        this._cookies.put(cookie.getName(), cookie);
                    }
                } catch (NullPointerException e) {
                    result = false;
                }

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = inputReader.readLine()) != null) {
                    response.append(inputLine);
                }
                inputReader.close();

                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response.toString());
                JsonObject responseJsonObject = jsonElement.getAsJsonObject();
                //this._sessionID = responseJsonObject.get("session").getAsString();
                this._status = ServerStatus.valueOf(responseJsonObject.get("status").getAsString());
            }
        } catch (MalformedURLException e) {
            result = false;
        } catch (IOException e) {
            result = false;
        }

        return result;
    }

    public Boolean SendMessage(String msg) {
        Boolean result = false;
        String _function = "android.chat";
        String _url = _appProtocol.concat("://").concat(_appName).concat(".").concat(_host);
        _url = _url.concat("/").concat(_function).concat("/");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("timestamp",(new Date()).toString());
        jsonObject.addProperty("message",msg);
        //
        // TODO: Officially message should be sent in JSON.
        //
        //String jsonString = jsonObject.toString();
        String ts = (new Date()).toString();
        String jsonString = "message=".concat(msg.replace("+","%2B").replace(" ", "+")).concat("&timestamp=").concat(ts.replace(":","%3A").replace("+","%2B").replace(" ","+"));

        try {
            URL url = new URL(_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());
            String Cookies = "";

            Iterator it = this._cookies.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry e = (Map.Entry)it.next();

                if ((Cookies != null) && (Cookies.length() > 0)) {
                    Cookies = Cookies.concat("; ");
                }
                Cookie cookie = ((Cookie)e.getValue());
                Cookies = Cookies.concat(cookie.getName()).concat("=").concat(cookie.getValue());
            }

            if ((Cookies != null) && (Cookies.length() > 0)) {
                httpURLConnection.setRequestProperty("Cookie",Cookies);
            }
            if (this._cookies.containsKey("csrftoken")) {
                httpURLConnection.setRequestProperty("X-CSRFToken",this._cookies.get("csrftoken").getValue());
            }
            httpURLConnection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonString);
            wr.flush();
            wr.close();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode != 200) {
                result = false;
            } else {
                result = true;

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = inputReader.readLine()) != null) {
                    response.append(inputLine);
                }
                inputReader.close();

                //
                // TODO: If status isn't recovered there would be something to do.
                //
                try {
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(response.toString());
                JsonObject responseJsonObject = jsonElement.getAsJsonObject();

                ServerStatus status = ServerStatus.valueOf(responseJsonObject.get("status").getAsString());
                if (status != this._status) {
                    this._status = status;
                    this.Connect();
                    this.SendMessage(msg);
                }
                }
                catch (Exception e) {
                    result=true;
                }
            }
        } catch (MalformedURLException e) {
            result = false;
        } catch (IOException e) {
            result = false;
        }



        return result;
    }
}
