package com.chattyhive.backend.contentprovider.server;

/**
 * Created by Jonathan on 27/12/13.
 */
public class ServerResponse {
    private int _responseCode;
    private String _bodyData;

    public int getResponseCode() { return this._responseCode; }
    public String getBodyData() { return this._bodyData; }

    public ServerResponse(int responseCode, String bodyData) {
        this._responseCode = responseCode;
        this._bodyData = bodyData;
    }
}
