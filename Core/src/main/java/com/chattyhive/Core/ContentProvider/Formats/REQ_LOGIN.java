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
 * RequestLogin
 * <p>
 * Request body for the Login method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_LOGIN {

    /**
     * User's public name
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;
    /**
     * User's email
     *
     */
    @SerializedName("email")
    @Expose
    private String email;
    /**
     * User's passowrd
     * (Required)
     *
     */
    @SerializedName("password")
    @Expose
    private String password;
    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     */
    @SerializedName("dev_os")
    @Expose
    private REQ_LOGIN.DevOs devOs;
    /**
     * Device type.
     *
     */
    @SerializedName("dev_type")
    @Expose
    private REQ_LOGIN.DevType devType;
    /**
     * Device internal identifier for android and ios devices (something like IMEI).
     *
     */
    @SerializedName("dev_code")
    @Expose
    private String devCode;
    /**
     * Device identifier for an already registered device into chattyhive server. Empty if unknown.
     *
     */
    @SerializedName("dev_id")
    @Expose
    private String devId;
    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     *
     */
    @SerializedName("services")
    @Expose
    private Set<SERVICE> services = new LinkedHashSet<SERVICE>();

    /**
     * User's public name
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * User's public name
     *
     * @param publicName
     * The public_name
     */
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * User's email
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * User's email
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * User's passowrd
     * (Required)
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     * User's passowrd
     * (Required)
     *
     * @param password
     * The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     * @return
     * The devOs
     */
    public REQ_LOGIN.DevOs getDevOs() {
        return devOs;
    }

    /**
     * Device's operative system. Note that is the platform where the chattyhive app is running not the current running real operative system.
     *
     * @param devOs
     * The dev_os
     */
    public void setDevOs(REQ_LOGIN.DevOs devOs) {
        this.devOs = devOs;
    }

    /**
     * Device type.
     *
     * @return
     * The devType
     */
    public REQ_LOGIN.DevType getDevType() {
        return devType;
    }

    /**
     * Device type.
     *
     * @param devType
     * The dev_type
     */
    public void setDevType(REQ_LOGIN.DevType devType) {
        this.devType = devType;
    }

    /**
     * Device internal identifier for android and ios devices (something like IMEI).
     *
     * @return
     * The devCode
     */
    public String getDevCode() {
        return devCode;
    }

    /**
     * Device internal identifier for android and ios devices (something like IMEI).
     *
     * @param devCode
     * The dev_code
     */
    public void setDevCode(String devCode) {
        this.devCode = devCode;
    }

    /**
     * Device identifier for an already registered device into chattyhive server. Empty if unknown.
     *
     * @return
     * The devId
     */
    public String getDevId() {
        return devId;
    }

    /**
     * Device identifier for an already registered device into chattyhive server. Empty if unknown.
     *
     * @param devId
     * The dev_id
     */
    public void setDevId(String devId) {
        this.devId = devId;
    }

    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
     *
     * @return
     * The services
     */
    public Set<SERVICE> getServices() {
        return services;
    }

    /**
     * List of app-known third party services used by the chattyhive service. Thus the server can determine the communication capabilities of the app.
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
        return new HashCodeBuilder().append(publicName).append(email).append(password).append(devOs).append(devType).append(devCode).append(devId).append(services).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_LOGIN) == false) {
            return false;
        }
        REQ_LOGIN rhs = ((REQ_LOGIN) other);
        return new EqualsBuilder().append(publicName, rhs.publicName).append(email, rhs.email).append(password, rhs.password).append(devOs, rhs.devOs).append(devType, rhs.devType).append(devCode, rhs.devCode).append(devId, rhs.devId).append(services, rhs.services).isEquals();
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
        private final static Map<String, REQ_LOGIN.DevOs> CONSTANTS = new HashMap<String, REQ_LOGIN.DevOs>();

        static {
            for (REQ_LOGIN.DevOs c: values()) {
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

        public static REQ_LOGIN.DevOs fromValue(String value) {
            REQ_LOGIN.DevOs constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Generated("org.jsonschema2pojo")
    public static enum DevType {

        @SerializedName("smartphone")
        SMARTPHONE("smartphone"),
        @SerializedName("6-8tablet")
        _6_8_TABLET("6-8tablet"),
        @SerializedName("big_tablet")
        BIG_TABLET("big_tablet"),
        @SerializedName("laptop")
        LAPTOP("laptop"),
        @SerializedName("desktop")
        DESKTOP("desktop"),
        @SerializedName("big_screen_desktop")
        BIG_SCREEN_DESKTOP("big_screen_desktop"),
        @SerializedName("tv")
        TV("tv");
        private final String value;
        private final static Map<String, REQ_LOGIN.DevType> CONSTANTS = new HashMap<String, REQ_LOGIN.DevType>();

        static {
            for (REQ_LOGIN.DevType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private DevType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static REQ_LOGIN.DevType fromValue(String value) {
            REQ_LOGIN.DevType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
