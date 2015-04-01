package com.chattyhive.Core.ContentProvider.Server;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.Command;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.CommandDefinition;
import com.chattyhive.Core.ContentProvider.SynchronousDataPath.IOrigin;
import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.StaticParameters;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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
import java.util.ArrayList;

/**
 * Created by Jonathan on 13/03/2015.
 */
public class RemoteServer implements IOrigin {

    private final String serverProtocol;
    private final String serverLocation;

    private String CSRFToken; //TODO: this member can be removed when using token based authentication.

    public RemoteServer(String serverProtocol, String serverLocation) {
        this.serverProtocol = serverProtocol;
        this.serverLocation = serverLocation;
        this.CSRFToken = "";
    }

    //TODO: this constructor will no longer be needed when using token based authentication.
    public RemoteServer(String serverProtocol, String serverLocation, String CSRFToken) {
        this.serverProtocol = serverProtocol;
        this.serverLocation = serverLocation;
        this.CSRFToken = CSRFToken;
    }

    @Override
    public void ProcessCommand(Command command, CallbackDelegate Callback, Object... callbackParameters) {
        ServerResponse response = new ServerResponse();
        try {
            URL url = new URL(String.format("%s://%s/%s", this.serverProtocol, this.serverLocation, command.getUrl()));

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(command.getCommandDefinition().getMethod().name());
            httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

            String BodyData = command.getBodyData();
            if ((BodyData != null) && (!BodyData.isEmpty()))
                httpURLConnection.setRequestProperty("Content-Type", "application/json");

            String cookieHeader = "";
            for (String requiredCookie : command.getCommandDefinition().getRequiredCookies()) { //TODO: this may change when using token based authentication.
                if (requiredCookie.equalsIgnoreCase(CommandDefinition.CSRFTokenCookie)) {
                    cookieHeader = cookieHeader.concat((cookieHeader.isEmpty())?"":"; ").concat(CommandDefinition.CSRFTokenCookie).concat("=").concat(this.CSRFToken);
                } else if (requiredCookie.equalsIgnoreCase(CommandDefinition.SessionCookie)) {
                    cookieHeader = cookieHeader.concat((cookieHeader.isEmpty())?"":"; ").concat(CommandDefinition.SessionCookie).concat("=").concat(command.getServerUser().getAuthToken(CommandDefinition.SessionCookie));
                }
            }
            if (!cookieHeader.isEmpty())
                httpURLConnection.setRequestProperty("Cookie",cookieHeader);

            if ((command.getCommandDefinition().getMethod() == CommandDefinition.Method.POST) && (BodyData != null) && (!BodyData.isEmpty())) {
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

            command.setServerResponse(response);
            command.setResultCode(response.getResultCode());

            if (command.getResultCode() == 200) {
                String preparedResponseBody = response.getResultBody();//.replace("\\\"","\"").replace("\"{","{").replace("}\"","}").replaceAll("\"PROFILE\": \"(.*?)\"","\"PROFILE\": {\"PUBLIC_NAME\": \"$1\"}");
                JsonElement data = (new JsonParser()).parse(preparedResponseBody);
                ArrayList<Format> resultFormats = new ArrayList<Format>();
                for (Class<? extends Format> returnFormatClass : command.getCommandDefinition().getReturningFormats()) {
                    try {
                        Format returnFormat = returnFormatClass.newInstance();
                        returnFormat.fromJSON(data);
                        resultFormats.add(returnFormat);
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        continue;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                if (!resultFormats.isEmpty())
                    command.setResultFormats(resultFormats);
            }

            Callback.Run(command,callbackParameters);

            if (response.getResultHeaders().containsKey("Set-Cookie")) //Update Cookies //TODO: This mays change when using token based authentication.
                for (String setCookieHeader : response.getResultHeaders().get("Set-Cookie"))
                    for (HttpCookie cookie : HttpCookie.parse(setCookieHeader))
                        if (cookie.getName().equalsIgnoreCase(CommandDefinition.CSRFTokenCookie)) {
                            this.CSRFToken = cookie.getValue();
                            //TODO: Notify value changed
                        } else if (cookie.getName().equalsIgnoreCase(CommandDefinition.SessionCookie)) {
                            command.getServerUser().updateAuthToken(CommandDefinition.SessionCookie,cookie.getValue());
                            //TODO: Notify value changed?
                        }


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
}
