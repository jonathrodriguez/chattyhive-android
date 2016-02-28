        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponseRequestUploadURL
 * <p>
 * Response body for the Request Upload URL method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_REQUEST_UPLOAD_URL {

    /**
     * URL value.
     * (Required)
     *
     */
    @SerializedName("url")
    @Expose
    private String url;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(url).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_REQUEST_UPLOAD_URL) == false) {
            return false;
        }
        RES_REQUEST_UPLOAD_URL rhs = ((RES_REQUEST_UPLOAD_URL) other);
        return new EqualsBuilder().append(url, rhs.url).isEquals();
    }

}
