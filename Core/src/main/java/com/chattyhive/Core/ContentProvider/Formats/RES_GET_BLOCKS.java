package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * List of blocked users
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_GET_BLOCKS {

    /**
     * Public_name of the user blocked.
     * (Required)
     *
     */
    @SerializedName("blocked_profile_ID")
    @Expose
    private String blockedProfileID;

    /**
     * Public_name of the user blocked.
     * (Required)
     *
     * @return
     * The blockedProfileID
     */
    public String getBlockedProfileID() {
        return blockedProfileID;
    }

    /**
     * Public_name of the user blocked.
     * (Required)
     *
     * @param blockedProfileID
     * The blocked_profile_ID
     */
    public void setBlockedProfileID(String blockedProfileID) {
        this.blockedProfileID = blockedProfileID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(blockedProfileID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_GET_BLOCKS) == false) {
            return false;
        }
        RES_GET_BLOCKS rhs = ((RES_GET_BLOCKS) other);
        return new EqualsBuilder().append(blockedProfileID, rhs.blockedProfileID).isEquals();
    }

}