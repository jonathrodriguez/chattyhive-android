package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsPublicNameGet
 * <p>
 * URL params for the Public name get method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_PUBLIC_NAME_GET {

    /**
     * QUERY. email of the user to retrieve the public name.
     * (Required)
     *
     */
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * QUERY. email of the user to retrieve the public name.
     * (Required)
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * QUERY. email of the user to retrieve the public name.
     * (Required)
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

}

