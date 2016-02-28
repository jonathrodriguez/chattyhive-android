        package com.chattyhive.Core.ContentProvider.Formats;

        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;
        import org.apache.commons.lang3.builder.EqualsBuilder;
        import org.apache.commons.lang3.builder.HashCodeBuilder;
        import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ParamsProfileHiveList
 * <p>
 * URL params for the Profile Hive List method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_PROFILE_HIVE_LIST {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("profile_ID")
    @Expose
    private String profileID;
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
        return new HashCodeBuilder().append(profileID).append(start).append(end).append(elements).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_PROFILE_HIVE_LIST) == false) {
            return false;
        }
        URL_PROFILE_HIVE_LIST rhs = ((URL_PROFILE_HIVE_LIST) other);
        return new EqualsBuilder().append(profileID, rhs.profileID).append(start, rhs.start).append(end, rhs.end).append(elements, rhs.elements).isEquals();
    }

}
