
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsJoin
 * <p>
 * URL params for the Join method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_JOIN {

    /**
     * PATH 1. public_name of the logged user.
     * (Required)
     *
     */
    @SerializedName("public_name")
    @Expose
    private String public_name;

    /**
     * PATH 1. public_name of the logged user.
     * (Required)
     *
     * @return
     * The public_name
     */
    public String getPublic_name() {
        return public_name;
    }

    /**
     * PATH 1. public_name of the logged user.
     * (Required)
     *
     * @param public_name
     * The public_name
     */
    public void setPublic_name(String public_name) {
        this.public_name = public_name;
    }

}
