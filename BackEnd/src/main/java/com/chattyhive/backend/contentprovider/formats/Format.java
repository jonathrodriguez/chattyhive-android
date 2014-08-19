package com.chattyhive.backend.contentprovider.formats;

import com.google.gson.JsonElement;
import com.chattyhive.backend.contentprovider.formats.USER_EMAIL;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.CHAT_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ID;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_INTERVAL;
import com.chattyhive.backend.contentprovider.formats.INTERVAL;
import com.chattyhive.backend.contentprovider.formats.YES_NO;
import com.chattyhive.backend.contentprovider.formats.CSRF_TOKEN;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PUBLIC_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PRIVATE_PROFILE;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_ID;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_LIST;
import com.chattyhive.backend.contentprovider.formats.CHAT_SYNC;
import com.chattyhive.backend.contentprovider.formats.CHAT_LIST;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/*
 * Automatically generated code by ChattyHive API Manager Code Generator on 18/08/2014.
 * Be careful to not modify this file since your changes will not be included in future
 * versions of this file.
 *
 * ChattyHive API Manager Code Generator was created by Jonathan on 25/06/2014.
*/
public abstract class Format {
    public abstract JsonElement toJSON();
    public abstract void fromJSON(JsonElement data);

    public static Format[] getFormat(JsonElement data) {
		 ArrayList<Format> result = new ArrayList<Format>();

        if (data.isJsonObject()) {
            Set<Map.Entry<String,JsonElement>> entries = data.getAsJsonObject().entrySet();
            for (Map.Entry<String,JsonElement> entry : entries) {
                Format f = null;
                if ((entry.getKey() == null) || (entry.getKey().isEmpty())) {
                    continue;
                } else if (entry.getKey().equalsIgnoreCase("USER_EMAIL")) {
                    f = new USER_EMAIL(data);
                } else if (entry.getKey().equalsIgnoreCase("LOGIN")) {
                    f = new LOGIN(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_ID")) {
                    f = new CHAT_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_ID")) {
                    f = new MESSAGE_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_INTERVAL")) {
                    f = new MESSAGE_INTERVAL(data);
                } else if (entry.getKey().equalsIgnoreCase("INTERVAL")) {
                    f = new INTERVAL(data);
                } else if (entry.getKey().equalsIgnoreCase("YES_NO")) {
                    f = new YES_NO(data);
                } else if (entry.getKey().equalsIgnoreCase("CSRF_TOKEN")) {
                    f = new CSRF_TOKEN(data);
                } else if (entry.getKey().equalsIgnoreCase("COMMON")) {
                    f = new COMMON(data);
                } else if (entry.getKey().equalsIgnoreCase("LOCAL_USER_PROFILE")) {
                    f = new LOCAL_USER_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("PUBLIC_PROFILE")) {
                    f = new PUBLIC_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("PRIVATE_PROFILE")) {
                    f = new PRIVATE_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("PROFILE_ID")) {
                    f = new PROFILE_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE")) {
                    f = new HIVE(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE_ID")) {
                    f = new HIVE_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT")) {
                    f = new CHAT(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE")) {
                    f = new MESSAGE(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_CONTENT")) {
                    f = new MESSAGE_CONTENT(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_ACK")) {
                    f = new MESSAGE_ACK(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_LIST")) {
                    f = new MESSAGE_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_SYNC")) {
                    f = new CHAT_SYNC(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_LIST")) {
                    f = new CHAT_LIST(data);
                } 
      

                if (f != null) result.add(f);
            }
        }

        if (result.size() == 0) { return null; }

        return result.toArray(new Format[0]);
    }

    protected Format() {}
    private Format (JsonElement data) {}
}