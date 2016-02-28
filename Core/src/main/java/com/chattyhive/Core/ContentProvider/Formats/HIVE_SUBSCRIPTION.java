
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
public class HIVE_SUBSCRIPTION {

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    /**
     * Hive object
     *
     */
    @SerializedName("hive")
    @Expose
    private HIVE hive;

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Subscription date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param creationDate
     * The creation_date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Hive object
     *
     * @return
     * The hive
     */
    public HIVE getHive() {
        return hive;
    }

    /**
     * Hive object
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
        return new HashCodeBuilder().append(creationDate).append(hive).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof HIVE_SUBSCRIPTION) == false) {
            return false;
        }
        HIVE_SUBSCRIPTION rhs = ((HIVE_SUBSCRIPTION) other);
        return new EqualsBuilder().append(creationDate, rhs.creationDate).append(hive, rhs.hive).isEquals();
    }

}
