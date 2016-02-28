
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Hive public chat
 *
 */
@Generated("org.jsonschema2pojo")
public class PUBLIC_CHAT {

    /**
     * Chat identifier. Example: "0a590deb318044bb9ac9e45641bbc1ff"
     *
     */
    @SerializedName("chat")
    @Expose
    private String chat;

    /**
     * Chat identifier. Example: "0a590deb318044bb9ac9e45641bbc1ff"
     *
     * @return
     * The chat
     */
    public String getChat() {
        return chat;
    }

    /**
     * Chat identifier. Example: "0a590deb318044bb9ac9e45641bbc1ff"
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
        if ((other instanceof PUBLIC_CHAT) == false) {
            return false;
        }
        PUBLIC_CHAT rhs = ((PUBLIC_CHAT) other);
        return new EqualsBuilder().append(chat, rhs.chat).isEquals();
    }

}
