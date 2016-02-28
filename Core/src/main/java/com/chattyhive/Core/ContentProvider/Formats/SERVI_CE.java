
package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Service
 * <p>
 * Third party service used by the chattyhive service.
 *
 */
@Generated("org.jsonschema2pojo")
public class SERVI_CE {

    /**
     * Service name/identifier.
     * (Required)
     *
     */
    @SerializedName("name")
    @Expose
    private SERVI_CE.Name name;
    /**
     * APP-Key used by the third party service.
     *
     */
    @SerializedName("app")
    @Expose
    private String app;
    /**
     * User identifier for the service. For example: with pusher this will be the socket_id and with gcm this will be the registration id
     *
     */
    @SerializedName("reg_id")
    @Expose
    private String regId;

    /**
     * Service name/identifier.
     * (Required)
     *
     * @return
     * The name
     */
    public SERVI_CE.Name getName() {
        return name;
    }

    /**
     * Service name/identifier.
     * (Required)
     *
     * @param name
     * The name
     */
    public void setName(SERVI_CE.Name name) {
        this.name = name;
    }

    /**
     * APP-Key used by the third party service.
     *
     * @return
     * The app
     */
    public String getApp() {
        return app;
    }

    /**
     * APP-Key used by the third party service.
     *
     * @param app
     * The app
     */
    public void setApp(String app) {
        this.app = app;
    }

    /**
     * User identifier for the service. For example: with pusher this will be the socket_id and with gcm this will be the registration id
     *
     * @return
     * The regId
     */
    public String getRegId() {
        return regId;
    }

    /**
     * User identifier for the service. For example: with pusher this will be the socket_id and with gcm this will be the registration id
     *
     * @param regId
     * The reg_id
     */
    public void setRegId(String regId) {
        this.regId = regId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(app).append(regId).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof SERVI_CE) == false) {
            return false;
        }
        SERVI_CE rhs = ((SERVI_CE) other);
        return new EqualsBuilder().append(name, rhs.name).append(app, rhs.app).append(regId, rhs.regId).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Name {

        @SerializedName("pusher")
        PUSHER("pusher"),
        @SerializedName("gcm")
        GCM("gcm");
        private final String value;
        private final static Map<String, SERVI_CE.Name> CONSTANTS = new HashMap<String, SERVI_CE.Name>();

        static {
            for (SERVI_CE.Name c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Name(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static SERVI_CE.Name fromValue(String value) {
            SERVI_CE.Name constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
