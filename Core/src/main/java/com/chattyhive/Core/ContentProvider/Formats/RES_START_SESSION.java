package com.chattyhive.Core.ContentProvider.Formats;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Generated;


/**
 * ResponseStartSession
 * <p>
 * Response body for the Start Session method. This method is used to retrieve the X-CSRF Token.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_START_SESSION {

    /**
     * The X-CSRF Token
     * (Required)
     *
     */
    @SerializedName("csrf")
    @Expose
    private String csrf;

    /**
     * The X-CSRF Token
     * (Required)
     *
     * @return
     * The csrf
     */
    public String getCsrf() {
        return csrf;
    }

    /**
     * The X-CSRF Token
     * (Required)
     *
     * @param csrf
     * The csrf
     */
    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(csrf).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_START_SESSION) == false) {
            return false;
        }
        RES_START_SESSION rhs = ((RES_START_SESSION) other);
        return new EqualsBuilder().append(csrf, rhs.csrf).isEquals();
    }

}