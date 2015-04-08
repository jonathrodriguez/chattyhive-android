package com.chattyhive.Core.Util.Formatters;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
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
    private static final String[] _formats = {"yyyy-MM-dd'T'HH:mm:ss.SSS",
                                              "HH:mm" ,
                                              "dd/MM/yyyy HH:mm" ,
                                              "HH:mm:ss 'GMT'Z" ,
                                              "HH:mm:ss zzzz" ,
                                              "HH:mm:ss Z" ,
                                              "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                                              "yyyy-MM-dd HH:mm:ss.SSS"};
    private static final String timeZoneID = "Europe/Madrid";
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
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
            return simpleDateFormat.format(timestamp);
        } else {
            return timestamp.toString();
        }
    }

    public static final String toLocaleString(Date timestamp) {
        SimpleDateFormat simpleDateFormat;
        Boolean recent = true;//(((new Date()).getTime() - timestamp.getTime())/(60*60*1000) <= 18);
        if ((_formats != null) && (_formats.length > 1) && (_formats[1] != null) && (!_formats[1].isEmpty()) && (recent)) {
            simpleDateFormat = new SimpleDateFormat(_formats[1]);
            return simpleDateFormat.format(timestamp);
        }
        else if ((_formats != null) && (_formats.length > 2) && (_formats[2] != null) && (!_formats[2].isEmpty()) && (!recent)) {
            simpleDateFormat = new SimpleDateFormat(_formats[2]);
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
        for (String format : _formats) {
            simpleDateFormat = new SimpleDateFormat(format);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
            try {
                return simpleDateFormat.parse(timestamp);
            } catch (ParseException e) {
            }
        }
        return (new Date());
    }

    public static final String toDbString(Date timestamp) {
        SimpleDateFormat simpleDateFormat;
        if ((_formats != null) && (_formats.length > 7) && (_formats[7] != null) && (!_formats[7].isEmpty())) {
            simpleDateFormat = new SimpleDateFormat(_formats[7]);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZoneID));
            return simpleDateFormat.format(timestamp);
        } else {
            return timestamp.toString();
        }
    }
}
