

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
 * ParamsGetUserProfile
 * <p>
 * URL params for the Get User Profile method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_GET_USER_PROFILE {

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;
    /**
     * PATH 2. Requested profile type.
     * (Required)
     *
     */
    @SerializedName("type")
    @Expose
    private URL_GET_USER_PROFILE.Type type;
    /**
     * QUERY. Requested profile package. Content relies on profile type.
     *      - public+basic: public_name, avatar, personal_color, public_status.
     *      - private+basic: public_name, first_name, last_name, avatar, personal_color,
     *                       picture, private_status.
     *      - logged_profile+basic: user_name, public_name, avatar, personal_color,
     *                              public_status, email, first_name, last_name, picture,
     *                              private_status.
     *      - public|private+info: birth_date, sex, languages, country, region, city,
     *                             coordinates (visible filds only).
     *      - logged_profile+info: birth_date, sex, languages, country, region, city,
     *                             coordinates, public_show_age, public_show_sex,
     *                             public_show_location, private_show_age,  private_show_location.
     *      - any+hives: (hive subscription list).
     *      - any+complete: basic+info+hives
     * (Required)
     *
     */
    @SerializedName("package")
    @Expose
    private URL_GET_USER_PROFILE.Package _package;

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * PATH 1. public_name of the user.
     * (Required)
     *
     * @param publicName
     * The public_name
     */
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * PATH 2. Requested profile type.
     * (Required)
     *
     * @return
     * The type
     */
    public URL_GET_USER_PROFILE.Type getType() {
        return type;
    }

    /**
     * PATH 2. Requested profile type.
     * (Required)
     *
     * @param type
     * The type
     */
    public void setType(URL_GET_USER_PROFILE.Type type) {
        this.type = type;
    }

    /**
     * QUERY. Requested profile package. Content relies on profile type.
     * (Required)
     *
     * @return
     * The _package
     */
    public URL_GET_USER_PROFILE.Package getPackage() {
        return _package;
    }

    /**
     * QUERY. Requested profile package. Content relies on profile type.
     * (Required)
     *
     * @param _package
     * The package
     */
    public void setPackage(URL_GET_USER_PROFILE.Package _package) {
        this._package = _package;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(publicName).append(type).append(_package).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof URL_GET_USER_PROFILE) == false) {
            return false;
        }
        URL_GET_USER_PROFILE rhs = ((URL_GET_USER_PROFILE) other);
        return new EqualsBuilder().append(publicName, rhs.publicName).append(type, rhs.type).append(_package, rhs._package).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Package {

        @SerializedName("basic")
        BASIC("basic"),
        @SerializedName("info")
        INFO("info"),
        @SerializedName("hives")
        HIVES("hives"),
        @SerializedName("complete")
        COMPLETE("complete");
        private final String value;
        private final static Map<String, URL_GET_USER_PROFILE.Package> CONSTANTS = new HashMap<String, URL_GET_USER_PROFILE.Package>();

        static {
            for (URL_GET_USER_PROFILE.Package c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Package(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static URL_GET_USER_PROFILE.Package fromValue(String value) {
            URL_GET_USER_PROFILE.Package constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        @SerializedName("private")
        PRIVATE("private"),
        @SerializedName("public")
        PUBLIC("public"),
        @SerializedName("logged_profile")
        LOGGED_PROFILE("logged_profile");
        private final String value;
        private final static Map<String, URL_GET_USER_PROFILE.Type> CONSTANTS = new HashMap<String, URL_GET_USER_PROFILE.Type>();

        static {
            for (URL_GET_USER_PROFILE.Type c: values()) {
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

        public static URL_GET_USER_PROFILE.Type fromValue(String value) {
            URL_GET_USER_PROFILE.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
