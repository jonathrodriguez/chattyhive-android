package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * RequestPasswordChange
 * <p>
 * Request body for the Password Change method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_PASSWORD_CHANGE {

    /**
     * User's actual password (to authenticate request).
     * (Required)
     *
     */
    @SerializedName("old_password")
    @Expose
    private String oldPassword;
    /**
     * User's new password.
     * (Required)
     *
     */
    @SerializedName("new_password")
    @Expose
    private String newPassword;

    /**
     * User's actual password (to authenticate request).
     * (Required)
     *
     * @return
     * The oldPassword
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * User's actual password (to authenticate request).
     * (Required)
     *
     * @param oldPassword
     * The old_password
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * User's new password.
     * (Required)
     *
     * @return
     * The newPassword
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * User's new password.
     * (Required)
     *
     * @param newPassword
     * The new_password
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(oldPassword).append(newPassword).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_PASSWORD_CHANGE) == false) {
            return false;
        }
        REQ_PASSWORD_CHANGE rhs = ((REQ_PASSWORD_CHANGE) other);
        return new EqualsBuilder().append(oldPassword, rhs.oldPassword).append(newPassword, rhs.newPassword).isEquals();
    }

}