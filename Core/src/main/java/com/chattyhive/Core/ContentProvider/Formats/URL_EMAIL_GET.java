        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsEmailGet
 * <p>
 * URL params for the Email get method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_EMAIL_GET {

    /**
     * PATH 1. username or public_name of the user.
     * (Required)
     *
     */
    @SerializedName("username")
    @Expose
    private String username;

    /**
     * PATH 1. username or public_name of the user.
     * (Required)
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * PATH 1. username or public_name of the user.
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
        return new HashCodeBuilder().append(username).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_EMAIL_GET) == false) {
            return false;
        }
        URL_EMAIL_GET rhs = ((URL_EMAIL_GET) other);
        return new EqualsBuilder().append(username, rhs.username).isEquals();
    }

}
