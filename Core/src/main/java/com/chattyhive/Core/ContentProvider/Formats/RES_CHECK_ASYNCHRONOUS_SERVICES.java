package com.chattyhive.Core.ContentProvider.Formats;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponseCheckAsynchronousServices
 * <p>
 * Response body for the Check Asynchronous Services method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_CHECK_ASYNCHRONOUS_SERVICES {

    /**
     * Device identifier for the registered device into chattyhive server.
     *
     */
    @SerializedName("dev_id")
    @Expose
    private String devId;
    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     * (Required)
     *
     */
    @SerializedName("services")
    @Expose
    private Set<SERVI_CE> services = new LinkedHashSet<SERVI_CE>();

    /**
     * Device identifier for the registered device into chattyhive server.
     *
     * @return
     * The devId
     */
    public String getDevId() {
        return devId;
    }

    /**
     * Device identifier for the registered device into chattyhive server.
     *
     * @param devId
     * The dev_id
     */
    public void setDevId(String devId) {
        this.devId = devId;
    }

    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     * (Required)
     *
     * @return
     * The services
     */
    public Set<SERVI_CE> getServices() {
        return services;
    }

    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     * (Required)
     *
     * @param services
     * The services
     */
    public void setServices(Set<SERVI_CE> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(devId).append(services).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_CHECK_ASYNCHRONOUS_SERVICES) == false) {
            return false;
        }
        RES_CHECK_ASYNCHRONOUS_SERVICES rhs = ((RES_CHECK_ASYNCHRONOUS_SERVICES) other);
        return new EqualsBuilder().append(devId, rhs.devId).append(services, rhs.services).isEquals();
    }

}
