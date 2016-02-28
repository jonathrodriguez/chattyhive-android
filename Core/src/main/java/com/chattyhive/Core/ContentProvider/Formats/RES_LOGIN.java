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
 * ResponseLogin
 * <p>
 * Response body for the Login method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_LOGIN {

    /**
     * User's public name
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;
    /**
     * User's email verification status. 'unverified' if (warned=False & verified=False & active=True) and the expiration_date is not yet due, 'warn' if (warned=False & verified=False & active=True) but expiration_date is due at the time of the login attempt, 'warned' if the the warning has been already issued (warned=True & verified=False & active=True), 'expired' if the warning time expired (warned=False & verified=False & active=False). Optional
     *
     */
    @SerializedName("email_verification")
    @Expose
    private RES_LOGIN.EmailVerification emailVerification;
    /**
     * If email_verification == 'unverified' then we send the DateTime for the first expiration_date (if next login attempt is has expiration_date due then account will be marked as 'warned'. If email_verification == 'warn' or 'warned' then expiration_date == DateTime for the extended expiration_date, from this date the account will be marked as disabled if user tries to log in (unless user verifies account before). In both cases as soon as we implement a background process cleaning and fixing stuff, this process will be able to mark as warned (sending warning email to user) or to disable accounts if the dates are due and not needing the user to login for that (once this process is working the 'warn' state in the response won't make any sense.
     *
     */
    @SerializedName("expiration_date")
    @Expose
    private String expirationDate;
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
     * User's email verification status. 'unverified' if (warned=False & verified=False & active=True) and the expiration_date is not yet due, 'warn' if (warned=False & verified=False & active=True) but expiration_date is due at the time of the login attempt, 'warned' if the the warning has been already issued (warned=True & verified=False & active=True), 'expired' if the warning time expired (warned=False & verified=False & active=False). Optional
     *
     * @return
     * The emailVerification
     */
    public RES_LOGIN.EmailVerification getEmailVerification() {
        return emailVerification;
    }

    /**
     * User's email verification status. 'unverified' if (warned=False & verified=False & active=True) and the expiration_date is not yet due, 'warn' if (warned=False & verified=False & active=True) but expiration_date is due at the time of the login attempt, 'warned' if the the warning has been already issued (warned=True & verified=False & active=True), 'expired' if the warning time expired (warned=False & verified=False & active=False). Optional
     *
     * @param emailVerification
     * The email_verification
     */
    public void setEmailVerification(RES_LOGIN.EmailVerification emailVerification) {
        this.emailVerification = emailVerification;
    }

    /**
     * If email_verification == 'unverified' then we send the DateTime for the first expiration_date (if next login attempt is has expiration_date due then account will be marked as 'warned'. If email_verification == 'warn' or 'warned' then expiration_date == DateTime for the extended expiration_date, from this date the account will be marked as disabled if user tries to log in (unless user verifies account before). In both cases as soon as we implement a background process cleaning and fixing stuff, this process will be able to mark as warned (sending warning email to user) or to disable accounts if the dates are due and not needing the user to login for that (once this process is working the 'warn' state in the response won't make any sense.
     *
     * @return
     * The expirationDate
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * If email_verification == 'unverified' then we send the DateTime for the first expiration_date (if next login attempt is has expiration_date due then account will be marked as 'warned'. If email_verification == 'warn' or 'warned' then expiration_date == DateTime for the extended expiration_date, from this date the account will be marked as disabled if user tries to log in (unless user verifies account before). In both cases as soon as we implement a background process cleaning and fixing stuff, this process will be able to mark as warned (sending warning email to user) or to disable accounts if the dates are due and not needing the user to login for that (once this process is working the 'warn' state in the response won't make any sense.
     *
     * @param expirationDate
     * The expiration_date
     */
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

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
        return new HashCodeBuilder().append(publicName).append(emailVerification).append(expirationDate).append(devId).append(services).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_LOGIN) == false) {
            return false;
        }
        RES_LOGIN rhs = ((RES_LOGIN) other);
        return new EqualsBuilder().append(publicName, rhs.publicName).append(emailVerification, rhs.emailVerification).append(expirationDate, rhs.expirationDate).append(devId, rhs.devId).append(services, rhs.services).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum EmailVerification {

        @SerializedName("unverified")
        UNVERIFIED("unverified"),
        @SerializedName("warn")
        WARN("warn"),
        @SerializedName("warned")
        WARNED("warned"),
        @SerializedName("expired")
        EXPIRED("expired");
        private final String value;
        private final static Map<String, RES_LOGIN.EmailVerification> CONSTANTS = new HashMap<String, RES_LOGIN.EmailVerification>();

        static {
            for (RES_LOGIN.EmailVerification c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private EmailVerification(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static RES_LOGIN.EmailVerification fromValue(String value) {
            RES_LOGIN.EmailVerification constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}