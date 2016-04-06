
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsHiveInfo
 * <p>
 * URL params for the Hive info method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_HIVE_INFO {

    /**
     * PATH 1. Hive identifier.
     *
     */
    @SerializedName("slug")
    @Expose
    private String slug;

    /**
     * PATH 1. Hive identifier.
     *
     * @return
     * The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * PATH 1. Hive identifier.
     *
     * @param slug
     * The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

}
