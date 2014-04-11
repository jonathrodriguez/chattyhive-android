package com.chattyhive.backend.util.formatters;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jonathan on 11/04/14.
 * This is a static class to perform date formatting.
 * There are two static methods. One to get a Date object from string representation and another to
 * get string representation from a Date object.
 */
public final class DateFormatter {
    /**
     * String format for Dates.
     */
    private static final String format = "yyyy-MM-dd";

    /**
     * Converts a Date object to it's string representation, referred to UTC, according to the
     * format: "yyyy-MM-dd"
     * @param date Date object representing the date to be converted.
     * @return A string containing the representation of the date.
     */
    public static final String toString(Date date) {
        SimpleDateFormat simpleDateFormat;
        if ((format != null) && (!format.isEmpty())) {
            simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
            return simpleDateFormat.format(date);
        } else {
            return date.toString();
        }
    }

    /**
     * Parses a string date representation into a Date object.
     * @param date The String representation of the date.
     * @return A Date which corresponds to the date.
     */
    public static final Date toDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return simpleDateFormat.parse(date);
        } catch (ParseException e) { }

        return (new Date());
    }

}

