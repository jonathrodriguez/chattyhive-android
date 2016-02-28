
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
 * User's profile.
 *
 */
@Generated("org.jsonschema2pojo")
public class PROFILE {

    /**
     * User's first name
     * (Required)
     *
     */
    @SerializedName("first_name")
    @Expose
    private String firstName;
    /**
     * User's last name
     * (Required)
     *
     */
    @SerializedName("last_name")
    @Expose
    private String lastName;
    /**
     * URL to AWS profile picture - Optional - the name of the file must be file.xxx
     *
     */
    @SerializedName("picture")
    @Expose
    private String picture;
    /**
     * User's birth date
     * (Required)
     *
     */
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    /**
     * User's language list.
     *
     */
    @SerializedName("languages")
    @Expose
    private Set<String> languages = new LinkedHashSet<String>();
    /**
     * User's public_name
     * (Required)
     *
     */
    @SerializedName("public_name")
    @Expose
    private String publicName;
    /**
     * User's sex
     *
     */
    @SerializedName("sex")
    @Expose
    private PROFILE.Sex sex;
    /**
     * User's city server code
     *
     */
    @SerializedName("city")
    @Expose
    private String city;
    /**
     * User's region server code
     *
     */
    @SerializedName("region")
    @Expose
    private String region;
    /**
     * User's country 2 character ISO code in uppercase
     * (Required)
     *
     */
    @SerializedName("country")
    @Expose
    private String country;
    /**
     * URL to AWS avatar picture - Optional - the name of the file must be file.xxx
     *
     */
    @SerializedName("avatar")
    @Expose
    private String avatar;
    /**
     * Flag to indicate if age should be shown in private profile
     * (Required)
     *
     */
    @SerializedName("private_show_age")
    @Expose
    private boolean privateShowAge;
    /**
     * Flag to indicate if age should be shown in public profile
     * (Required)
     *
     */
    @SerializedName("public_show_age")
    @Expose
    private boolean publicShowAge;
    /**
     * Flag to indicate if sex should be shown in public profile
     * (Required)
     *
     */
    @SerializedName("public_show_sex")
    @Expose
    private boolean publicShowSex;
    /**
     * Flag to indicate if location should be shown in public profile
     * (Required)
     *
     */
    @SerializedName("public_show_location")
    @Expose
    private boolean publicShowLocation;

    /**
     * User's first name
     * (Required)
     *
     * @return
     * The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * User's first name
     * (Required)
     *
     * @param firstName
     * The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * User's last name
     * (Required)
     *
     * @return
     * The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * User's last name
     * (Required)
     *
     * @param lastName
     * The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * URL to AWS profile picture - Optional - the name of the file must be file.xxx
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * URL to AWS profile picture - Optional - the name of the file must be file.xxx
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * User's birth date
     * (Required)
     *
     * @return
     * The birthdate
     */
    public String getBirthdate() {
        return birthdate;
    }

    /**
     * User's birth date
     * (Required)
     *
     * @param birthdate
     * The birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
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
     * User's public_name
     * (Required)
     *
     * @return
     * The publicName
     */
    public String getPublicName() {
        return publicName;
    }

    /**
     * User's public_name
     * (Required)
     *
     * @param publicName
     * The public_name
     */
    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    /**
     * User's sex
     *
     * @return
     * The sex
     */
    public PROFILE.Sex getSex() {
        return sex;
    }

    /**
     * User's sex
     *
     * @param sex
     * The sex
     */
    public void setSex(PROFILE.Sex sex) {
        this.sex = sex;
    }

    /**
     * User's city server code
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     * User's city server code
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * User's region server code
     *
     * @return
     * The region
     */
    public String getRegion() {
        return region;
    }

    /**
     * User's region server code
     *
     * @param region
     * The region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * User's country 2 character ISO code in uppercase
     * (Required)
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * User's country 2 character ISO code in uppercase
     * (Required)
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * URL to AWS avatar picture - Optional - the name of the file must be file.xxx
     *
     * @return
     * The avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * URL to AWS avatar picture - Optional - the name of the file must be file.xxx
     *
     * @param avatar
     * The avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Flag to indicate if age should be shown in private profile
     * (Required)
     *
     * @return
     * The privateShowAge
     */
    public boolean isPrivateShowAge() {
        return privateShowAge;
    }

    /**
     * Flag to indicate if age should be shown in private profile
     * (Required)
     *
     * @param privateShowAge
     * The private_show_age
     */
    public void setPrivateShowAge(boolean privateShowAge) {
        this.privateShowAge = privateShowAge;
    }

    /**
     * Flag to indicate if age should be shown in public profile
     * (Required)
     *
     * @return
     * The publicShowAge
     */
    public boolean isPublicShowAge() {
        return publicShowAge;
    }

    /**
     * Flag to indicate if age should be shown in public profile
     * (Required)
     *
     * @param publicShowAge
     * The public_show_age
     */
    public void setPublicShowAge(boolean publicShowAge) {
        this.publicShowAge = publicShowAge;
    }

    /**
     * Flag to indicate if sex should be shown in public profile
     * (Required)
     *
     * @return
     * The publicShowSex
     */
    public boolean isPublicShowSex() {
        return publicShowSex;
    }

    /**
     * Flag to indicate if sex should be shown in public profile
     * (Required)
     *
     * @param publicShowSex
     * The public_show_sex
     */
    public void setPublicShowSex(boolean publicShowSex) {
        this.publicShowSex = publicShowSex;
    }

    /**
     * Flag to indicate if location should be shown in public profile
     * (Required)
     *
     * @return
     * The publicShowLocation
     */
    public boolean isPublicShowLocation() {
        return publicShowLocation;
    }

    /**
     * Flag to indicate if location should be shown in public profile
     * (Required)
     *
     * @param publicShowLocation
     * The public_show_location
     */
    public void setPublicShowLocation(boolean publicShowLocation) {
        this.publicShowLocation = publicShowLocation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(firstName).append(lastName).append(picture).append(birthdate).append(languages).append(publicName).append(sex).append(city).append(region).append(country).append(avatar).append(privateShowAge).append(publicShowAge).append(publicShowSex).append(publicShowLocation).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof PROFILE) == false) {
            return false;
        }
        PROFILE rhs = ((PROFILE) other);
        return new EqualsBuilder().append(firstName, rhs.firstName).append(lastName, rhs.lastName).append(picture, rhs.picture).append(birthdate, rhs.birthdate).append(languages, rhs.languages).append(publicName, rhs.publicName).append(sex, rhs.sex).append(city, rhs.city).append(region, rhs.region).append(country, rhs.country).append(avatar, rhs.avatar).append(privateShowAge, rhs.privateShowAge).append(publicShowAge, rhs.publicShowAge).append(publicShowSex, rhs.publicShowSex).append(publicShowLocation, rhs.publicShowLocation).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Sex {

        @SerializedName("male")
        MALE("male"),
        @SerializedName("female")
        FEMALE("female");
        private final String value;
        private final static Map<String, PROFILE.Sex> CONSTANTS = new HashMap<String, PROFILE.Sex>();

        static {
            for (PROFILE.Sex c: values()) {
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

        public static PROFILE.Sex fromValue(String value) {
            PROFILE.Sex constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}