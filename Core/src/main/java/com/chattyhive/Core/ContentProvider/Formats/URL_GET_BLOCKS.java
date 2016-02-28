package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsGetBlocks
 * <p>
 * URL params for the Get Blocks method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_GET_BLOCKS {

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     */
    @SerializedName("blocker_profile_ID")
    @Expose
    private String blockerProfileID;
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
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     * @return
     * The blockerProfileID
     */
    public String getBlockerProfileID() {
        return blockerProfileID;
    }

    /**
     * PATH 1. public_name of the blocker user.
     * (Required)
     *
     * @param blockerProfileID
     * The blocker_profile_ID
     */
    public void setBlockerProfileID(String blockerProfileID) {
        this.blockerProfileID = blockerProfileID;
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
        return new HashCodeBuilder().append(blockerProfileID).append(start).append(end).append(elements).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_GET_BLOCKS) == false) {
            return false;
        }
        URL_GET_BLOCKS rhs = ((URL_GET_BLOCKS) other);
        return new EqualsBuilder().append(blockerProfileID, rhs.blockerProfileID).append(start, rhs.start).append(end, rhs.end).append(elements, rhs.elements).isEquals();
    }

}