package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsGetRegionCities
 * <p>
 * URL params for the Get Country Regions method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_GET_REGION_CITIES {

    /**
     * PATH 1. Country code. ISO 3166-1 alpha 2
     *
     */
    @SerializedName("country_code")
    @Expose
    private String country_code;
    /**
     * PATH 2. Region name. as sent by server in the get regions method
     *
     */
    @SerializedName("region_name")
    @Expose
    private String region_name;

    /**
     * PATH 1. Country code. ISO 3166-1 alpha 2
     *
     * @return
     * The country_code
     */
    public String getCountry_code() {
        return country_code;
    }

    /**
     * PATH 1. Country code. ISO 3166-1 alpha 2
     *
     * @param country_code
     * The country_code
     */
    public void setCountry_code(String country_code) {
        this.country_code = country_code;
    }

    public URL_GET_REGION_CITIES withCountry_code(String country_code) {
        this.country_code = country_code;
        return this;
    }

    /**
     * PATH 2. Region name. as sent by server in the get regions method
     *
     * @return
     * The region_name
     */
    public String getRegion_name() {
        return region_name;
    }

    /**
     * PATH 2. Region name. as sent by server in the get regions method
     *
     * @param region_name
     * The region_name
     */
    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }

    public URL_GET_REGION_CITIES withRegion_name(String region_name) {
        this.region_name = region_name;
        return this;
    }

}
