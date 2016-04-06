package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ResponsePublicNameGet
 * <p>
 * Response body for the Public_name Get method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_PUBLIC_NAME_GET {

    /**
     * Public_name corresponding to the request email. Only for active session.
     *
     */
    @SerializedName("public_name")
    @Expose
    private String public_name;

    /**
     * Public_name corresponding to the request email. Only for active session.
     *
     * @return
     * The public_name
     */
    public String getPublic_name() {
        return public_name;
    }

    /**
     * Public_name corresponding to the request email. Only for active session.
     *
     * @param public_name
     * The public_name
     */
    public void setPublic_name(String public_name) {
        this.public_name = public_name;
    }

}

