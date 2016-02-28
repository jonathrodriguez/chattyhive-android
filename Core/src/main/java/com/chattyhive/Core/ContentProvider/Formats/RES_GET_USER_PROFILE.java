
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
 * ResponseGetUserProfile
 * <p>
 * Response body for the Get User Profile method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_GET_USER_PROFILE {

    /**
     *
     *
     */
    @SerializedName("user")
    @Expose
    private USER user;
    /**
     * User's public_name
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;
    /**
     * User's avatar url
     *
     */
    @SerializedName("avatar")
    @Expose
    private String avatar;
    /**
     * User's personal color for public messages. In hex format RGB
     *
     */
    @SerializedName("personal_color")
    @Expose
    private String personalColor;
    /**
     * User's public status message
     *
     */
    @SerializedName("public_status")
    @Expose
    private String publicStatus;
    /**
     * User's first name
     *
     */
    @SerializedName("first_name")
    @Expose
    private String firstName;
    /**
     * User's last name
     *
     */
    @SerializedName("last_name")
    @Expose
    private String lastName;
    /**
     * User's profile picture url
     *
     */
    @SerializedName("picture")
    @Expose
    private String picture;
    /**
     * User's private status message
     *
     */
    @SerializedName("private_status")
    @Expose
    private String privateStatus;
    /**
     * User's birth date
     *
     */
    @SerializedName("birth_date")
    @Expose
    private String birthDate;
    /**
     * User's sex
     *
     */
    @SerializedName("sex")
    @Expose
    private RES_GET_USER_PROFILE.Sex sex;
    /**
     * User's language list.
     *
     */
    @SerializedName("languages")
    @Expose
    private Set<String> languages = new LinkedHashSet<String>();
    /**
     * User's country 2 character ISO code in uppercase
     *
     */
    @SerializedName("country")
    @Expose
    private String country;
    /**
     * User's region name
     *
     */
    @SerializedName("region")
    @Expose
    private String region;
    /**
     * User's city name
     *
     */
    @SerializedName("city")
    @Expose
    private String city;
    /**
     * List of hive subscriptions
     *
     */
    @SerializedName("hive_subscriptions")
    @Expose
    private Set<HIVE_SUBSCRIPTION> hiveSubscriptions = new LinkedHashSet<HIVE_SUBSCRIPTION>();
    /**
     * Flag to indicate if age should be shown in public profile
     *
     */
    @SerializedName("public_show_age")
    @Expose
    private boolean publicShowAge;
    /**
     * Flag to indicate if age should be shown in private profile
     *
     */
    @SerializedName("private_show_age")
    @Expose
    private boolean privateShowAge;
    /**
     * Flag to indicate if sex should be shown in public profile
     *
     */
    @SerializedName("public_show_sex")
    @Expose
    private boolean publicShowSex;
    /**
     * Flag to indicate if location should be shown in public profile
     *
     */
    @SerializedName("public_show_location")
    @Expose
    private boolean publicShowLocation;
    /**
     * Flag to indicate if location should be shown in private profile
     *
     */
    @SerializedName("private_show_location")
    @Expose
    private boolean privateShowLocation;
    /**
     * User's location coordinates; first latitude then longitude separated with space. example: 42.23282 -8.72264
     *
     */
    @SerializedName("coordinates")
    @Expose
    private String coordinates;

    /**
     *
     *
     * @return
     * The user
     */
    public USER getUser() {
        return user;
    }

    /**
     *
     *
     * @param user
     * The user
     */
    public void setUser(USER user) {
        this.user = user;
    }

    /**
     * User's public_name
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * User's public_name
     *
     * @param publicName
     * The public_name
     */
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * User's avatar url
     *
     * @return
     * The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * User's avatar url
     *
     * @param avatar
     * The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * User's personal color for public messages. In hex format RGB
     *
     * @return
     * The personalColor
     */
    public String getPersonalColor() {
        return personalColor;
    }

    /**
     * User's personal color for public messages. In hex format RGB
     *
     * @param personalColor
     * The personal_color
     */
    public void setPersonalColor(String personalColor) {
        this.personalColor = personalColor;
    }

    /**
     * User's public status message
     *
     * @return
     * The publicStatus
     */
    public String getPublicStatus() {
        return publicStatus;
    }

    /**
     * User's public status message
     *
     * @param publicStatus
     * The public_status
     */
    public void setPublicStatus(String publicStatus) {
        this.publicStatus = publicStatus;
    }

    /**
     * User's first name
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * User's first name
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * User's last name
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * User's last name
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * User's profile picture url
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * User's profile picture url
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * User's private status message
     *
     * @return
     * The privateStatus
     */
    public String getPrivateStatus() {
        return privateStatus;
    }

    /**
     * User's private status message
     *
     * @param privateStatus
     * The private_status
     */
    public void setPrivateStatus(String privateStatus) {
        this.privateStatus = privateStatus;
    }

    /**
     * User's birth date
     *
     * @return
     * The birthDate
     */
    public String getBirthDate() {
        return birthDate;
    }

    /**
     * User's birth date
     *
     * @param birthDate
     * The birth_date
     */
    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * User's sex
     *
     * @return
     * The sex
     */
    public RES_GET_USER_PROFILE.Sex getSex() {
        return sex;
    }

    /**
     * User's sex
     *
     * @param sex
     * The sex
     */
    public void setSex(RES_GET_USER_PROFILE.Sex sex) {
        this.sex = sex;
    }

    /**
     * User's language list.
     *
     * @return
     * The languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * User's language list.
     *
     * @param languages
     * The languages
     */
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    /**
     * User's country 2 character ISO code in uppercase
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * User's country 2 character ISO code in uppercase
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * User's region name
     *
     * @return
     * The region
     */
    public String getRegion() {
        return region;
    }

    /**
     * User's region name
     *
     * @param region
     * The region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * User's city name
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     * User's city name
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * List of hive subscriptions
     *
     * @return
     * The hiveSubscriptions
     */
    public Set<HIVE_SUBSCRIPTION> getHiveSubscriptions() {
        return hiveSubscriptions;
    }

    /**
     * List of hive subscriptions
     *
     * @param hiveSubscriptions
     * The hive_subscriptions
     */
    public void setHiveSubscriptions(Set<HIVE_SUBSCRIPTION> hiveSubscriptions) {
        this.hiveSubscriptions = hiveSubscriptions;
    }

    /**
     * Flag to indicate if age should be shown in public profile
     *
     * @return
     * The publicShowAge
     */
    public boolean isPublicShowAge() {
        return publicShowAge;
    }

    /**
     * Flag to indicate if age should be shown in public profile
     *
     * @param publicShowAge
     * The public_show_age
     */
    public void setPublicShowAge(boolean publicShowAge) {
        this.publicShowAge = publicShowAge;
    }

    /**
     * Flag to indicate if age should be shown in private profile
     *
     * @return
     * The privateShowAge
     */
    public boolean isPrivateShowAge() {
        return privateShowAge;
    }

    /**
     * Flag to indicate if age should be shown in private profile
     *
     * @param privateShowAge
     * The private_show_age
     */
    public void setPrivateShowAge(boolean privateShowAge) {
        this.privateShowAge = privateShowAge;
    }

    /**
     * Flag to indicate if sex should be shown in public profile
     *
     * @return
     * The publicShowSex
     */
    public boolean isPublicShowSex() {
        return publicShowSex;
    }

    /**
     * Flag to indicate if sex should be shown in public profile
     *
     * @param publicShowSex
     * The public_show_sex
     */
    public void setPublicShowSex(boolean publicShowSex) {
        this.publicShowSex = publicShowSex;
    }

    /**
     * Flag to indicate if location should be shown in public profile
     *
     * @return
     * The publicShowLocation
     */
    public boolean isPublicShowLocation() {
        return publicShowLocation;
    }

    /**
     * Flag to indicate if location should be shown in public profile
     *
     * @param publicShowLocation
     * The public_show_location
     */
    public void setPublicShowLocation(boolean publicShowLocation) {
        this.publicShowLocation = publicShowLocation;
    }

    /**
     * Flag to indicate if location should be shown in private profile
     *
     * @return
     * The privateShowLocation
     */
    public boolean isPrivateShowLocation() {
        return privateShowLocation;
    }

    /**
     * Flag to indicate if location should be shown in private profile
     *
     * @param privateShowLocation
     * The private_show_location
     */
    public void setPrivateShowLocation(boolean privateShowLocation) {
        this.privateShowLocation = privateShowLocation;
    }

    /**
     * User's location coordinates; first latitude then longitude separated with space. example: 42.23282 -8.72264
     *
     * @return
     * The coordinates
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * User's location coordinates; first latitude then longitude separated with space. example: 42.23282 -8.72264
     *
     * @param coordinates
     * The coordinates
     */
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(user).append(publicName).append(avatar).append(personalColor).append(publicStatus).append(firstName).append(lastName).append(picture).append(privateStatus).append(birthDate).append(sex).append(languages).append(country).append(region).append(city).append(hiveSubscriptions).append(publicShowAge).append(privateShowAge).append(publicShowSex).append(publicShowLocation).append(privateShowLocation).append(coordinates).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_GET_USER_PROFILE) == false) {
            return false;
        }
        RES_GET_USER_PROFILE rhs = ((RES_GET_USER_PROFILE) other);
        return new EqualsBuilder().append(user, rhs.user).append(publicName, rhs.publicName).append(avatar, rhs.avatar).append(personalColor, rhs.personalColor).append(publicStatus, rhs.publicStatus).append(firstName, rhs.firstName).append(lastName, rhs.lastName).append(picture, rhs.picture).append(privateStatus, rhs.privateStatus).append(birthDate, rhs.birthDate).append(sex, rhs.sex).append(languages, rhs.languages).append(country, rhs.country).append(region, rhs.region).append(city, rhs.city).append(hiveSubscriptions, rhs.hiveSubscriptions).append(publicShowAge, rhs.publicShowAge).append(privateShowAge, rhs.privateShowAge).append(publicShowSex, rhs.publicShowSex).append(publicShowLocation, rhs.publicShowLocation).append(privateShowLocation, rhs.privateShowLocation).append(coordinates, rhs.coordinates).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Sex {

        @SerializedName("male")
        MALE("male"),
        @SerializedName("female")
        FEMALE("female");
        private final String value;
        private final static Map<String, RES_GET_USER_PROFILE.Sex> CONSTANTS = new HashMap<String, RES_GET_USER_PROFILE.Sex>();

        static {
            for (RES_GET_USER_PROFILE.Sex c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Sex(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static RES_GET_USER_PROFILE.Sex fromValue(String value) {
            RES_GET_USER_PROFILE.Sex constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
