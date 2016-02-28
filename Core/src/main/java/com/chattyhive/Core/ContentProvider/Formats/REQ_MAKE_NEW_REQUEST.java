package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestMakeNewRequest
 * <p>
 * Request body for the Make New Request method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_MAKE_NEW_REQUEST {

    /**
     * public_name of the requested user.
     * (Required)
     *
     */
    @SerializedName("requested_profile_ID")
    @Expose
    private String requestedProfileID;

    /**
     * public_name of the requested user.
     * (Required)
     *
     * @return
     * The requestedProfileID
     */
    public String getRequestedProfileID() {
        return requestedProfileID;
    }

    /**
     * public_name of the requested user.
     * (Required)
     *
     * @param requestedProfileID
     * The requested_profile_ID
     */
    public void setRequestedProfileID(String requestedProfileID) {
        this.requestedProfileID = requestedProfileID;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(requestedProfileID).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_MAKE_NEW_REQUEST) == false) {
            return false;
        }
        REQ_MAKE_NEW_REQUEST rhs = ((REQ_MAKE_NEW_REQUEST) other);
        return new EqualsBuilder().append(requestedProfileID, rhs.requestedProfileID).isEquals();
    }

}