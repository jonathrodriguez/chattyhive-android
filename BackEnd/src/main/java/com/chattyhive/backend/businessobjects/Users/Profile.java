package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.google.gson.JsonElement;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Jonathan on 08/07/2014.
 */
public abstract class Profile {

    String userID;

    String imageURL;

    String statusMessage;

    String location;
    String sex;
    Date birthdate;
    ArrayList<String> language;

    ProfileLevel loadedProfileLevel;

    Image smallProfileImage;
    Image mediumProfileImage;
    Image largeProfileImage;
    Image xlargeProfileImage;
    Image fileProfileImage;

    public enum ImageSize { small, medium, large, xlarge, file }

    public Image getProfileImage(ImageSize imageSize) {
        switch (imageSize) {
            case small:
                if (smallProfileImage != null)
                    return smallProfileImage;
                break;
            case medium:
                if (mediumProfileImage != null)
                    return mediumProfileImage;
                break;
            case large:
                if (largeProfileImage != null)
                    return largeProfileImage;
                break;
            case xlarge:
                if (xlargeProfileImage != null)
                    return xlargeProfileImage;
                break;
            case file:
                if (fileProfileImage != null)
                    return fileProfileImage;
                break;
        }
        return null;
    }

    public String getID() {
        return this.userID;
    }
    public void setID(String userID) {
        this.userID = userID;
    }

    public abstract String getShowingName();

    public String getImageURL() {
        return this.imageURL;
    }
    public void setImageURL(String value) {
        this.imageURL = value;
    }

    public String getStatusMessage() {
        return this.statusMessage;
    }
    public void setStatusMessage(String value) {
        this.statusMessage = value;
    }

    public String getLocation() {
        return this.location;
    }
    public void setLocation(String value) {
        this.location = value;
    }

    public String getSex() {
        return this.sex;
    }
    public void setSex(String value){
        if (value.equalsIgnoreCase("MALE") || value.equalsIgnoreCase("FEMALE"))
            this.sex = value;
        else
            throw new IllegalArgumentException("Invalid sex value.");
    }

    public Date getBirthdate() {
        return this.birthdate;
    }
    public void setBirthdate(Date value) {
        this.birthdate = value;
    }

    public ProfileLevel getLoadedProfileLevel() {
        return this.loadedProfileLevel;
    }
    public abstract void unloadProfile(ProfileLevel profileLevel);

    /*****************************************************************/
    /*                          LANGUAGES                            */
    /*****************************************************************/
    public Boolean hasLanguage(String value) {
        return ((this.language != null) && (this.language.contains(value)));
    }
    public void removeLanguage (String value) {
        if (this.language != null) {
            this.language.remove(value);
            if (this.language.isEmpty())
                this.language = null;
        }
    }
    public void addLanguage(String value) {
        if (this.language == null)
            this.language = new ArrayList<String>();

        if (!this.language.contains(value))
            this.language.add(value);
    }

    public ArrayList<String> getLanguages() {
        return this.language;
    }
    public ArrayList<String> getLanguages(Collection<String> sortReference) {
        /*TODO: Sort languages. If first language from reference matches any language, this goes first.
        *       Then, if first language from languages matches any reference, this goes next.
        *       Then, if english is in languages and reference, this goes next.
        *       Next are matching languages from reference and languages alphabetically.
        *       Finally, all other languages, alphabetically.
        */
        return this.language;
    }
    public void setLanguages(Collection<String> value) {
        this.language = (value != null)?new ArrayList<String>(value):null;
    }
    /*****************************************************************/

    public abstract Format toFormat(Format format);
    public abstract Boolean fromFormat(Format format);

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }
    public void fromJson(JsonElement jsonElement) {
        Format[] formats = Format.getFormat(jsonElement);
        for (Format format : formats)
            if (this.fromFormat(format)) return;

        throw  new IllegalArgumentException("Expected PUBLIC_PROFILE or PRIVATE_PROFILE formats.");
    }
}
