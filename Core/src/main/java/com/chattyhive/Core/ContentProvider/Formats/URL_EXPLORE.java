
package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsExplore
 * <p>
 * URL params for the Explore method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_EXPLORE {

    /**
     * PATH 1. Can be recommended, near, recent, communities, top, a category code or empty.
     *
     */
    @SerializedName("sort")
    @Expose
    private String sort;
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
    private Integer elements;
    /**
     * QUERY. Two character country code. For near search.
     *
     */
    @SerializedName("country")
    @Expose
    private String country;
    /**
     * QUERY. Region name. For near search. Requires country.
     *
     */
    @SerializedName("region")
    @Expose
    private String region;
    /**
     * QUERY. City name. For near search. Requires region and country.
     *
     */
    @SerializedName("city")
    @Expose
    private String city;
    /**
     * QUERY. UTM Coordinates of the actual location. For near search.
     *
     */
    @SerializedName("coordinates")
    @Expose
    private String coordinates;
    /**
     * QUERY. String to search in the name of hives. If present no other query or path params are allowed, except pagination.
     *
     */
    @SerializedName("search_string")
    @Expose
    private String search_string;
    /**
     * QUERY. If true hives already subscribed are included. Default is false.
     *
     */
    @SerializedName("include_subscribed")
    @Expose
    private Boolean include_subscribed;
    /**
     * QUERY. Name of a tag. Can be specified multiple times, in that case hives must have at least one of the specified tags.
     *
     */
    @SerializedName("tags")
    @Expose
    private String tags;

    /**
     * PATH 1. Can be recommended, near, recent, communities, top, a category code or empty.
     *
     * @return
     * The sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * PATH 1. Can be recommended, near, recent, communities, top, a category code or empty.
     *
     * @param sort
     * The sort
     */
    public void setSort(String sort) {
        this.sort = sort;
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
    public Integer getElements() {
        return elements;
    }

    /**
     * QUERY. number of elements requested.
     *
     * @param elements
     * The elements
     */
    public void setElements(Integer elements) {
        this.elements = elements;
    }

    /**
     * QUERY. Two character country code. For near search.
     *
     * @return
     * The country
     */
    public String getCountry() {
        return country;
    }

    /**
     * QUERY. Two character country code. For near search.
     *
     * @param country
     * The country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * QUERY. Region name. For near search. Requires country.
     *
     * @return
     * The region
     */
    public String getRegion() {
        return region;
    }

    /**
     * QUERY. Region name. For near search. Requires country.
     *
     * @param region
     * The region
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * QUERY. City name. For near search. Requires region and country.
     *
     * @return
     * The city
     */
    public String getCity() {
        return city;
    }

    /**
     * QUERY. City name. For near search. Requires region and country.
     *
     * @param city
     * The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * QUERY. UTM Coordinates of the actual location. For near search.
     *
     * @return
     * The coordinates
     */
    public String getCoordinates() {
        return coordinates;
    }

    /**
     * QUERY. UTM Coordinates of the actual location. For near search.
     *
     * @param coordinates
     * The coordinates
     */
    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * QUERY. String to search in the name of hives. If present no other query or path params are allowed, except pagination.
     *
     * @return
     * The search_string
     */
    public String getSearch_string() {
        return search_string;
    }

    /**
     * QUERY. String to search in the name of hives. If present no other query or path params are allowed, except pagination.
     *
     * @param search_string
     * The search_string
     */
    public void setSearch_string(String search_string) {
        this.search_string = search_string;
    }

    /**
     * QUERY. If true hives already subscribed are included. Default is false.
     *
     * @return
     * The include_subscribed
     */
    public Boolean getInclude_subscribed() {
        return include_subscribed;
    }

    /**
     * QUERY. If true hives already subscribed are included. Default is false.
     *
     * @param include_subscribed
     * The include_subscribed
     */
    public void setInclude_subscribed(Boolean include_subscribed) {
        this.include_subscribed = include_subscribed;
    }

    /**
     * QUERY. Name of a tag. Can be specified multiple times, in that case hives must have at least one of the specified tags.
     *
     * @return
     * The tags
     */
    public String getTags() {
        return tags;
    }

    /**
     * QUERY. Name of a tag. Can be specified multiple times, in that case hives must have at least one of the specified tags.
     *
     * @param tags
     * The tags
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

}
