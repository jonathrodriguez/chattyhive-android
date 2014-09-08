package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;

/**
 * Created by Jonathan on 08/07/2014.
 */
public class PublicProfile extends Profile {

    String publicName;

    Boolean showSex;
    Boolean showAge;
    Boolean showLocation;

    public String getPublicName() {
        return this.publicName;
    }
    public void setPublicName(String value) {
        this.publicName = value;
    }

    public Boolean getShowSex() {
        return (this.showSex!=null)?this.showSex:false;
    }
    public void setShowSex(Boolean value) {
        this.showSex = value;
    }

    public Boolean getShowAge() {
        return (this.showAge!=null)?this.showAge:false;
    }
    public void setShowAge(Boolean value) {
        this.showAge = value;
    }

    public Boolean getShowLocation() {
        return (this.showLocation!=null)?this.showLocation:false;
    }
    public void setShowLocation(Boolean value) {
        this.showLocation = value;
    }

    public PublicProfile(Format format) {
        if (!this.fromFormat(format)) {
            throw new IllegalArgumentException("Format not valid.");
        }
    }

    @Override
    public String getID() {
        return this.publicName;
    }

    @Override
    public String getShowingName() {
        return this.publicName;
    }

    @Override
    public Format toFormat(Format format) {
        if (format instanceof PUBLIC_PROFILE) {
            ((PUBLIC_PROFILE) format).PUBLIC_NAME = this.publicName;
            ((PUBLIC_PROFILE) format).IMAGE_URL = this.imageURL;
            ((PUBLIC_PROFILE) format).BIRTHDATE = this.birthdate;
            ((PUBLIC_PROFILE) format).LOCATION = this.location;
            ((PUBLIC_PROFILE) format).LANGUAGE = this.language;
            ((PUBLIC_PROFILE) format).PUBLIC_SHOW_AGE = this.showAge;
            ((PUBLIC_PROFILE) format).PUBLIC_SHOW_LOCATION = this.showLocation;
            ((PUBLIC_PROFILE) format).PUBLIC_SHOW_SEX = this.showSex;
            ((PUBLIC_PROFILE) format).SEX = this.sex;
            ((PUBLIC_PROFILE) format).USER_COLOR = this.color;
            return format;
        }

        throw new IllegalArgumentException("Expected PUBLIC_PROFILE format");
    }

    @Override
    public Boolean fromFormat(Format format) {
        if (format instanceof PUBLIC_PROFILE) {
            this.publicName = ((PUBLIC_PROFILE) format).PUBLIC_NAME;
            this.imageURL = ((PUBLIC_PROFILE) format).IMAGE_URL;
            this.birthdate = ((PUBLIC_PROFILE) format).BIRTHDATE;
            this.language = ((PUBLIC_PROFILE) format).LANGUAGE;
            this.location = ((PUBLIC_PROFILE) format).LOCATION;
            this.showSex = ((PUBLIC_PROFILE) format).PUBLIC_SHOW_SEX;
            this.showAge = ((PUBLIC_PROFILE) format).PUBLIC_SHOW_AGE;
            this.showLocation = ((PUBLIC_PROFILE) format).PUBLIC_SHOW_LOCATION;
            this.sex = ((PUBLIC_PROFILE) format).SEX;
            this.color = ((PUBLIC_PROFILE) format).USER_COLOR;
            return true;
        }

        return false;
    }
}
