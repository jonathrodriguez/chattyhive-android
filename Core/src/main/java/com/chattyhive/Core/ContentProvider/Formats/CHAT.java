package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Chat object
 *
 */
@Generated("org.jsonschema2pojo")
public class CHAT {

    /**
     * Chat identifier
     * (Required)
     *
     */
    @SerializedName("chat_id")
    @Expose
    private String chatId;
    /**
     * Chat text identifier.
     * (Required)
     *
     */
    @SerializedName("slug")
    @Expose
    private String slug;
    /**
     * Chat type. (public, mate_private, mate_group, friend_private, friend_group)
     * (Required)
     *
     */
    @SerializedName("type")
    @Expose
    private CHAT.Type type;
    /**
     * Last message in chat
     * (Required)
     *
     */
    @SerializedName("last_message")
    @Expose
    private LAST_MESSAGE lastMessage;

    /**
     * Chat identifier
     * (Required)
     *
     * @return
     * The chatId
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * Chat identifier
     * (Required)
     *
     * @param chatId
     * The chat_id
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     * Chat text identifier.
     * (Required)
     *
     * @return
     * The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Chat text identifier.
     * (Required)
     *
     * @param slug
     * The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Chat type. (public, mate_private, mate_group, friend_private, friend_group)
     * (Required)
     *
     * @return
     * The type
     */
    public CHAT.Type getType() {
        return type;
    }

    /**
     * Chat type. (public, mate_private, mate_group, friend_private, friend_group)
     * (Required)
     *
     * @param type
     * The type
     */
    public void setType(CHAT.Type type) {
        this.type = type;
    }

    /**
     * Last message in chat
     * (Required)
     *
     * @return
     * The lastMessage
     */
    public LAST_MESSAGE getLastMessage() {
        return lastMessage;
    }

    /**
     * Last message in chat
     * (Required)
     *
     * @param lastMessage
     * The last_message
     */
    public void setLastMessage(LAST_MESSAGE lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(chatId).append(slug).append(type).append(lastMessage).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof CHAT) == false) {
            return false;
        }
        CHAT rhs = ((CHAT) other);
        return new EqualsBuilder().append(chatId, rhs.chatId).append(slug, rhs.slug).append(type, rhs.type).append(lastMessage, rhs.lastMessage).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        @SerializedName("public")
        PUBLIC("public"),
        @SerializedName("mate_private")
        MATE_PRIVATE("mate_private"),
        @SerializedName("mate_group")
        MATE_GROUP("mate_group"),
        @SerializedName("friend_private")
        FRIEND_PRIVATE("friend_private"),
        @SerializedName("friend_group")
        FRIEND_GROUP("friend_group");
        private final String value;
        private final static Map<String, CHAT.Type> CONSTANTS = new HashMap<String, CHAT.Type>();

        static {
            for (CHAT.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static CHAT.Type fromValue(String value) {
            CHAT.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}