package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Last message in chat
 *
 */
@Generated("org.jsonschema2pojo")
public class LAST_MESSAGE {

    /**
     * Message sequential identifier
     * (Required)
     *
     */
    @SerializedName("id")
    @Expose
    private int id;
    /**
     * Reception flag
     * (Required)
     *
     */
    @SerializedName("received")
    @Expose
    private boolean received;
    /**
     * Message content
     * (Required)
     *
     */
    @SerializedName("content")
    @Expose
    private String content;
    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     * (Required)
     *
     */
    @SerializedName("content_type")
    @Expose
    private String contentType;
    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     */
    @SerializedName("created")
    @Expose
    private String created;
    /**
     * Sender public name
     * (Required)
     *
     */
    @SerializedName("profile")
    @Expose
    private String profile;

    /**
     * Message sequential identifier
     * (Required)
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     * Message sequential identifier
     * (Required)
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Reception flag
     * (Required)
     *
     * @return
     * The received
     */
    public boolean isReceived() {
        return received;
    }

    /**
     * Reception flag
     * (Required)
     *
     * @param received
     * The received
     */
    public void setReceived(boolean received) {
        this.received = received;
    }

    /**
     * Message content
     * (Required)
     *
     * @return
     * The content
     */
    public String getContent() {
        return content;
    }

    /**
     * Message content
     * (Required)
     *
     * @param content
     * The content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     * (Required)
     *
     * @return
     * The contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Content-Type of the message. (MIME Type type, except for application which may contain subtype)
     * (Required)
     *
     * @param contentType
     * The content_type
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @return
     * The created
     */
    public String getCreated() {
        return created;
    }

    /**
     * Message date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @param created
     * The created
     */
    public void setCreated(String created) {
        this.created = created;
    }

    /**
     * Sender public name
     * (Required)
     *
     * @return
     * The profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sender public name
     * (Required)
     *
     * @param profile
     * The profile
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(received).append(content).append(contentType).append(created).append(profile).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LAST_MESSAGE) == false) {
            return false;
        }
        LAST_MESSAGE rhs = ((LAST_MESSAGE) other);
        return new EqualsBuilder().append(id, rhs.id).append(received, rhs.received).append(content, rhs.content).append(contentType, rhs.contentType).append(created, rhs.created).append(profile, rhs.profile).isEquals();
    }

}