package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestRegisterUser
 * <p>
 * Request body for the Register User method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_REGISTER_USER {

    /**
     * User's email.
     * (Required)
     *
     */
    @SerializedName("email")
    @Expose
    private String email;
    /**
     * User's password.
     * (Required)
     *
     */
    @SerializedName("password")
    @Expose
    private String password;
    /**
     * User's profile.
     * (Required)
     *
     */
    @SerializedName("profile")
    @Expose
    private PROFILE profile;

    /**
     * User's email.
     * (Required)
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * User's email.
     * (Required)
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * User's password.
     * (Required)
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * User's password.
     * (Required)
     *
     * @param password
     * The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * User's profile.
     * (Required)
     *
     * @return
     * The profile
     */
    public PROFILE getProfile() {
        return profile;
    }

    /**
     * User's profile.
     * (Required)
     *
     * @param profile
     * The profile
     */
    public void setProfile(PROFILE profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(email).append(password).append(profile).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_REGISTER_USER) == false) {
            return false;
        }
        REQ_REGISTER_USER rhs = ((REQ_REGISTER_USER) other);
        return new EqualsBuilder().append(email, rhs.email).append(password, rhs.password).append(profile, rhs.profile).isEquals();
    }

}
