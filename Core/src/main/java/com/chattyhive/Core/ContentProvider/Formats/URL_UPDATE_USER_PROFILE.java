package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsUpdateUserProfile
 * <p>
 * URL params for the Update User Profile method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_UPDATE_USER_PROFILE {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @param publicName
     * The public_name
     */
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(publicName).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_UPDATE_USER_PROFILE) == false) {
            return false;
        }
        URL_UPDATE_USER_PROFILE rhs = ((URL_UPDATE_USER_PROFILE) other);
        return new EqualsBuilder().append(publicName, rhs.publicName).isEquals();
    }

}