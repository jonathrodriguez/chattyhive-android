package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsGetCountryRegions
 * <p>
 * URL params for the Get Country Regions method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_GET_COUNTRY_REGIONS {

    /**
     * PATH 1. Country code. ISO 3166-1 alpha 2
     *
     */
    @SerializedName("country_code")
    @Expose
    private String country_code;

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

    public URL_GET_COUNTRY_REGIONS withCountry_code(String country_code) {
        this.country_code = country_code;
        return this;
    }

}
