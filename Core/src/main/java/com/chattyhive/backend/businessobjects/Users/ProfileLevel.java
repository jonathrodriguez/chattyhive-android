package com.chattyhive.backend.businessobjects.Users;

/**
 * Created by Jonathan on 29/09/2014.
 */
public enum ProfileLevel {
    None, //No info available
    Basic, //Only showing name, image url, status message and (if public) user color.
    Extended, //user info (genre, age, location and languages)
    Complete //user shared objects
}
