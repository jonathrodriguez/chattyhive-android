        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponseEmailGet
 * <p>
 * Response body for the Email get method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_EMAIL_GET {

    /**
     * Email address.
     * (Required)
     *
     */
    @SerializedName("email")
    @Expose
    private String email;

    /**
     * Email address.
     * (Required)
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email address.
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
        if ((other instanceof RES_EMAIL_GET) == false) {
            return false;
        }
        RES_EMAIL_GET rhs = ((RES_EMAIL_GET) other);
        return new EqualsBuilder().append(email, rhs.email).isEquals();
    }

}
