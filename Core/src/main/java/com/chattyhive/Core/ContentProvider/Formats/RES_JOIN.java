        package com.chattyhive.Core.ContentProvider.Formats;

        import java.util.HashMap;
        import java.util.LinkedHashSet;
        import java.util.Map;
        import java.util.Set;
        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;


/**
 * ResponseJoin
 * <p>
 * Response body for the Join method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_JOIN {

    /**
     * Hive name
     *
     */
    @SerializedName("name")
    @Expose
    private String name;
    /**
     * Hive identifier.
     *
     */
    @SerializedName("slug")
    @Expose
    private String slug;
    /**
     * Hive description
     *
     */
    @SerializedName("description")
    @Expose
    private String description;
    /**
     * Hive creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("creation_date")
    @Expose
    private String creation_date;
    /**
     * Hive manually set priority. Initially used for recommended hives.
     *
     */
    @SerializedName("priority")
    @Expose
    private Integer priority;
    /**
     * Hive picture url.
     *
     */
    @SerializedName("picture")
    @Expose
    private String picture;
    /**
     * Hive type. May be Community or Hive.
     *
     */
    @SerializedName("type")
    @Expose
    private HIVE.Type type;
    /**
     * Category code. Example: 09.01
     *
     */
    @SerializedName("category")
    @Expose
    private String category;
    /**
     * Public chats language list.
     *
     */
    @SerializedName("languages")
    @Expose
    private Set<String> languages = new LinkedHashSet<String>();
    /**
     * public_name of the creator user
     *
     */
    @SerializedName("creator")
    @Expose
    private String creator;
    /**
     * Hive tag list.
     *
     */
    @SerializedName("tags")
    @Expose
    private Set<String> tags = new LinkedHashSet<String>();
    /**
     * Number of subscribed users
     *
     */
    @SerializedName("subscribed_users_count")
    @Expose
    private Integer subscribed_users_count;
    /**
     * Hive public chat
     *
     */
    @SerializedName("public_chat")
    @Expose
    private PUBLIC_CHAT public_chat;
    /**
     * Community public chats list.
     *
     */
    @SerializedName("community_public_chats")
    @Expose
    private Set<COMMUNITY_PUBLIC_CHAT> community_public_chats = new LinkedHashSet<COMMUNITY_PUBLIC_CHAT>();
    /**
     * Community extension data. Admins and owner
     *
     */
    @SerializedName("community")
    @Expose
    private COMMUNITY community;

    /**
     * Hive name
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     * Hive name
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Hive identifier.
     *
     * @return
     * The slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Hive identifier.
     *
     * @param slug
     * The slug
     */
    public void setSlug(String slug) {
        this.slug = slug;
    }

    /**
     * Hive description
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Hive description
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Hive creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The creation_date
     */
    public String getCreation_date() {
        return creation_date;
    }

    /**
     * Hive creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param creation_date
     * The creation_date
     */
    public void setCreation_date(String creation_date) {
        this.creation_date = creation_date;
    }

    /**
     * Hive manually set priority. Initially used for recommended hives.
     *
     * @return
     * The priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Hive manually set priority. Initially used for recommended hives.
     *
     * @param priority
     * The priority
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Hive picture url.
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * Hive picture url.
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * Hive type. May be Community or Hive.
     *
     * @return
     * The type
     */
    public HIVE.Type getType() {
        return type;
    }

    /**
     * Hive type. May be Community or Hive.
     *
     * @param type
     * The type
     */
    public void setType(HIVE.Type type) {
        this.type = type;
    }

    /**
     * Category code. Example: 09.01
     *
     * @return
     * The category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Category code. Example: 09.01
     *
     * @param category
     * The category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Public chats language list.
     *
     * @return
     * The languages
     */
    public Set<String> getLanguages() {
        return languages;
    }

    /**
     * Public chats language list.
     *
     * @param languages
     * The languages
     */
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    /**
     * public_name of the creator user
     *
     * @return
     * The creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * public_name of the creator user
     *
     * @param creator
     * The creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * Hive tag list.
     *
     * @return
     * The tags
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Hive tag list.
     *
     * @param tags
     * The tags
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    /**
     * Number of subscribed users
     *
     * @return
     * The subscribed_users_count
     */
    public Integer getSubscribed_users_count() {
        return subscribed_users_count;
    }

    /**
     * Number of subscribed users
     *
     * @param subscribed_users_count
     * The subscribed_users_count
     */
    public void setSubscribed_users_count(Integer subscribed_users_count) {
        this.subscribed_users_count = subscribed_users_count;
    }

    /**
     * Hive public chat
     *
     * @return
     * The public_chat
     */
    public PUBLIC_CHAT getPublic_chat() {
        return public_chat;
    }

    /**
     * Hive public chat
     *
     * @param public_chat
     * The public_chat
     */
    public void setPublic_chat(PUBLIC_CHAT public_chat) {
        this.public_chat = public_chat;
    }

    /**
     * Community public chats list.
     *
     * @return
     * The community_public_chats
     */
    public Set<COMMUNITY_PUBLIC_CHAT> getCommunity_public_chats() {
        return community_public_chats;
    }

    /**
     * Community public chats list.
     *
     * @param community_public_chats
     * The community_public_chats
     */
    public void setCommunity_public_chats(Set<COMMUNITY_PUBLIC_CHAT> community_public_chats) {
        this.community_public_chats = community_public_chats;
    }

    /**
     * Community extension data. Admins and owner
     *
     * @return
     * The community
     */
    public COMMUNITY getCommunity() {
        return community;
    }

    /**
     * Community extension data. Admins and owner
     *
     * @param community
     * The community
     */
    public void setCommunity(COMMUNITY community) {
        this.community = community;
    }
}
