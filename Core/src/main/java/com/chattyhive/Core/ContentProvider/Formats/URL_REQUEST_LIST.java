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
 * ParamsRequestList
 * <p>
 * URL params for the Request List method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_REQUEST_LIST {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("profile_ID")
    @Expose
    private String profileID;
    /**
     * PATH 2. type of request: incoming or outgoing.
     * (Required)
     *
     */
    @SerializedName("type")
    @Expose
    private URL_REQUEST_LIST.Type type;
    /**
     * QUERY. start point of the requested list.
     *
     */
    @SerializedName("start")
    @Expose
    private String start;
    /**
     * QUERY. end point of the requested list.
     *
     */
    @SerializedName("end")
    @Expose
    private String end;
    /**
     * QUERY. number of elements requested.
     *
     */
    @SerializedName("elements")
    @Expose
    private int elements;

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @return
     * The profileID
     */
    public String getProfileID() {
        return profileID;
    }

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @param profileID
     * The profile_ID
     */
    public void setProfileID(String profileID) {
        this.profileID = profileID;
    }

    /**
     * PATH 2. type of request: incoming or outgoing.
     * (Required)
     *
     * @return
     * The type
     */
    public URL_REQUEST_LIST.Type getType() {
        return type;
    }

    /**
     * PATH 2. type of request: incoming or outgoing.
     * (Required)
     *
     * @param type
     * The type
     */
    public void setType(URL_REQUEST_LIST.Type type) {
        this.type = type;
    }

    /**
     * QUERY. start point of the requested list.
     *
     * @return
     * The start
     */
    public String getStart() {
        return start;
    }

    /**
     * QUERY. start point of the requested list.
     *
     * @param start
     * The start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * QUERY. end point of the requested list.
     *
     * @return
     * The end
     */
    public String getEnd() {
        return end;
    }

    /**
     * QUERY. end point of the requested list.
     *
     * @param end
     * The end
     */
    public void setEnd(String end) {
        this.end = end;
    }

    /**
     * QUERY. number of elements requested.
     *
     * @return
     * The elements
     */
    public int getElements() {
        return elements;
    }

    /**
     * QUERY. number of elements requested.
     *
     * @param elements
     * The elements
     */
    public void setElements(int elements) {
        this.elements = elements;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(profileID).append(type).append(start).append(end).append(elements).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_REQUEST_LIST) == false) {
            return false;
        }
        URL_REQUEST_LIST rhs = ((URL_REQUEST_LIST) other);
        return new EqualsBuilder().append(profileID, rhs.profileID).append(type, rhs.type).append(start, rhs.start).append(end, rhs.end).append(elements, rhs.elements).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        @SerializedName("in")
        IN("in"),
        @SerializedName("out")
        OUT("out");
        private final String value;
        private final static Map<String, URL_REQUEST_LIST.Type> CONSTANTS = new HashMap<String, URL_REQUEST_LIST.Type>();

        static {
            for (URL_REQUEST_LIST.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static URL_REQUEST_LIST.Type fromValue(String value) {
            URL_REQUEST_LIST.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}