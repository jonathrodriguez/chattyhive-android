package com.chattyhive.backend.ContentProvider.Server;

import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 11/02/2015.
 */
public class ServerResponse {
    private int resultCode;
    private String resultBody;
    private Map<String,List<String>> resultHeaders;

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultBody(String resultBody) {
        this.resultBody = resultBody;
    }

    public void setResultHeaders(Map<String, List<String>> resultHeaders) {
        this.resultHeaders = resultHeaders;
    }

    public int getResultCode() {
        return this.resultCode;
    }

    public String getResultBody() {
        return resultBody;
    }

    public Map<String, List<String>> getResultHeaders() {
        return resultHeaders;
    }

    public ServerResponse() {}
}
