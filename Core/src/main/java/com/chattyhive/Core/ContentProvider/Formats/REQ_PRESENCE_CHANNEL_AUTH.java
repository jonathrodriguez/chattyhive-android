

        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestPresenceChannelAuth
 * <p>
 * Request body for the Presence-channel Auth method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_PRESENCE_CHANNEL_AUTH {

    /**
     * IMPORTANT: do not send this from client. In future versions this will be used to choose between pusher, poxa, slanger or whatever event delivery we use at the moment. Right now this is automatically filled by the backend.
     *
     */
    @SerializedName("service")
    @Expose
    private String service;
    /**
     * Identifier of the presence channel.
     * (Required)
     *
     */
    @SerializedName("channel_name")
    @Expose
    private String channelName;
    /**
     * Identifier of the socket asigned to the user.
     * (Required)
     *
     */
    @SerializedName("socket_id")
    @Expose
    private String socketId;

    /**
     * IMPORTANT: do not send this from client. In future versions this will be used to choose between pusher, poxa, slanger or whatever event delivery we use at the moment. Right now this is automatically filled by the backend.
     *
     * @return
     * The service
     */
    public String getService() {
        return service;
    }

    /**
     * IMPORTANT: do not send this from client. In future versions this will be used to choose between pusher, poxa, slanger or whatever event delivery we use at the moment. Right now this is automatically filled by the backend.
     *
     * @param service
     * The service
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * Identifier of the presence channel.
     * (Required)
     *
     * @return
     * The channelName
     */
    public String getChannelName() {
        return channelName;
    }

    /**
     * Identifier of the presence channel.
     * (Required)
     *
     * @param channelName
     * The channel_name
     */
    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /**
     * Identifier of the socket asigned to the user.
     * (Required)
     *
     * @return
     * The socketId
     */
    public String getSocketId() {
        return socketId;
    }

    /**
     * Identifier of the socket asigned to the user.
     * (Required)
     *
     * @param socketId
     * The socket_id
     */
    public void setSocketId(String socketId) {
        this.socketId = socketId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(service).append(channelName).append(socketId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_PRESENCE_CHANNEL_AUTH) == false) {
            return false;
        }
        REQ_PRESENCE_CHANNEL_AUTH rhs = ((REQ_PRESENCE_CHANNEL_AUTH) other);
        return new EqualsBuilder().append(service, rhs.service).append(channelName, rhs.channelName).append(socketId, rhs.socketId).isEquals();
    }

}
