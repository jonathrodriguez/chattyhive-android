        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestPublicNameCheck
 * <p>
 * Request body for the Public_name Check method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_PUBLIC_NAME_CHECK {

    /**
     * Public_name to check.
     * (Required)
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;

    /**
     * Public_name to check.
     * (Required)
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * Public_name to check.
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
        if ((other instanceof REQ_PUBLIC_NAME_CHECK) == false) {
            return false;
        }
        REQ_PUBLIC_NAME_CHECK rhs = ((REQ_PUBLIC_NAME_CHECK) other);
        return new EqualsBuilder().append(publicName, rhs.publicName).isEquals();
    }

}
