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
 * RequestRespondRequest
 * <p>
 * Request body for the Respond Request method.
 *
 */
@Generated("org.jsonschema2pojo")
public class REQ_RESPOND_REQUEST {

    /**
     * response for the request.
     * (Required)
     *
     */
    @SerializedName("action")
    @Expose
    private REQ_RESPOND_REQUEST.Action action;

    /**
     * response for the request.
     * (Required)
     *
     * @return
     * The action
     */
    public REQ_RESPOND_REQUEST.Action getAction() {
        return action;
    }

    /**
     * response for the request.
     * (Required)
     *
     * @param action
     * The action
     */
    public void setAction(REQ_RESPOND_REQUEST.Action action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(action).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof REQ_RESPOND_REQUEST) == false) {
            return false;
        }
        REQ_RESPOND_REQUEST rhs = ((REQ_RESPOND_REQUEST) other);
        return new EqualsBuilder().append(action, rhs.action).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Action {

        @SerializedName("accept")
        ACCEPT("accept"),
        @SerializedName("ignore")
        IGNORE("ignore"),
        @SerializedName("cancel")
        CANCEL("cancel");
        private final String value;
        private final static Map<String, REQ_RESPOND_REQUEST.Action> CONSTANTS = new HashMap<String, REQ_RESPOND_REQUEST.Action>();

        static {
            for (REQ_RESPOND_REQUEST.Action c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Action(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static REQ_RESPOND_REQUEST.Action fromValue(String value) {
            REQ_RESPOND_REQUEST.Action constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}