        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestEmailChange
 * <p>
 * Request body for the Email Change method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_EMAIL_CHANGE {

    /**
     * New email to set.
     * (Required)
     *
     */
    @SerializedName("new_email")
    @Expose
    private String newEmail;
    /**
     * User's password.
     * (Required)
     *
     */
    @SerializedName("password")
    @Expose
    private String password;

    /**
     * New email to set.
     * (Required)
     *
     * @return
     * The newEmail
     */
    public String getNewEmail() {
        return newEmail;
    }

    /**
     * New email to set.
     * (Required)
     *
     * @param newEmail
     * The new_email
     */
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(newEmail).append(password).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_EMAIL_CHANGE) == false) {
            return false;
        }
        REQ_EMAIL_CHANGE rhs = ((REQ_EMAIL_CHANGE) other);
        return new EqualsBuilder().append(newEmail, rhs.newEmail).append(password, rhs.password).isEquals();
    }

}
