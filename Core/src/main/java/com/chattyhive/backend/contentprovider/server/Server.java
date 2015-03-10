package com.chattyhive.backend.ContentProvider.server;


import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.backend.ContentProvider.SynchronousDataPath.CommandDefinition;
import com.chattyhive.backend.Util.Events.Event;
import com.chattyhive.backend.Util.Events.EventArgs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 20/11/13.
 * This class represents the communication with the server.
 */
public class Server {
    /************************************************************************/
    /*                          MEMBER FIELDS                               */
    /************************************************************************/
    public Event<EventArgs> CSRFTokenChanged;

    private String appName = "";
    private String appProtocol = "";
    private String host = "";

    private String CSRFToken = "";
    private void setCSRFToken(String CSRFToken) {
        if (this.CSRFToken.equals(CSRFToken)) return;

        this.CSRFToken = CSRFToken;
        if (this.CSRFTokenChanged != null)
            this.CSRFTokenChanged.fire(this, EventArgs.Empty());
    }
    public String getCSRFToken() {
        return this.CSRFToken;
    }

    public String getAppName() {
        return this.appName;
    }
    public String getAppProtocol() {
        return this.appProtocol;
    }
    public String getHost() {
        return this.host;
    }
    /************************************************************************/
    /*                           CONSTRUCTORS                               */
    /************************************************************************/
    public Server() {
        this(null, null, null, null);
    }

    public Server(String CSRFToken) {
        this(CSRFToken,null,null,null);
    }

    public Server(String CSRFToken,String AppName) {
        this(CSRFToken, AppName, null, null);
    }

    public Server(String CSRFToken,String AppName,String AppProtocol,String Host) {
        if (CSRFToken != null)
            this.CSRFToken = CSRFToken;

        if ((AppName != null) && (!AppName.isEmpty()))
            this.appName = AppName;
        else
            this.appName = StaticParameters.DefaultServerAppName;

        if ((AppProtocol != null) && (!AppProtocol.isEmpty()))
            this.appProtocol = AppProtocol;
        else
            this.appProtocol = StaticParameters.DefaultServerAppProtocol;

        if ((Host != null) && (!Host.isEmpty()))
            this.host = Host;
        else
            this.host = StaticParameters.DefaultServerHost;

        this.InitializeEvents();
    }

    private void InitializeEvents() {
        this.CSRFTokenChanged = new Event<EventArgs>();
    }
    /************************************************************************/
    /*                              METHODS                                 */
    /************************************************************************/

    public void StartSession() {
        CommandDefinition commandDefinition = CommandDefinition.GetCommand(AvailableCommands.StartSession);
        ServerResponse result = this.ExecuteCommand(commandDefinition.getMethod().name(), commandDefinition.getUrl(),null,null);

        if ((result.getResultCode() == 200) && (result.getResultHeaders().containsKey("Set-Cookie"))) {
            for (String setCookie : result.getResultHeaders().get("Set-Cookie")) {
                List<HttpCookie> cookies = HttpCookie.parse(setCookie);
                for (HttpCookie cookie : cookies) {
                    if (cookie.getName() == ServerConfiguration.csrfTokenCookie) {
                        this.setCSRFToken(cookie.getValue());
                        return;
                    }
                }
            }
        } else if (result.getResultCode() == 200) {
            //TODO: No cookie retrieved. This means a server error.
        }

        //TODO: Error occurred
    }

    private ServerResponse ExecuteCommand(final String Method, final String CommandURL, final String BodyData,final HashMap<String, String> Headers) {
        final ServerResponse response = new ServerResponse();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(String.format("%s://%s.%s/%s", appProtocol, appName, host, CommandURL));

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod(Method);
                    httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

                    if ((BodyData != null) && (!BodyData.isEmpty()))
                        httpURLConnection.setRequestProperty("Content-Type", "application/json");

                    for (Map.Entry<String, String> header : Headers.entrySet()) {
                        httpURLConnection.setRequestProperty(header.getKey(),header.getValue());
                    }

                    if ((Method.equalsIgnoreCase("POST")) && (BodyData != null) && (!BodyData.isEmpty())) {
                        httpURLConnection.setDoOutput(true);
                        BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(),"UTF-8"));
                        wr.write(BodyData);
                        wr.flush();
                        wr.close();
                    }

                    response.setResultCode(httpURLConnection.getResponseCode());

                    response.setResultHeaders(httpURLConnection.getHeaderFields());

                    BufferedReader inputReader;

                    if (response.getResultCode() == 200)
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    else
                        inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));

                    String inputLine;
                    StringBuffer resultString = new StringBuffer();

                    while ((inputLine = inputReader.readLine()) != null) {
                        resultString.append(inputLine);
                    }
                    inputReader.close();

                    response.setResultBody(resultString.toString());

                    httpURLConnection.disconnect();

                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    response.setResultCode(-1);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    response.setResultCode(-2);
                } catch (IOException e) {
                    e.printStackTrace();
                    response.setResultCode(-3);
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            response.setResultCode(-4);
        }

        return response;
    }


}
