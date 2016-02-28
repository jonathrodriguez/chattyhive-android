        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponsePublicNameCheck
 * <p>
 * Response body for the Public_name Check method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_PUBLIC_NAME_CHECK {

    /**
     * Proposed public_name if the checked one existed.
     *
     */
    @SerializedName("proposed_username")
    @Expose
    private String proposedUsername;

    /**
     * Proposed public_name if the checked one existed.
     *
     * @return
     * The proposedUsername
     */
    public String getProposedUsername() {
        return proposedUsername;
    }

    /**
     * Proposed public_name if the checked one existed.
     *
     * @param proposedUsername
     * The proposed_username
     */
    public void setProposedUsername(String proposedUsername) {
        this.proposedUsername = proposedUsername;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(proposedUsername).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_PUBLIC_NAME_CHECK) == false) {
            return false;
        }
        RES_PUBLIC_NAME_CHECK rhs = ((RES_PUBLIC_NAME_CHECK) other);
        return new EqualsBuilder().append(proposedUsername, rhs.proposedUsername).isEquals();
    }

}
