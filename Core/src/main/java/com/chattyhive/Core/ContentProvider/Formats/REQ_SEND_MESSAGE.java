package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * RequestSendMessage
 * <p>
 * Request body for the Send Message method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_SEND_MESSAGE {

    /**
     * Pusher socket_id of the client. Required
     *
     */
    @SerializedName("socket_id")
    @Expose
    private Object socket_id;
    /**
     * Required if the client dev_os is android
     *
     */
    @SerializedName("dev_id")
    @Expose
    private Object dev_id;
    /**
     * Message content. URL if not textual data.
     *
     */
    @SerializedName("content")
    @Expose
    private String content;
    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     *
     */
    @SerializedName("content_type")
    @Expose
    private String content_type;
    /**
     * True if the client believes that this is the first message for the chat
     *
     */
    @SerializedName("new_chat")
    @Expose
    private Boolean new_chat;

    /**
     * Pusher socket_id of the client. Required
     *
     * @return
     * The socket_id
     */
    public Object getSocket_id() {
        return socket_id;
    }

    /**
     * Pusher socket_id of the client. Required
     *
     * @param socket_id
     * The socket_id
     */
    public void setSocket_id(Object socket_id) {
        this.socket_id = socket_id;
    }

    public REQ_SEND_MESSAGE withSocket_id(Object socket_id) {
        this.socket_id = socket_id;
        return this;
    }

    /**
     * Required if the client dev_os is android
     *
     * @return
     * The dev_id
     */
    public Object getDev_id() {
        return dev_id;
    }

    /**
     * Required if the client dev_os is android
     *
     * @param dev_id
     * The dev_id
     */
    public void setDev_id(Object dev_id) {
        this.dev_id = dev_id;
    }

    public REQ_SEND_MESSAGE withDev_id(Object dev_id) {
        this.dev_id = dev_id;
        return this;
    }

    /**
     * Message content. URL if not textual data.
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     * Message content. URL if not textual data.
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    public REQ_SEND_MESSAGE withContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     *
     * @return
     * The content_type
     */
    public String getContent_type() {
        return content_type;
    }

    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     *
     * @param content_type
     * The content_type
     */
    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public REQ_SEND_MESSAGE withContent_type(String content_type) {
        this.content_type = content_type;
        return this;
    }

    /**
     * True if the client believes that this is the first message for the chat
     *
     * @return
     * The new_chat
     */
    public Boolean getNew_chat() {
        return new_chat;
    }

    /**
     * True if the client believes that this is the first message for the chat
     *
     * @param new_chat
     * The new_chat
     */
    public void setNew_chat(Boolean new_chat) {
        this.new_chat = new_chat;
    }

    public REQ_SEND_MESSAGE withNew_chat(Boolean new_chat) {
        this.new_chat = new_chat;
        return this;
    }

}
