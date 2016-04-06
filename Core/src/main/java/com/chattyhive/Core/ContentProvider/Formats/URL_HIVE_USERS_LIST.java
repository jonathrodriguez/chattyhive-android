

package com.chattyhive.Core.ContentProvider.Formats;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * ParamsHiveUsersList
 * <p>
 * URL params for the Hive Users List method.
 *
 */
@Generated("org.jsonschema2pojo")
public class URL_HIVE_USERS_LIST {

    /**
     * PATH 1. Hive slug identifier.
     *
     */
    @SerializedName("hive_slug")
    @Expose
    private String hive_slug;
    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     */
    @SerializedName("sort")
    @Expose
    private URL_HIVE_USERS_LIST.Sort sort;
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
     * PATH 1. Hive slug identifier.
     *
     * @return
     * The hive_slug
     */
    public String getHive_slug() {
        return hive_slug;
    }

    /**
     * PATH 1. Hive slug identifier.
     *
     * @param hive_slug
     * The hive_slug
     */
    public void setHive_slug(String hive_slug) {
        this.hive_slug = hive_slug;
    }

    public URL_HIVE_USERS_LIST withHive_slug(String hive_slug) {
        this.hive_slug = hive_slug;
        return this;
    }

    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     * @return
     * The sort
     */
    public URL_HIVE_USERS_LIST.Sort getSort() {
        return sort;
    }

    /**
     * PATH 1. Sort type. recent -> recently online. new -> new subscriptions.
     *
     * @param sort
     * The sort
     */
    public void setSort(URL_HIVE_USERS_LIST.Sort sort) {
        this.sort = sort;
    }

    public URL_HIVE_USERS_LIST withSort(URL_HIVE_USERS_LIST.Sort sort) {
        this.sort = sort;
        return this;
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

    public URL_HIVE_USERS_LIST withStart(String start) {
        this.start = start;
        return this;
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

    public URL_HIVE_USERS_LIST withEnd(String end) {
        this.end = end;
        return this;
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

    public URL_HIVE_USERS_LIST withElements(Integer elements) {
        this.elements = elements;
        return this;
    }

    @Generated("org.jsonschema2pojo")
    public enum Sort {

        @SerializedName("recommended")
        RECOMMENDED("recommended"),
        @SerializedName("near")
        NEAR("near"),
        @SerializedName("recent")
        RECENT("recent"),
        @SerializedName("new")
        NEW("new");
        private final String value;
        private final static Map<String, URL_HIVE_USERS_LIST.Sort> CONSTANTS = new HashMap<String, URL_HIVE_USERS_LIST.Sort>();

        static {
            for (URL_HIVE_USERS_LIST.Sort c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Sort(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public static URL_HIVE_USERS_LIST.Sort fromValue(String value) {
            URL_HIVE_USERS_LIST.Sort constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
