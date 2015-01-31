package com.chattyhive.backend.contentprovider.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.contentprovider.AvailableCommands;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private UserSession userSession;

    private String appName = "";
    private String appProtocol = "";
    private String host = "";

    public String getAppName() {
        return this.appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }
    public UserSession getUserSession() {
        return this.userSession;
    }
    /************************************************************************/
    /*                           CONSTRUCTORS                               */
    /************************************************************************/

    public Server(AbstractMap.SimpleEntry<String, String> loginInfo, String appName) {
        if (loginInfo != null)
            this.userSession = new UserSession(loginInfo.getKey(),loginInfo.getValue());
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
        RunCommand(AvailableCommands.StartSession, null, null, true, null);
    }
    public void Login() {
        if (userSession == null) return;
        final Format[] loginFormats = Format.getFormat(this.userSession.toJson());
        RunCommand(AvailableCommands.Login,null,null,true,loginFormats);
    }

    public void Connect() {
        this.StartSession();
        this.Login();
    }

    public void RunCommand(AvailableCommands command, final Format... formats) {
        this.RunCommand(command,null,null,formats);
    }

    private Boolean recursiveTestCookiePrerequisites(final ServerCommand serverCommand, final Format... formats) {
        if (!serverCommand.checkCookies()) {
            List<Format> formatList = Arrays.asList(formats);
            if (this.userSession != null)
                formatList.addAll(Arrays.asList(Format.getFormat(this.userSession.toJson())));
            Format[] newFormats = formatList.toArray(new Format[formatList.size()]);

            ArrayList<String> unsatisfyingCookies = serverCommand.getUnsatisfyingCookies();
            for (String unsatisfyingCookie : unsatisfyingCookies) {
                ArrayList<AvailableCommands> commandsForCookie = ServerCommand.GetCommandForCookie(unsatisfyingCookie);
                for (AvailableCommands commandForCookie : commandsForCookie) {
                    ServerCommand serverCommandForCookie = ServerCommand.GetCommand(commandForCookie);
                    if (serverCommandForCookie == null) { continue; }
                    if (!serverCommandForCookie.checkFormats(newFormats)) { continue; }

                    if (!serverCommandForCookie.checkCookies())
                        recursiveTestCookiePrerequisites(serverCommandForCookie,newFormats);
                    else {
                        RunCommand(commandForCookie,null,true,newFormats);
                        break;
                    }
                }
            }
        } else {
            return true;
        }
        return recursiveTestCookiePrerequisites(serverCommand,formats);
    }

    public void RunCommand(final AvailableCommands command, final EventHandler<CommandCallbackEventArgs> Callback, Object CallbackAdditionalData, final Format... formats) {
        RunCommand(command,Callback,CallbackAdditionalData,false,formats);
    }

    private Boolean RunCommand(final AvailableCommands command, final EventHandler<CommandCallbackEventArgs> Callback, final Object CallbackAdditionalData, boolean waitForEnd, final Format... formats) {
        final ServerCommand serverCommand = ServerCommand.GetCommand(command);
        if (serverCommand == null) { return false; }
        if (!serverCommand.checkFormats(formats)) { return false; }

        if (!serverCommand.checkCookies())
            recursiveTestCookiePrerequisites(serverCommand,formats);

        final boolean[] result = new boolean[1];

        final Server thisServer = (StaticParameters.StandAlone)?this:null;

        Thread thread = new Thread() {
            @Override
            public void run() {
                int retryCount = 0;
                if (StaticParameters.StandAlone) {
                    result[0] = StandAloneServer.ExecuteCommand(thisServer, serverCommand.getCommand(), Callback, CallbackAdditionalData, retryCount, formats);
                } else {
                    result[0] = RunCommand(serverCommand, Callback, CallbackAdditionalData, retryCount, formats);
                }
                if (!result[0]) {
                    //TODO: Test connection availability.
                    if ((!DataProvider.isConnectionAvailable()) && (!StaticParameters.StandAlone)) {
                        //There is no network. What to do with pending command?
                        System.out.println("No network available.");
                    } else {
                        //Some strange error happened. What to do with this error?
                        System.out.println("Server error.");
                    }
                }
            }
        };

        thread.start();
        try {
            if (waitForEnd) {
                thread.join();
                return result[0];
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Boolean RunCommand (ServerCommand serverCommand, EventHandler<CommandCallbackEventArgs> Callback, Object CallbackAdditionalData, int retryCount, Format... formats) {
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
            int startSessionTries = 1;

            while ((csrfCookie == null) && (startSessionTries > 0)) {
                CookieManager cookieManager = (CookieManager) CookieHandler.getDefault();
                CookieStore cookieStore = cookieManager.getCookieStore();
                List<HttpCookie> cookies = cookieStore.getCookies();

                if (cookies != null) {
                    for (HttpCookie cookie : cookies)
                        if (cookie.getName().equalsIgnoreCase("csrftoken")) {
                            csrfCookie = cookie;
                        }
                }
                if ((csrfCookie != null) && (csrfCookie.hasExpired())) {
                    if (startSessionTries <= 3) {
                        StartSession();
                        startSessionTries++;
                    } else {
                        return false;
                    }
                } else
                    startSessionTries--;
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

                if (serverCommand.getCommand() == AvailableCommands.StartSession) {
                    if (CsrfTokenChanged != null)
                        CsrfTokenChanged.fire(this,EventArgs.Empty());
                    return true;
                }

                for (Format format : receivedFormats)
                    if (format instanceof COMMON) {
                        if (((COMMON) format).STATUS.equalsIgnoreCase("OK")) {
                            result = true;
                            if (Callback != null)
                                Callback.Invoke(httpURLConnection, new CommandCallbackEventArgs(serverCommand.getCommand(), Arrays.asList(receivedFormats), (formats != null) ? Arrays.asList(formats) : null, CallbackAdditionalData));
                            else if (responseEvent != null)
                                responseEvent.fire(httpURLConnection, new FormatReceivedEventArgs(Arrays.asList(receivedFormats)));

                            if (serverCommand.getCommand() == AvailableCommands.Login){
                                if (onConnected != null)
                                    onConnected.fire(httpURLConnection, new ConnectionEventArgs(true));
                                return true;
                            }
                        } else if (((COMMON) format).STATUS.equalsIgnoreCase("SESSION EXPIRED")) {
                            userSession.setStatus(ServerStatus.EXPIRED);
                            Login();
                            result = RunCommand(serverCommand, Callback, CallbackAdditionalData, retryCount + 1, formats);
                        } else {
                            //TODO: Check COMMON for operation Error and set result here.
                            userSession.setStatus(ServerStatus.ERROR);
                        }
                        break;
                    }
            } else if (responseCode == 403) { //CSRF-Token error.
                StartSession();
                result = RunCommand(serverCommand, Callback, CallbackAdditionalData, retryCount + 1, formats);
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
