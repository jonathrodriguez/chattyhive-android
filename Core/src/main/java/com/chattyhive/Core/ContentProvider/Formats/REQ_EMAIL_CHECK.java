
        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestEmailCheck
 * <p>
 * Request body for the Email check method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_EMAIL_CHECK {

    /**
     * Email address to check.
     * (Required)
     *
     */
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * Email address to check.
     * (Required)
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email address to check.
     * (Required)
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(email).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_EMAIL_CHECK) == false) {
            return false;
        }
        REQ_EMAIL_CHECK rhs = ((REQ_EMAIL_CHECK) other);
        return new EqualsBuilder().append(email, rhs.email).isEquals();
    }

}
