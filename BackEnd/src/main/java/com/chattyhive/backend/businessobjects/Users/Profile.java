package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by Jonathan on 08/07/2014.
 */
public abstract class Profile {
    String color;
    String imageURL;

    String location;
    String sex;
    Date birthdate;
    ArrayList<String> language;

    public abstract String getID();
    public abstract String getShowingName();

    public String getColor() {
        return this.color;
    }
    public void setColor(String value) {
        this.color = value;
    }

    public String getImageURL() {
        return this.imageURL;
    }
    public void setImageURL(String value) {
        this.imageURL = value;
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

    /*****************************************************************/
    /*                          LANGUAGES                            */
    /*****************************************************************/
    public Boolean hasLanguage(String value) {
        return ((this.language != null) && (this.language.contains(value)));
    }
    public void removeLanguage (String value) {
        if (this.language != null)
            this.language.remove(value);
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
        this.language = new ArrayList<String>(value);
    }
    /*****************************************************************/

    public abstract Format toFormat(Format format);
    public abstract Boolean fromFormat(Format format);

    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }
    public void fromJson(JsonElement jsonElement) {
        Format[] formats = Format.getFormat(jsonElement);
        Boolean parsed = false;
        for (Format format : formats)
            if (this.fromFormat(format)) return;

        throw  new IllegalArgumentException("Expected PUBLIC_PROFILE or PRIVATE_PROFILE formats.");
    }
}
