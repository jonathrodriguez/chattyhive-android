package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsChatMessages
 * <p>
 * URL params for the Chat Messages method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_CHAT_MESSAGES {

    /**
     * PATH 1. Chat identifier.
     *
     */
    @SerializedName("chat_id")
    @Expose
    private String chat_id;
    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     */
    @SerializedName("sort")
    @Expose
    private URL_CHAT_MESSAGES.Sort sort;
    /**
     * QUERY. start point of the requested list.
     *
     */
    @SerializedName("start")
    @Expose
    private String start;
    /**
     * QUERY. end point of the requested list.
     *
     */
    @SerializedName("end")
    @Expose
    private String end;
    /**
     * QUERY. number of elements requested.
     *
     */
    @SerializedName("elements")
    @Expose
    private Integer elements;

    /**
     * PATH 1. Chat identifier.
     *
     * @return
     * The chat_id
     */
    public String getChat_id() {
        return chat_id;
    }

    /**
     * PATH 1. Chat identifier.
     *
     * @param chat_id
     * The chat_id
     */
    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public URL_CHAT_MESSAGES withChat_id(String chat_id) {
        this.chat_id = chat_id;
        return this;
    }

    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     * @return
     * The sort
     */
    public URL_CHAT_MESSAGES.Sort getSort() {
        return sort;
    }

    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     * @param sort
     * The sort
     */
    public void setSort(URL_CHAT_MESSAGES.Sort sort) {
        this.sort = sort;
    }

    public URL_CHAT_MESSAGES withSort(URL_CHAT_MESSAGES.Sort sort) {
        this.sort = sort;
        return this;
    }

    /**
     * QUERY. start point of the requested list.
     *
     * @return
     * The start
     */
    public String getStart() {
        return start;
    }

    /**
     * QUERY. start point of the requested list.
     *
     * @param start
     * The start
     */
    public void setStart(String start) {
        this.start = start;
    }

    public URL_CHAT_MESSAGES withStart(String start) {
        this.start = start;
        return this;
    }

    /**
     * QUERY. end point of the requested list.
     *
     * @return
     * The end
     */
    public String getEnd() {
        return end;
    }

    /**
     * QUERY. end point of the requested list.
     *
     * @param end
     * The end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    public URL_CHAT_MESSAGES withEnd(String end) {
        this.end = end;
        return this;
    }

    /**
     * QUERY. number of elements requested.
     *
     * @return
     * The elements
     */
    public Integer getElements() {
        return elements;
    }

    /**
     * QUERY. number of elements requested.
     *
     * @param elements
     * The elements
     */
    public void setElements(Integer elements) {
        this.elements = elements;
    }

    public URL_CHAT_MESSAGES withElements(Integer elements) {
        this.elements = elements;
        return this;
    }

    @Generated("org.jsonschema2pojo")
    public static enum Sort {

        @SerializedName("recommended")
        RECOMMENDED("recommended"),
        @SerializedName("near")
        NEAR("near"),
        @SerializedName("recent")
        RECENT("recent"),
        @SerializedName("new")
        NEW("new");
        private final String value;
        private final static Map<String, URL_CHAT_MESSAGES.Sort> CONSTANTS = new HashMap<String, URL_CHAT_MESSAGES.Sort>();

        static {
            for (URL_CHAT_MESSAGES.Sort c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Sort(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static URL_CHAT_MESSAGES.Sort fromValue(String value) {
            URL_CHAT_MESSAGES.Sort constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
