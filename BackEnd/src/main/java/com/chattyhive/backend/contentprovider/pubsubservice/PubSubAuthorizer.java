package com.chattyhive.backend.contentprovider.pubsubservice;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.businessobjects.Users.User;
import com.chattyhive.backend.contentprovider.server.AsyncHttpURLConnection;
import com.chattyhive.backend.contentprovider.server.ServerResponse;
import com.chattyhive.backend.contentprovider.server.ServerStatus;
import com.chattyhive.backend.contentprovider.server.ServerUser;
import com.google.gson.JsonObject;
import com.pusher.client.AuthorizationFailureException;
import com.pusher.client.Authorizer;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan Rodriguez on 10/06/2014.
 */
public class PubSubAuthorizer implements Authorizer {

    private ServerUser serverUser;

    public PubSubAuthorizer (ServerUser serverUser) {
        this.serverUser = serverUser;
    }

    public ServerUser getServerUser() { return this.serverUser; }
    public void setServerUser(ServerUser serverUser) { this.serverUser = serverUser; }

    /**
     * Called when a channel is to be authenticated.
     *
     * @param channelName The name of the channel to be authenticated.
     * @param socketId    A unique socket connection ID to be used with the authentication.
     *                    This uniquely identifies the connection that the subscription is being authenticated for.
     * @return An authentication token.
     * @throws com.pusher.client.AuthorizationFailureException if the authentication fails.
     */
    @Override
    public String authorize(String channelName, String socketId) throws AuthorizationFailureException {
        if ((this.serverUser == null) || (this.serverUser.getStatus() == ServerStatus.DISCONNECTED) || (this.serverUser.getCookies() == null) || (this.serverUser.getCookies().isEmpty())) {
            throw new AuthorizationFailureException("No user connected.");
        }

        String method = "POST";
        String URL = "http://chtest2.herokuapp.com/chat_auth";
        ServerUser user = Controller.getRunningController().getServerUser();
        String bodyData = "channel_name=" + channelName + "\nsocket_id=" + socketId;
        String RESTData = "";

        AsyncHttpURLConnection asyncHttpURLConnection = new AsyncHttpURLConnection(method, URL, user, bodyData, RESTData);

        try {
            ServerResponse res = asyncHttpURLConnection.getServerResponse();
            if (res.getResponseCode() == 200) {
                System.out.println(res.getBodyData());
                return res.getBodyData();
            } else {
                System.out.println(res.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "";
    }
}
