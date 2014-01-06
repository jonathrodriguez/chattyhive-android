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
 * This class provides a unique way to perform http connections. Since this is intended to perform
 * asynchronous http connections, it provides a method to permit synchronous operation.
 */
public class AsyncHttpURLConnection extends Thread {
    private String _method;
    private String _URL;
    private ServerUser _user;
    private String _bodyData;
    private String _RESTData;

    private ServerResponse _serverResponse;

    /**
     * Performs a synchronous http connection, using a separate thread (Android does not permit network
     * operations on main thread).
     * @return a Server Response object.
     * @throws InterruptedException
     */
    public ServerResponse getServerResponse() throws InterruptedException {
        if (this._serverResponse == null) {
            this.start();
        }
        this.join();
        return this._serverResponse;
    }

    /**
     * Public constructor.
     * @param method The http method. (Usually GET or POST).
     * @param URL The URL where to connect.
     * @param user A Server User from which to take a save cookies.
     * @param bodyData The data to be sent in the message body of the http protocol. (Requires method POST).
     * @param RESTData The data to be sent as REST data, encoded in the URL. (Useful to send data with GET).
     */
    public AsyncHttpURLConnection (String method, String URL, ServerUser user, String bodyData, String RESTData) {
        this._method = method;
        this._URL = URL;
        this._user = user;
        this._bodyData = bodyData;
        this._RESTData = RESTData;
    }

    @Override
    /**
     * The connection itself is done in the run method overridden from the Thread class; thus, the
     * connection is performed in a separate thread.
     */
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
