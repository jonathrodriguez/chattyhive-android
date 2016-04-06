
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * RequestJoin
 * <p>
 * Request body for the Join method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_JOIN {

    /**
     * Hive slug identifier of the hive to join.
     * (Required)
     *
     */
    @SerializedName("hive_slug")
    @Expose
    private String hive_slug;

    /**
     * Hive slug identifier of the hive to join.
     * (Required)
     *
     * @return
     * The hive_slug
     */
    public String getHive_slug() {
        return hive_slug;
    }

    /**
     * Hive slug identifier of the hive to join.
     * (Required)
     *
     * @param hive_slug
     * The hive_slug
     */
    public void setHive_slug(String hive_slug) {
        this.hive_slug = hive_slug;
    }

}
