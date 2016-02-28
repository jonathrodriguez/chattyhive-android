package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Hive subscription
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_PROFILE_HIVE_LIST {

    /**
     * Last activity timestamp. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     */
    @SerializedName("profile_last_activity")
    @Expose
    private String profileLastActivity;
    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     */
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    /**
     * Hive object
     * (Required)
     *
     */
    @SerializedName("hive")
    @Expose
    private HIVE hive;

    /**
     * Last activity timestamp. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @return
     * The profileLastActivity
     */
    public String getProfileLastActivity() {
        return profileLastActivity;
    }

    /**
     * Last activity timestamp. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @param profileLastActivity
     * The profile_last_activity
     */
    public void setProfileLastActivity(String profileLastActivity) {
        this.profileLastActivity = profileLastActivity;
    }

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @return
     * The creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @param creationDate
     * The creation_date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Hive object
     * (Required)
     *
     * @return
     * The hive
     */
    public HIVE getHive() {
        return hive;
    }

    /**
     * Hive object
     * (Required)
     *
     * @param hive
     * The hive
     */
    public void setHive(HIVE hive) {
        this.hive = hive;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(profileLastActivity).append(creationDate).append(hive).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_PROFILE_HIVE_LIST) == false) {
            return false;
        }
        RES_PROFILE_HIVE_LIST rhs = ((RES_PROFILE_HIVE_LIST) other);
        return new EqualsBuilder().append(profileLastActivity, rhs.profileLastActivity).append(creationDate, rhs.creationDate).append(hive, rhs.hive).isEquals();
    }

}