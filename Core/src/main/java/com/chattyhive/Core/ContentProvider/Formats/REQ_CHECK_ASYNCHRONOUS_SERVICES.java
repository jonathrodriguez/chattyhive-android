package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestCheckAsynchronousServices
 * <p>
 * Request body for the Check Asynchronous Services method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_CHECK_ASYNCHRONOUS_SERVICES {

    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     */
    @SerializedName("dev_os")
    @Expose
    private REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs devOs;
    /**
     * Device identifier for an already registered device into chattyhive server. IMPORTANT. The server could also use this opportunity to tell the client to update its dev_id, so the client should check if the dev_id returned by the server is a different one.
     * (Required)
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
    private Set<SERVICE> services = new LinkedHashSet<SERVICE>();

    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     * @return
     * The devOs
     */
    public REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs getDevOs() {
        return devOs;
    }

    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     * @param devOs
     * The dev_os
     */
    public void setDevOs(REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs devOs) {
        this.devOs = devOs;
    }

    /**
     * Device identifier for an already registered device into chattyhive server. IMPORTANT. The server could also use this opportunity to tell the client to update its dev_id, so the client should check if the dev_id returned by the server is a different one.
     * (Required)
     *
     * @return
     * The devId
     */
    public String getDevId() {
        return devId;
    }

    /**
     * Device identifier for an already registered device into chattyhive server. IMPORTANT. The server could also use this opportunity to tell the client to update its dev_id, so the client should check if the dev_id returned by the server is a different one.
     * (Required)
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
    public Set<SERVICE> getServices() {
        return services;
    }

    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     * (Required)
     *
     * @param services
     * The services
     */
    public void setServices(Set<SERVICE> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(devOs).append(devId).append(services).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_CHECK_ASYNCHRONOUS_SERVICES) == false) {
            return false;
        }
        REQ_CHECK_ASYNCHRONOUS_SERVICES rhs = ((REQ_CHECK_ASYNCHRONOUS_SERVICES) other);
        return new EqualsBuilder().append(devOs, rhs.devOs).append(devId, rhs.devId).append(services, rhs.services).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum DevOs {

        @SerializedName("android")
        ANDROID("android"),
        @SerializedName("ios")
        IOS("ios"),
        @SerializedName("wp")
        WP("wp"),
        @SerializedName("browser")
        BROWSER("browser"),
        @SerializedName("windows")
        WINDOWS("windows"),
        @SerializedName("linux")
        LINUX("linux"),
        @SerializedName("mac")
        MAC("mac");
        private final String value;
        private final static Map<String, REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs> CONSTANTS = new HashMap<String, REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs>();

        static {
            for (REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private DevOs(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs fromValue(String value) {
            REQ_CHECK_ASYNCHRONOUS_SERVICES.DevOs constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}