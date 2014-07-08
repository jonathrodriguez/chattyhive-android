package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jonathan on 08/07/2014.
 */
public class PrivateProfile extends Profile {
    String userID;
    String firstName;
    String lastName;
    Boolean showAge;

    public String getUserID() {
        return this.userID;
    }
    public void setUserID(String value) {
        this.userID = value;
    }

    public String getFirstName() {
        return this.firstName;
    }
    public void setFirstName(String value) {
        this.firstName = value;
    }

    public String getLastName() {
        return this.lastName;
    }
    public void setLastName(String value) {
        this.lastName = value;
    }

    public Boolean getShowAge() {
        return this.showAge;
    }
    public void setShowAge(Boolean value) {
        this.showAge = value;
    }


    public PrivateProfile(Format format) {
        if (!this.fromFormat(format)) {
            throw new IllegalArgumentException("Format not valid.");
        }
    }

    @Override
    public String getID() {
        return this.userID;
    }

    @Override
    public String getShowingName() {
        return String.format("%s %s",this.firstName,this.lastName);
    }

    @Override
    public Format toFormat(Format format) {
        if (format instanceof PRIVATE_PROFILE) {
            ((PRIVATE_PROFILE) format).USER_ID = this.userID;
            ((PRIVATE_PROFILE) format).FIRST_NAME = this.firstName;
            ((PRIVATE_PROFILE) format).LAST_NAME = this.lastName;
            ((PRIVATE_PROFILE) format).IMAGE_URL = this.imageURL;
            ((PRIVATE_PROFILE) format).BIRTHDATE = this.birthdate;
            ((PRIVATE_PROFILE) format).LOCATION = this.location;
            ((PRIVATE_PROFILE) format).LANGUAGE = this.language;
            ((PRIVATE_PROFILE) format).PRIVATE_SHOW_AGE = this.showAge;
            ((PRIVATE_PROFILE) format).SEX = this.sex;
            ((PRIVATE_PROFILE) format).USER_COLOR = this.color;
            return format;
        }

        throw new IllegalArgumentException("Expected PUBLIC_PROFILE format");
    }

    @Override
    public Boolean fromFormat(Format format) {
        if (format instanceof PRIVATE_PROFILE) {
            this.userID = ((PRIVATE_PROFILE) format).USER_ID;
            this.firstName = ((PRIVATE_PROFILE) format).FIRST_NAME;
            this.lastName = ((PRIVATE_PROFILE) format).LAST_NAME;
            this.imageURL = ((PRIVATE_PROFILE) format).IMAGE_URL;
            this.birthdate = ((PRIVATE_PROFILE) format).BIRTHDATE;
            this.language = ((PRIVATE_PROFILE) format).LANGUAGE;
            this.location = ((PRIVATE_PROFILE) format).LOCATION;
            this.showAge = ((PRIVATE_PROFILE) format).PRIVATE_SHOW_AGE;
            this.sex = ((PRIVATE_PROFILE) format).SEX;
            this.color = ((PRIVATE_PROFILE) format).USER_COLOR;
            return true;
        }

        return false;
    }
}
