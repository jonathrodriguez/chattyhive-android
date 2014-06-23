package com.chattyhive.backend.contentprovider.pubsubservice;

import com.chattyhive.backend.businessobjects.Users.User;
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

        String authToken = "";
        List<HttpCookie> cookieList = HttpCookie.parse(this.serverUser.getCookies());

        for (HttpCookie cookie : cookieList)
            if ((!cookie.hasExpired()) && (!cookie.getName().equalsIgnoreCase("csrftoken")))
                authToken = authToken.concat(cookie.getValue());

        if (authToken.isEmpty())
            throw new AuthorizationFailureException("No user connected.");

        JsonObject authorization = new JsonObject();
        authorization.addProperty("auth",authToken);

        if (channelName.startsWith("presence-")) {
            User me = User.getMe();
            if (me == null)
                throw new AuthorizationFailureException("No user profile available.");

            JsonObject channelData = new JsonObject();
            channelData.addProperty("user-id",me.getPublicName());

            /*HashMap<String,String> userInfoMap = new HashMap<String, String>();
            //Add user info
            JsonObject userInfo = new JsonObject();
            for (Map.Entry<String,String> entry : userInfoMap.entrySet())
                userInfo.addProperty(entry.getKey(),entry.getValue());
            channelData.add("user-info",userInfo);*/

            authorization.add("channelData",channelData);
        }

        return authorization.toString();
    }
}
