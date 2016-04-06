package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ResponseChatInfo
 * <p>
 * Response body for the Chat info method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_CHAT_INFO {

    /**
     * Chat identifier. Example: 0a590deb318044bb9ac9e45641bbc1ff
     *
     */
    @SerializedName("chat_id")
    @Expose
    private String chat_id;
    /**
     * Number of chat messages?
     *
     */
    @SerializedName("count")
    @Expose
    private Integer count;
    /**
     * Chat creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("creatated")
    @Expose
    private String creatated;
    /**
     * Chat identifier. Example: 14c727a19adc4c5aa5e75d1e1363e624-asi-es-imposible-estar-a-dieta
     *
     */
    @SerializedName("slug")
    @Expose
    private String slug;
    /**
     * Chat type
     *
     */
    @SerializedName("type")
    @Expose
    private RES_CHAT_INFO.Type type;
    @SerializedName("last_message")
    @Expose
    private LAST_MESSAGE last_message;

    /**
     * Chat identifier. Example: 0a590deb318044bb9ac9e45641bbc1ff
     *
     * @return
     * The chat_id
     */
    public String getChat_id() {
        return chat_id;
    }

    /**
     * Chat identifier. Example: 0a590deb318044bb9ac9e45641bbc1ff
     *
     * @param chat_id
     * The chat_id
     */
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public RES_CHAT_INFO withChat_id(String chat_id) {
        this.chat_id = chat_id;
        return this;
    }

    /**
     * Number of chat messages?
     *
     * @return
     * The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * Number of chat messages?
     *
     * @param count
     * The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    public RES_CHAT_INFO withCount(Integer count) {
        this.count = count;
        return this;
    }

    /**
     * Chat creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The creatated
     */
    public String getCreatated() {
        return creatated;
    }

    /**
     * Chat creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param creatated
     * The creatated
     */
    public void setCreatated(String creatated) {
        this.creatated = creatated;
    }

    public RES_CHAT_INFO withCreatated(String creatated) {
        this.creatated = creatated;
        return this;
    }

    /**
     * Chat identifier. Example: 14c727a19adc4c5aa5e75d1e1363e624-asi-es-imposible-estar-a-dieta
     *
     * @return
     * The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Chat identifier. Example: 14c727a19adc4c5aa5e75d1e1363e624-asi-es-imposible-estar-a-dieta
     *
     * @param slug
     * The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    public RES_CHAT_INFO withSlug(String slug) {
        this.slug = slug;
        return this;
    }

    /**
     * Chat type
     *
     * @return
     * The type
     */
    public RES_CHAT_INFO.Type getType() {
        return type;
    }

    /**
     * Chat type
     *
     * @param type
     * The type
     */
    public void setType(RES_CHAT_INFO.Type type) {
        this.type = type;
    }

    public RES_CHAT_INFO withType(RES_CHAT_INFO.Type type) {
        this.type = type;
        return this;
    }

    /**
     *
     * @return
     * The last_message
     */
    public LAST_MESSAGE getLast_message() {
        return last_message;
    }

    /**
     *
     * @param last_message
     * The last_message
     */
    public void setLast_message(LAST_MESSAGE last_message) {
        this.last_message = last_message;
    }

    public RES_CHAT_INFO withLast_message(LAST_MESSAGE last_message) {
        this.last_message = last_message;
        return this;
    }

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        @SerializedName("public")
        PUBLIC("public"),
        @SerializedName("mate_private")
        MATE_PRIVATE("mate_private"),
        @SerializedName("mates_group")
        MATES_GROUP("mates_group"),
        @SerializedName("friend_private")
        FRIEND_PRIVATE("friend_private"),
        @SerializedName("friends_group")
        FRIENDS_GROUP("friends_group");
        private final String value;
        private final static Map<String, RES_CHAT_INFO.Type> CONSTANTS = new HashMap<String, RES_CHAT_INFO.Type>();

        static {
            for (RES_CHAT_INFO.Type c: values()) {
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

        public static RES_CHAT_INFO.Type fromValue(String value) {
            RES_CHAT_INFO.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
