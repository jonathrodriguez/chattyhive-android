package com.chattyhive.backend.util.formatters;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

/**
 * Created by Jonathan on 15/12/13.
 * This is a static class to perform timestamp formatting.
 * There are two static methods. One to get a Date object from string representation and another to
 * get string representation from a Date object.
 */
public final class TimestampFormatter {
    /**
     * Collection of string formats for Timestamps. Only the first representation may be used.
     * The other representations are set to provide compatibility with server's first version.
     */
    private static final String[] _formats = {"yyyy-MM-dd'T'HH:mm:ss.sss",
                                              "yyyy-MM-dd'T'HH:mm:ss.sss'Z'",
                                              "HH:mm:ss z",
                                              "EEE MMM dd HH:mm:ss z yyyy",
                                              "EEE MMM dd HH:mm:ss yyyy 'GMT'Z",
                                              "HH:mm:ss 'GMT'Z"};

    /**
     * Converts a Date object to it's string representation, referred to UTC, according to the
     * format: "yyyy-MM-dd'T'HH:mm:ss.sss"
     * @param timestamp Date object representing the timestamp to be converted.
     * @return A string containing the representation of the timestamp.
     */
    public static final String toString(Date timestamp) {
        SimpleDateFormat simpleDateFormat;
        if ((_formats != null) && (_formats.length > 0) && (_formats[0] != null) && (!_formats[0].isEmpty())) {
            simpleDateFormat = new SimpleDateFormat(_formats[0]);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
            return simpleDateFormat.format(timestamp);
        } else {
            return timestamp.toString();
        }
    }

    /**
     * Parses a string timestamp representation into a Date object.
     * @param timestamp The string representation of the timestamp.
     * @return A date which corresponds to the timestamp.
     */
    public static final Date toDate(String timestamp) {
        SimpleDateFormat simpleDateFormat;
        Iterator<String> formatIterator = Arrays.asList(_formats).iterator();
        while (formatIterator.hasNext()) {
            String format = formatIterator.next();
            simpleDateFormat = new SimpleDateFormat(format);
            try {
                return simpleDateFormat.parse(timestamp);
            } catch (ParseException e) { }
        }
        return (new Date());
    }

}
