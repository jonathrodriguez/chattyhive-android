

package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 *
 *
 */
@Generated("org.jsonschema2pojo")
public class USER {

    /**
     * User's email
     * (Required)
     *
     */
    @SerializedName("email")
    @Expose
    private String email;
    /**
     * User identifier.
     * (Required)
     *
     */
    @SerializedName("username")
    @Expose
    private String username;

    /**
     * User's email
     * (Required)
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * User's email
     * (Required)
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * User identifier.
     * (Required)
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * User identifier.
     * (Required)
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(email).append(username).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof USER) == false) {
            return false;
        }
        USER rhs = ((USER) other);
        return new EqualsBuilder().append(email, rhs.email).append(username, rhs.username).isEquals();
    }

}
