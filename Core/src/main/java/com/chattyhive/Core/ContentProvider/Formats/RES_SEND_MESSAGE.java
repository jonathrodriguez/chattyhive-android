package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ResponseSendMessage
 * <p>
 * Response body for the Send Message method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_SEND_MESSAGE {

    /**
     * Message sequential identifier
     *
     */
    @SerializedName("message_id")
    @Expose
    private Integer message_id;
    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("server_timestamp")
    @Expose
    private String server_timestamp;
    /**
     * chat identifier if new_chat was specified in the request
     *
     */
    @SerializedName("chat_id")
    @Expose
    private String chat_id;

    /**
     * Message sequential identifier
     *
     * @return
     * The message_id
     */
    public Integer getMessage_id() {
        return message_id;
    }

    /**
     * Message sequential identifier
     *
     * @param message_id
     * The message_id
     */
    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public RES_SEND_MESSAGE withMessage_id(Integer message_id) {
        this.message_id = message_id;
        return this;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The server_timestamp
     */
    public String getServer_timestamp() {
        return server_timestamp;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param server_timestamp
     * The server_timestamp
     */
    public void setServer_timestamp(String server_timestamp) {
        this.server_timestamp = server_timestamp;
    }

    public RES_SEND_MESSAGE withServer_timestamp(String server_timestamp) {
        this.server_timestamp = server_timestamp;
        return this;
    }

    /**
     * chat identifier if new_chat was specified in the request
     *
     * @return
     * The chat_id
     */
    public String getChat_id() {
        return chat_id;
    }

    /**
     * chat identifier if new_chat was specified in the request
     *
     * @param chat_id
     * The chat_id
     */
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public RES_SEND_MESSAGE withChat_id(String chat_id) {
        this.chat_id = chat_id;
        return this;
    }

}
