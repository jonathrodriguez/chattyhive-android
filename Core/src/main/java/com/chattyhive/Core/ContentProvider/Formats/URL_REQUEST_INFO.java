package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsRequestInfo
 * <p>
 * URL params for the Request Info method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_REQUEST_INFO {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("profile_ID")
    @Expose
    private String profileID;
    /**
     * PATH 2. identifier of the request.
     * (Required)
     *
     */
    @SerializedName("request_ID")
    @Expose
    private String requestID;

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

    /**
     * PATH 2. identifier of the request.
     * (Required)
     *
     * @return
     * The requestID
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * PATH 2. identifier of the request.
     * (Required)
     *
     * @param requestID
     * The request_ID
     */
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(profileID).append(requestID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_REQUEST_INFO) == false) {
            return false;
        }
        URL_REQUEST_INFO rhs = ((URL_REQUEST_INFO) other);
        return new EqualsBuilder().append(profileID, rhs.profileID).append(requestID, rhs.requestID).isEquals();
    }

}