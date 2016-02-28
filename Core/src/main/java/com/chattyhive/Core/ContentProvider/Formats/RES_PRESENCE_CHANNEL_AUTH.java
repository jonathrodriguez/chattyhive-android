
        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponsePresenceChannelAuth
 * <p>
 * Response body for the Presence-channel Auth method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_PRESENCE_CHANNEL_AUTH {

    /**
     * URL value.
     * (Required)
     *
     */
    @SerializedName("url")
    @Expose
    private String url;
    /**
     * Signature to authorize the channel subscription.
     * (Required)
     *
     */
    @SerializedName("signature")
    @Expose
    private String signature;

    /**
     * URL value.
     * (Required)
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * URL value.
     * (Required)
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Signature to authorize the channel subscription.
     * (Required)
     *
     * @return
     * The signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Signature to authorize the channel subscription.
     * (Required)
     *
     * @param signature
     * The signature
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(url).append(signature).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_PRESENCE_CHANNEL_AUTH) == false) {
            return false;
        }
        RES_PRESENCE_CHANNEL_AUTH rhs = ((RES_PRESENCE_CHANNEL_AUTH) other);
        return new EqualsBuilder().append(url, rhs.url).append(signature, rhs.signature).isEquals();
    }

}