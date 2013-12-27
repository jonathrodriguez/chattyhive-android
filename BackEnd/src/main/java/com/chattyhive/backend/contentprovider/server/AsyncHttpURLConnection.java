package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.StaticParameters;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Jonathan on 23/12/13.
 */
public class AsyncHttpURLConnection extends Thread {
    private String _method;
    private String _URL;
    private ServerUser _user;
    private String _bodyData;
    private String _RESTData;

    private ServerResponse _serverResponse;

    public ServerResponse getServerResponse() throws InterruptedException {
        if (this._serverResponse == null) {
            this.start();
        }
        this.join();
        return this._serverResponse;
    }

    public AsyncHttpURLConnection (String method, String URL, ServerUser user, String bodyData, String RESTData) {
        this._method = method;
        this._URL = URL;
        this._user = user;
        this._bodyData = bodyData;
        this._RESTData = RESTData;
    }

    @Override
    public void run() {
        int responseCode = 0;
        String responseBody = "";
        try {
            URL url = new URL(this._URL.concat("/").concat(this._RESTData));
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod(this._method);
            httpURLConnection.setRequestProperty("User-Agent", StaticParameters.UserAgent());

            String Cookies = this._user.getCookies();
            httpURLConnection.setRequestProperty("Cookie",Cookies);

            HttpCookie csrfCookie = this._user.getCookie("csrftoken");
            if (csrfCookie != null) {
                httpURLConnection.setRequestProperty("X-CSRFToken",csrfCookie.getValue());
            }

            if ((this._method.equalsIgnoreCase("POST")) && (this._bodyData != null) && (!this._bodyData.isEmpty())) {
                httpURLConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                wr.writeBytes(this._bodyData);
                wr.flush();
                wr.close();
            }

            responseCode = httpURLConnection.getResponseCode();

            if (responseCode == 200) {

                BufferedReader inputReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                List<String> setCookies = httpURLConnection.getHeaderFields().get("Set-Cookie");
                if (setCookies != null) {
                    for (String setCookie : setCookies) {
                        List<HttpCookie> cookies = HttpCookie.parse(setCookie);
                        for (HttpCookie cookie : cookies) {
                            this._user.setCookie(cookie);
                        }
                    }
                }


                while ((inputLine = inputReader.readLine()) != null) {
                    response.append(inputLine);
                }
                inputReader.close();

                responseBody = response.toString();
            }

        } catch (MalformedURLException e) {
            responseCode = 0;
        } catch (ProtocolException e) {
            responseCode = 10;
        } catch (IOException e) {
            responseCode = 20;
        }

        this._serverResponse = new ServerResponse(responseCode,responseBody);
    }
}
