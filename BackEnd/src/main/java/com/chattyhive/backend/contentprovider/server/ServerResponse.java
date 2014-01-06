package com.chattyhive.backend.contentprovider.server;

/**
 * Created by Jonathan on 27/12/13.
 * This class contains a response for a http connection. It contains the response code and the body data.
 * This class is intended to be returned by the AsyncHttÙRLConnection.
 */
public class ServerResponse {
    private int _responseCode;
    private String _bodyData;

    /**
     * Returns the response code as an integer.
     * @return an integer containing the response code.
     */
    public int getResponseCode() { return this._responseCode; }

    /**
     * Returns the response body data as a string
     * @return a string containing the body data.
     */
    public String getBodyData() { return this._bodyData; }

    /**
     * Public constructor.
     * @param responseCode an integer containing the response code.
     * @param bodyData a string containing the body data.
     */
    public ServerResponse(int responseCode, String bodyData) {
        this._responseCode = responseCode;
        this._bodyData = bodyData;
    }
}
