package com.chattyhive.Core.ContentProvider.Formats;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Partial public profile
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_HIVE_USERS_LIST {

    /**
     * User public name
     *
     */
    @SerializedName("public_name")
    @Expose
    private String public_name;
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
    private String personal_color;
    /**
     * User's public status message
     *
     */
    @SerializedName("public_status")
    @Expose
    private String public_status;
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
     * User public name
     *
     * @return
     * The public_name
     */
    public String getPublic_name() {
        return public_name;
    }

    /**
     * User public name
     *
     * @param public_name
     * The public_name
     */
    public void setPublic_name(String public_name) {
        this.public_name = public_name;
    }

    public RES_HIVE_USERS_LIST withPublic_name(String public_name) {
        this.public_name = public_name;
        return this;
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

    public RES_HIVE_USERS_LIST withAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    /**
     * User's personal color for public messages. In hex format RGB
     *
     * @return
     * The personal_color
     */
    public String getPersonal_color() {
        return personal_color;
    }

    /**
     * User's personal color for public messages. In hex format RGB
     *
     * @param personal_color
     * The personal_color
     */
    public void setPersonal_color(String personal_color) {
        this.personal_color = personal_color;
    }

    public RES_HIVE_USERS_LIST withPersonal_color(String personal_color) {
        this.personal_color = personal_color;
        return this;
    }

    /**
     * User's public status message
     *
     * @return
     * The public_status
     */
    public String getPublic_status() {
        return public_status;
    }

    /**
     * User's public status message
     *
     * @param public_status
     * The public_status
     */
    public void setPublic_status(String public_status) {
        this.public_status = public_status;
    }

    public RES_HIVE_USERS_LIST withPublic_status(String public_status) {
        this.public_status = public_status;
        return this;
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

    public RES_HIVE_USERS_LIST withLanguages(Set<String> languages) {
        this.languages = languages;
        return this;
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

    public RES_HIVE_USERS_LIST withCountry(String country) {
        this.country = country;
        return this;
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

    public RES_HIVE_USERS_LIST withRegion(String region) {
        this.region = region;
        return this;
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

    public RES_HIVE_USERS_LIST withCity(String city) {
        this.city = city;
        return this;
    }

}
