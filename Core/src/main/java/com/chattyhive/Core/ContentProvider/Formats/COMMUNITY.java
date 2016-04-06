package com.chattyhive.Core.ContentProvider.Formats;

        import java.util.LinkedHashSet;
        import java.util.Set;
        import javax.annotation.Generated;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;


/**
 * Community extension data. Admins and owner
 *
 */
@Generated("org.jsonschema2pojo")
public class COMMUNITY {

    /**
     * Community administrators list
     *
     */
    @SerializedName("admins")
    @Expose
    private Set<String> admins = new LinkedHashSet<String>();
    /**
     * Actual community owner.
     *
     */
    @SerializedName("owner")
    @Expose
    private String owner;

    /**
     * Community administrators list
     *
     * @return
     * The admins
     */
    public Set<String> getAdmins() {
        return admins;
    }

    /**
     * Community administrators list
     *
     * @param admins
     * The admins
     */
    public void setAdmins(Set<String> admins) {
        this.admins = admins;
    }

    /**
     * Actual community owner.
     *
     * @return
     * The owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Actual community owner.
     *
     * @param owner
     * The owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

}