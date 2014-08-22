package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.ConnectionEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.events.FormatReceivedEventArgs;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;

import javax.security.auth.callback.Callback;

/**
 * Created by Jonathan on 20/11/13.
 * This class represents the communication with the server.
 */
public class Server {
    /************************************************************************/
    /*                          MEMBER FIELDS                               */
    /************************************************************************/
    public Event<ConnectionEventArgs> onConnected;
    public Event<FormatReceivedEventArgs> responseEvent;

    public Event<EventArgs> CsrfTokenChanged;

    private ServerUser serverUser;

    private String appName = "";
    private String appProtocol = "";
    private String host = "";

    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setServerUser(ServerUser serverUser) {
        this.serverUser = serverUser;
    }

    /************************************************************************/
    /*                           CONSTRUCTORS                               */
    /************************************************************************/
    @Deprecated
    public Server(ServerUser serverUser, String appName) {
        this.serverUser = serverUser;
        this.appName = appName;
        this.appProtocol = StaticParameters.DefaultServerAppProtocol;
        this.host = StaticParameters.DefaultServerHost;

        this.InitializeEvents();
    }

    @Deprecated
    public Server(String username, String appName) {
        this.serverUser = new ServerUser(username,"");
        this.appName = appName;
        this.appProtocol = StaticParameters.DefaultServerAppProtocol;
        this.host = StaticParameters.DefaultServerHost;

        this.InitializeEvents();
    }

    public Server(AbstractMap.SimpleEntry<String, String> loginInfo, String appName) {
        if (loginInfo != null)
            this.serverUser = new ServerUser(loginInfo.getKey(),loginInfo.getValue());
        this.appName = appName;
        this.appProtocol = StaticParameters.DefaultServerAppProtocol;
        this.host = StaticParameters.DefaultServerHost;

        this.InitializeEvents();
    }

    private void InitializeEvents() {
        this.onConnected = new Event<ConnectionEventArgs>();
        this.responseEvent = new Event<FormatReceivedEventArgs>();
        this.CsrfTokenChanged = new Event<EventArgs>();
    }
    /************************************************************************/
    /*                              METHODS                                 */
    /************************************************************************/

    public void StartSession() {
        if (StaticParameters.StandAlone) return;

        String function = "android.start_session";
        final String Url = String.format("%s://%s.%s/%s",appProtocol,appName,host,function);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Url);

                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

                    int responseCode = httpURLConnection.getResponseCode();

                    BufferedReader inputReader;

                    if (responseCode == 200)
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    else
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));

                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = inputReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    inputReader.close();

                    String responseBody = response.toString();
                    System.out.println(String.format("Request: %s\nCode: %d\n%s",url.toString(), responseCode, responseBody));

                    if (responseCode == 200) {
                        if (CsrfTokenChanged != null)
                            CsrfTokenChanged.fire(this,EventArgs.Empty());
                    }

                    httpURLConnection.disconnect();


                } catch (SocketTimeoutException e) {
                    onNetworkUnavailable();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void Login() {
        if (StaticParameters.StandAlone) return;

        String function = "android.login";
        final String Url = String.format("%s://%s.%s/%s/",appProtocol,appName,host,function);

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(Url);
                    if (serverUser == null) return;
                    String BodyData = serverUser.toJson().toString();


                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

                    if ((BodyData != null) && (!BodyData.isEmpty()))
                        httpURLConnection.addRequestProperty("Content-Type", "application/json");

                    HttpCookie csrfCookie = null;

                    while (csrfCookie == null) {
                        CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
                        CookieStore cookieStore = cookieManager.getCookieStore();
                        List<HttpCookie> cookies = cookieStore.getCookies();

                        if (cookies != null) {
                            for (HttpCookie cookie : cookies)
                                if (cookie.getName().equalsIgnoreCase("csrftoken")) {
                                    csrfCookie = cookie;
                                    break;
                                }
                        }

                        if ((csrfCookie == null) || (csrfCookie.hasExpired())) StartSession();
                    }

                    if (csrfCookie != null) {
                        httpURLConnection.setRequestProperty("X-CSRFToken", csrfCookie.getValue());
                    }

                    if ((BodyData != null) && (!BodyData.isEmpty())) {
                        httpURLConnection.setDoOutput(true);
                        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8"));
                        wr.write(BodyData);
                        wr.flush();
                        wr.close();
                    }

                    int responseCode = httpURLConnection.getResponseCode();

                    BufferedReader inputReader;

                    if (responseCode == 200)
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    else
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));

                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = inputReader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    inputReader.close();

                    String responseBody = response.toString();

                    System.out.println(String.format("Request: %s\nCode: %d\n%s",url.toString(), responseCode, responseBody));

                    if (responseCode == 200) {

                        Format[] receivedFormats = Format.getFormat(new JsonParser().parse(responseBody));

                        for (Format format : receivedFormats)
                            if (format instanceof COMMON) {
                                if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                                    serverUser.setStatus(ServerStatus.LOGGED);
                                    if (onConnected != null)
                                        onConnected.fire(httpURLConnection, new ConnectionEventArgs(true));
                                } else if (((COMMON) format).STATUS.equalsIgnoreCase("SESSION EXPIRED")) {
                                    //TODO: What happens in this case? This case has no sense.
                                    serverUser.setStatus(ServerStatus.EXPIRED);
                                } else {
                                    //TODO: Check COMMON for operation Error and set result here.
                                    serverUser.setStatus(ServerStatus.ERROR);
                                }
                                break;
                            }
                    } else if (responseCode == 403) { //CSRF-Token error.
                        StartSession();
                        Login();
                    }

                    httpURLConnection.disconnect();

                } catch (SocketTimeoutException e) {
                    onNetworkUnavailable();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Connect() {
        this.StartSession();
        this.Login();
    }

    public void RunCommand(ServerCommand.AvailableCommands command, final Format... formats) {
        this.RunCommand(command,null,formats);
    }

    public void RunCommand(final ServerCommand.AvailableCommands command, final EventHandler<CommandCallbackEventArgs> Callback, final Format... formats) {
        if (StaticParameters.StandAlone) { return; }

        final ServerCommand serverCommand = ServerCommand.GetCommand(command);
        if (serverCommand == null) { return; }
        if (!serverCommand.checkFormats(formats)) { return; }

        new Thread() {
            @Override
            public void run() {
                int retryCount = 0;
                if (!RunCommand(serverCommand,Callback,retryCount,formats)) {
                    //TODO: Test connection availability.
                    if (!DataProvider.isConnectionAvailable()) {
                        //There is no network. What to do with pending command?
                        System.out.println("No network available.");
                    } else {
                        //Some strange error happened. What to do with this error?
                        System.out.println("Server error.");
                    }
                }
            }
        }.start();
    }

    private Boolean RunCommand (ServerCommand serverCommand, EventHandler<CommandCallbackEventArgs> Callback,int retryCount, Format... formats) {
        Boolean result = false;

        if (retryCount >= 3) return false;

        try {
            URL url = new URL(String.format("%s://%s.%s/%s", appProtocol, appName, host, serverCommand.getUrl(formats)));
            String BodyData = serverCommand.getBodyData(formats);
            String Method = serverCommand.getMethod();

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(Method);
            httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

            if ((BodyData != null) && (!BodyData.isEmpty()))
                httpURLConnection.addRequestProperty("Content-Type", "application/json");

            HttpCookie csrfCookie = null;
            HttpCookie sessionCookie = null;

            int startSessionTries = 0;
            int loginTries = 0;

            while (csrfCookie == null) {
                CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
                CookieStore cookieStore = cookieManager.getCookieStore();
                List<HttpCookie> cookies = cookieStore.getCookies();

                if (cookies != null) {
                    for (HttpCookie cookie : cookies)
                        if (cookie.getName().equalsIgnoreCase("csrftoken")) {
                            csrfCookie = cookie;
                        } else if (cookie.getName().equalsIgnoreCase("sessionid")) {
                            sessionCookie = cookie;
                        }
                }
                if ((csrfCookie == null) || (csrfCookie.hasExpired())) {
                    if (startSessionTries < 3) {
                        StartSession();
                        startSessionTries++;
                    } else {
                        return false;
                    }
                }
                if ((sessionCookie == null) || (sessionCookie.hasExpired())) {
                    if (loginTries < 3) {
                        Login();
                        loginTries++;
                    } else {
                        return false;
                    }
                }
            }

            if (csrfCookie != null) {
                httpURLConnection.setRequestProperty("X-CSRFToken", csrfCookie.getValue());
            }

            if ((Method.equalsIgnoreCase("POST")) && (BodyData != null) && (!BodyData.isEmpty())) {
                httpURLConnection.setDoOutput(true);
                BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8"));
                wr.write(BodyData);
                wr.flush();
                wr.close();
            }

            int responseCode = httpURLConnection.getResponseCode();

            BufferedReader inputReader;

            if (responseCode == 200)
                inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            else
                inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = inputReader.readLine()) != null) {
                response.append(inputLine);
            }
            inputReader.close();

            String responseBody = response.toString();
            System.out.println(String.format("Request: %s\nCode: %d\n%s",url.toString(), responseCode, responseBody));

            Format[] receivedFormats = null;

            if (responseCode == 200) {

                //TODO: receivedFormats = Format.getFormat(new JsonParser().parse(responseBody));
                String preparedResponseBody = responseBody.replace("\\\"","\"").replace("\"{","{").replace("}\"","}").replaceAll("\"PROFILE\": \"(.*?)\"","\"PROFILE\": {\"PUBLIC_NAME\": \"$1\"}");
                receivedFormats = Format.getFormat(new JsonParser().parse(preparedResponseBody));

                for (Format format : receivedFormats)
                    if (format instanceof COMMON) {
                        if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                            result = true;
                            if (Callback != null)
                                Callback.Invoke(httpURLConnection, new CommandCallbackEventArgs((receivedFormats!=null)?Arrays.asList(receivedFormats):null, (formats!=null)?Arrays.asList(formats):null));
                            else if (responseEvent != null)
                                responseEvent.fire(httpURLConnection, new FormatReceivedEventArgs(Arrays.asList(receivedFormats)));
                        } else if (((COMMON) format).STATUS.equalsIgnoreCase("SESSION EXPIRED")) {
                            serverUser.setStatus(ServerStatus.EXPIRED);
                            Login();
                            result = RunCommand(serverCommand, Callback, retryCount + 1, formats);
                        } else {
                            //TODO: Check COMMON for operation Error and set result here.
                            serverUser.setStatus(ServerStatus.ERROR);
                        }
                        break;
                    }
            } else if (responseCode == 403) { //CSRF-Token error.
                StartSession();
                result = RunCommand(serverCommand, Callback, retryCount + 1, formats);
            }

            httpURLConnection.disconnect();

        } catch (SocketTimeoutException e) {
            result = false;
            onNetworkUnavailable();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public void Disconnect() {
    }

    private void onNetworkUnavailable() {
        DataProvider.setConnectionAvailable(false);
    }
}
