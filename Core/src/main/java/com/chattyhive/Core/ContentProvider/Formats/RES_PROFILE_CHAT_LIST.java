        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Chat subscription
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_PROFILE_CHAT_LIST {

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     */
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    /**
     * Chat object
     * (Required)
     *
     */
    @SerializedName("chat")
    @Expose
    private CHAT chat;

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @return
     * The creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @param creationDate
     * The creation_date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Chat object
     * (Required)
     *
     * @return
     * The chat
     */
    public CHAT getChat() {
        return chat;
    }

    /**
     * Chat object
     * (Required)
     *
     * @param chat
     * The chat
     */
    public void setChat(CHAT chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(creationDate).append(chat).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_PROFILE_CHAT_LIST) == false) {
            return false;
        }
        RES_PROFILE_CHAT_LIST rhs = ((RES_PROFILE_CHAT_LIST) other);
        return new EqualsBuilder().append(creationDate, rhs.creationDate).append(chat, rhs.chat).isEquals();
    }

}
