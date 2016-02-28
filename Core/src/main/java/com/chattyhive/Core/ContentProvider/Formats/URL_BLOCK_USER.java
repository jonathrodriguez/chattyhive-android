package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsBlockUser
 * <p>
 * URL params for the Block User method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_BLOCK_USER {

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     */
    @SerializedName("blocker_profile_ID")
    @Expose
    private String blockerProfileID;

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     * @return
     * The blockerProfileID
     */
    public String getBlockerProfileID() {
        return blockerProfileID;
    }

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     * @param blockerProfileID
     * The blocker_profile_ID
     */
    public void setBlockerProfileID(String blockerProfileID) {
        this.blockerProfileID = blockerProfileID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(blockerProfileID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_BLOCK_USER) == false) {
            return false;
        }
        URL_BLOCK_USER rhs = ((URL_BLOCK_USER) other);
        return new EqualsBuilder().append(blockerProfileID, rhs.blockerProfileID).isEquals();
    }

}