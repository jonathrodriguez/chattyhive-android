package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsChatInfo
 * <p>
 * URL params for the Chat Info method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_CHAT_INFO {

    /**
     * PATH 1. Chat identifier.
     *
     */
    @SerializedName("chat_id")
    @Expose
    private String chat_id;

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

    public URL_CHAT_INFO withChat_id(String chat_id) {
        this.chat_id = chat_id;
        return this;
    }

}
