package com.chattyhive.backend.businessobjects.Users;

import com.chattyhive.backend.contentprovider.formats.BASIC_PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;

/**
 * Created by Jonathan on 08/07/2014.
 */
public class PrivateProfile extends Profile {
    String firstName;
    String lastName;
    Boolean showAge;

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
        return (this.showAge!=null)?this.showAge:false;
    }
    public void setShowAge(Boolean value) {
        this.showAge = value;
    }


    public PrivateProfile(Format format) {
        this();
        if (!this.fromFormat(format)) {
            throw new IllegalArgumentException("Format not valid.");
        }
    }

    public PrivateProfile() {
        this.loadedProfileLevel = ProfileLevel.None;
    }

    @Override
    public void unloadProfile(ProfileLevel profileLevel) {
        if (profileLevel.ordinal() >= this.loadedProfileLevel.ordinal()) return;

        if (profileLevel.ordinal() <= ProfileLevel.Extended.ordinal()) {

        }

        if (profileLevel.ordinal() <= ProfileLevel.Basic.ordinal()) {
            this.showAge = null;
            this.language = null;
            this.location = null;
            this.birthdate = null;
            this.sex = null;
        }

        if (profileLevel.ordinal() <= ProfileLevel.None.ordinal()) {
            this.firstName = null;
            this.lastName = null;
            this.imageURL = null;
            this.statusMessage = null;
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
            ((PRIVATE_PROFILE) format).BIRTHDATE = this.birthdate;
            ((PRIVATE_PROFILE) format).LOCATION = this.location;
            ((PRIVATE_PROFILE) format).LANGUAGE = this.language;
            ((PRIVATE_PROFILE) format).PRIVATE_SHOW_AGE = this.showAge;
            ((PRIVATE_PROFILE) format).SEX = this.sex;
            return format;
        } else if (format instanceof BASIC_PRIVATE_PROFILE) {
            ((BASIC_PRIVATE_PROFILE) format).USER_ID = this.userID;
            ((BASIC_PRIVATE_PROFILE) format).FIRST_NAME = this.firstName;
            ((BASIC_PRIVATE_PROFILE) format).LAST_NAME = this.lastName;
            ((BASIC_PRIVATE_PROFILE) format).IMAGE_URL = this.imageURL;
            ((BASIC_PRIVATE_PROFILE) format).STATUS_MESSAGE = this.statusMessage;
            return format;
        }

        throw new IllegalArgumentException("Expected PRIVATE_PROFILE or BASIC_PRIVATE_PROFILE format");
    }

    @Override
    public Boolean fromFormat(Format format) {
        if (format instanceof PRIVATE_PROFILE) {
            this.userID = ((PRIVATE_PROFILE) format).USER_ID;
            this.birthdate = ((PRIVATE_PROFILE) format).BIRTHDATE;
            this.language = ((PRIVATE_PROFILE) format).LANGUAGE;
            this.location = ((PRIVATE_PROFILE) format).LOCATION;
            this.showAge = ((PRIVATE_PROFILE) format).PRIVATE_SHOW_AGE;
            this.sex = ((PRIVATE_PROFILE) format).SEX;
            if (this.loadedProfileLevel.ordinal() < ProfileLevel.Extended.ordinal())
                this.loadedProfileLevel = ProfileLevel.Extended;
            return true;
        } else if (format instanceof BASIC_PRIVATE_PROFILE) {
            this.userID = ((BASIC_PRIVATE_PROFILE) format).USER_ID;
            this.firstName = ((BASIC_PRIVATE_PROFILE) format).FIRST_NAME;
            this.lastName = ((BASIC_PRIVATE_PROFILE) format).LAST_NAME;
            this.imageURL = ((BASIC_PRIVATE_PROFILE) format).IMAGE_URL;
            this.statusMessage = ((BASIC_PRIVATE_PROFILE) format).STATUS_MESSAGE;
            if (this.loadedProfileLevel.ordinal() < ProfileLevel.Basic.ordinal())
                this.loadedProfileLevel = ProfileLevel.Basic;
            return true;
        }

        return false;
    }
}
