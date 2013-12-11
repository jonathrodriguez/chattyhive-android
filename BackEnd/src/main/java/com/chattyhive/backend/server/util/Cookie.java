package com.chattyhive.backend.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jonathan on 27/11/13.
 */
public class Cookie {
    private String _name;
    private String _value;
    private Date _expiration;

    public String getName() {
        return this._name;
    }

    public String getValue() {
        return this._value;
    }

    public Boolean hasExpired() {
        return (this._expiration.before(new Date()));
    }

    public Cookie(String setString) {
        String[] splited = setString.split(";");
        this._name = splited[0].split("=")[0];
        this._value = splited[0].split("=")[1];
        try {
            this._expiration = (new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss 'GMT'")).parse(splited[1].split("=")[1]); //"E, d-M-y H:m:s z"
        } catch (ParseException e) {
            this._expiration = null;
        }
    }

    public String toSend() {
        String res = "";
        res = res.concat(this._name).concat("=").concat(this._value);
        return res;
    }
}
