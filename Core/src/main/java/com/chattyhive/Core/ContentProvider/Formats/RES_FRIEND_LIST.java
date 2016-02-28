package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * List of friend users
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_FRIEND_LIST {

    /**
     * Public_name of the friend user.
     * (Required)
     *
     */
    @SerializedName("profile_ID")
    @Expose
    private String profileID;

    /**
     * Public_name of the friend user.
     * (Required)
     *
     * @return
     * The profileID
     */
    public String getProfileID() {
        return profileID;
    }

    /**
     * Public_name of the friend user.
     * (Required)
     *
     * @param profileID
     * The profile_ID
     */
    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(profileID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_FRIEND_LIST) == false) {
            return false;
        }
        RES_FRIEND_LIST rhs = ((RES_FRIEND_LIST) other);
        return new EqualsBuilder().append(profileID, rhs.profileID).isEquals();
    }

}