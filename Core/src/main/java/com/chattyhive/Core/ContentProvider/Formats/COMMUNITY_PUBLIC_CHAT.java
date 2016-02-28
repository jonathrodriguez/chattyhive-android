
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 *
 *
 */
@Generated("org.jsonschema2pojo")
public class COMMUNITY_PUBLIC_CHAT {

    /**
     * Chat identifier. Example: "66236e2c19cb4caf9f871ff2c5fd42e3"
     *
     */
    @SerializedName("chat")
    @Expose
    private String chat;

    /**
     * Chat identifier. Example: "66236e2c19cb4caf9f871ff2c5fd42e3"
     *
     * @return
     * The chat
     */
    public String getChat() {
        return chat;
    }

    /**
     * Chat identifier. Example: "66236e2c19cb4caf9f871ff2c5fd42e3"
     *
     * @param chat
     * The chat
     */
    public void setChat(String chat) {
        this.chat = chat;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(chat).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof COMMUNITY_PUBLIC_CHAT) == false) {
            return false;
        }
        COMMUNITY_PUBLIC_CHAT rhs = ((COMMUNITY_PUBLIC_CHAT) other);
        return new EqualsBuilder().append(chat, rhs.chat).isEquals();
    }

}
