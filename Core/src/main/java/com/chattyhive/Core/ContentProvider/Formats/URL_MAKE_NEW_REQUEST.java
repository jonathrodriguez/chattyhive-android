package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsMakeNewRequest
 * <p>
 * URL params for the Make New Request method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_MAKE_NEW_REQUEST {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("profile_ID")
    @Expose
    private String profileID;

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @return
     * The profileID
     */
    public String getProfileID() {
        return profileID;
    }

    /**
     * PATH 1. public_name of the user.
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
        if ((other instanceof URL_MAKE_NEW_REQUEST) == false) {
            return false;
        }
        URL_MAKE_NEW_REQUEST rhs = ((URL_MAKE_NEW_REQUEST) other);
        return new EqualsBuilder().append(profileID, rhs.profileID).isEquals();
    }

}