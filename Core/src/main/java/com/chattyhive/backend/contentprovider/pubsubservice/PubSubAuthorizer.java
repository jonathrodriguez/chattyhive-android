package com.chattyhive.backend.contentprovider.pubsubservice;

/**
 * Created by Jonathan Rodriguez on 10/06/2014.
 */
/*public class PubSubAuthorizer implements Authorizer {

    private ServerUser serverUser;

    public PubSubAuthorizer (ServerUser serverUser) {
        this.serverUser = serverUser;
    }

    public ServerUser getServerUser() { return this.serverUser; }
    public void setServerUser(ServerUser serverUser) { this.serverUser = serverUser; }

    *//**
     * Called when a channel is to be authenticated.
     *
     * @param channelName The name of the channel to be authenticated.
     * @param socketId    A unique socket connection ID to be used with the authentication.
     *                    This uniquely identifies the connection that the subscription is being authenticated for.
     * @return An authentication token.
     * @throws com.pusher.client.AuthorizationFailureException if the authentication fails.
     *//*
    @Override
    public String authorize(String channelName, String socketId) throws AuthorizationFailureException {
        if ((!DataProvider.isConnectionAvailable()) || (!DataProvider.GetDataProvider().isServerConnected())) {
            throw new AuthorizationFailureException("No user connected.");
        }

        DataProvider dataProvider = DataProvider.GetDataProvider();



        String method = "POST";
        String URL = "http://chtest2.herokuapp.com/chat_auth";
        ServerUser user = Controller.GetRunningController().getServerUser();
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
}*/
