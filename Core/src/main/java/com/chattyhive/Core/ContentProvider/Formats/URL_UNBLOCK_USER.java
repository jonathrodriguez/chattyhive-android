package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsUnblockUser
 * <p>
 * URL params for the Unblock User method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_UNBLOCK_USER {

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     */
    @SerializedName("blocker_profile_ID")
    @Expose
    private String blockerProfileID;
    /**
     * PATH 2. public_name of the blocked user.
     * (Required)
     *
     */
    @SerializedName("blocked_profile_ID")
    @Expose
    private String blockedProfileID;

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

    /**
     * PATH 2. public_name of the blocked user.
     * (Required)
     *
     * @return
     * The blockedProfileID
     */
    public String getBlockedProfileID() {
        return blockedProfileID;
    }

    /**
     * PATH 2. public_name of the blocked user.
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
        return new HashCodeBuilder().append(blockerProfileID).append(blockedProfileID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_UNBLOCK_USER) == false) {
            return false;
        }
        URL_UNBLOCK_USER rhs = ((URL_UNBLOCK_USER) other);
        return new EqualsBuilder().append(blockerProfileID, rhs.blockerProfileID).append(blockedProfileID, rhs.blockedProfileID).isEquals();
    }

}