        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;


/**
 * Chat message
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_CHAT_MESSAGE {

    /**
     * Message sequential identifier
     *
     */
    @SerializedName("id")
    @Expose
    private Integer id;
    /**
     * Reception flag
     *
     */
    @SerializedName("received")
    @Expose
    private Boolean received;
    /**
     * Message content
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
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("created")
    @Expose
    private String created;
    /**
     * Sender public name
     *
     */
    @SerializedName("profile")
    @Expose
    private String profile;

    /**
     * Message sequential identifier
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Message sequential identifier
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    public RES_CHAT_MESSAGE withId(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * Reception flag
     *
     * @return
     * The received
     */
    public Boolean getReceived() {
        return received;
    }

    /**
     * Reception flag
     *
     * @param received
     * The received
     */
    public void setReceived(Boolean received) {
        this.received = received;
    }

    public RES_CHAT_MESSAGE withReceived(Boolean received) {
        this.received = received;
        return this;
    }

    /**
     * Message content
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     * Message content
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    public RES_CHAT_MESSAGE withContent(String content) {
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

    public RES_CHAT_MESSAGE withContent_type(String content_type) {
        this.content_type = content_type;
        return this;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    public RES_CHAT_MESSAGE withCreated(String created) {
        this.created = created;
        return this;
    }

    /**
     * Sender public name
     *
     * @return
     * The profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sender public name
     *
     * @param profile
     * The profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    public RES_CHAT_MESSAGE withProfile(String profile) {
        this.profile = profile;
        return this;
    }

}
