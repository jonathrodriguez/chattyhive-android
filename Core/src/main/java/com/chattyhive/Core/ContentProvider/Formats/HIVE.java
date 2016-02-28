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
 * Hive object
 *
 */
@Generated("org.jsonschema2pojo")
public class HIVE {

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
     *
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
    private String creationDate;
    /**
     *
     *
     */
    @SerializedName("priority")
    @Expose
    private int priority;
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
    private int subscribedUsersCount;
    /**
     * Hive public chat
     *
     */
    @SerializedName("public_chat")
    @Expose
    private PUBLIC_CHAT publicChat;
    /**
     * Community public chats list.
     *
     */
    @SerializedName("community_public_chats")
    @Expose
    private Set<COMMUNITY_PUBLIC_CHAT> communityPublicChats = new LinkedHashSet<COMMUNITY_PUBLIC_CHAT>();

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
     *
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
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
     * The creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Hive creation date. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param creationDate
     * The creation_date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     *
     *
     * @return
     * The priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     *
     *
     * @param priority
     * The priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
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
     * The subscribedUsersCount
     */
    public int getSubscribedUsersCount() {
        return subscribedUsersCount;
    }

    /**
     * Number of subscribed users
     *
     * @param subscribedUsersCount
     * The subscribed_users_count
     */
    public void setSubscribedUsersCount(int subscribedUsersCount) {
        this.subscribedUsersCount = subscribedUsersCount;
    }

    /**
     * Hive public chat
     *
     * @return
     * The publicChat
     */
    public PUBLIC_CHAT getPublicChat() {
        return publicChat;
    }

    /**
     * Hive public chat
     *
     * @param publicChat
     * The public_chat
     */
    public void setPublicChat(PUBLIC_CHAT publicChat) {
        this.publicChat = publicChat;
    }

    /**
     * Community public chats list.
     *
     * @return
     * The communityPublicChats
     */
    public Set<COMMUNITY_PUBLIC_CHAT> getCommunityPublicChats() {
        return communityPublicChats;
    }

    /**
     * Community public chats list.
     *
     * @param communityPublicChats
     * The community_public_chats
     */
    public void setCommunityPublicChats(Set<COMMUNITY_PUBLIC_CHAT> communityPublicChats) {
        this.communityPublicChats = communityPublicChats;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).append(slug).append(description).append(creationDate).append(priority).append(type).append(category).append(languages).append(creator).append(tags).append(subscribedUsersCount).append(publicChat).append(communityPublicChats).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof HIVE) == false) {
            return false;
        }
        HIVE rhs = ((HIVE) other);
        return new EqualsBuilder().append(name, rhs.name).append(slug, rhs.slug).append(description, rhs.description).append(creationDate, rhs.creationDate).append(priority, rhs.priority).append(type, rhs.type).append(category, rhs.category).append(languages, rhs.languages).append(creator, rhs.creator).append(tags, rhs.tags).append(subscribedUsersCount, rhs.subscribedUsersCount).append(publicChat, rhs.publicChat).append(communityPublicChats, rhs.communityPublicChats).isEquals();
    }

    @Generated("org.jsonschema2pojo")
    public static enum Type {

        @SerializedName("Hive")
        HIVE("Hive"),
        @SerializedName("Community")
        COMMUNITY("Community");
        private final String value;
        private final static Map<String, com.chattyhive.Core.ContentProvider.Formats.HIVE.Type> CONSTANTS = new HashMap<String, com.chattyhive.Core.ContentProvider.Formats.HIVE.Type>();

        static {
            for (com.chattyhive.Core.ContentProvider.Formats.HIVE.Type c: values()) {
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

        public static com.chattyhive.Core.ContentProvider.Formats.HIVE.Type fromValue(String value) {
            com.chattyhive.Core.ContentProvider.Formats.HIVE.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}