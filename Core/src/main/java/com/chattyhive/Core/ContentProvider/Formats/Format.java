package com.chattyhive.Core.ContentProvider.Formats;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import com.google.gson.JsonElement;


/*
  * Automatically generated code by ChattyHive API Manager Code Generator on 28/09/2014.
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
                } else if (entry.getKey().equalsIgnoreCase("CSRF_TOKEN")) {
                    f = new CSRF_TOKEN(data);
                } else if (entry.getKey().equalsIgnoreCase("COMMON")) {
                    f = new COMMON(data);
                } else if (entry.getKey().equalsIgnoreCase("LOGIN")) {
                    f = new LOGIN(data);
                } else if (entry.getKey().equalsIgnoreCase("LOCAL_USER_PROFILE")) {
                    f = new LOCAL_USER_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("USER_PROFILE")) {
                    f = new USER_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("BASIC_PUBLIC_PROFILE")) {
                    f = new BASIC_PUBLIC_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("USER_PROFILE_LIST")) {
                    f = new USER_PROFILE_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("PUBLIC_PROFILE")) {
                    f = new PUBLIC_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("BASIC_PRIVATE_PROFILE")) {
                    f = new BASIC_PRIVATE_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("PRIVATE_PROFILE")) {
                    f = new PRIVATE_PROFILE(data);
                } else if (entry.getKey().equalsIgnoreCase("PROFILE_ID")) {
                    f = new PROFILE_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("FRIEND_LIST")) {
                    f = new FRIEND_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("USER_EMAIL")) {
                    f = new USER_EMAIL(data);
                } else if (entry.getKey().equalsIgnoreCase("USERNAME")) {
                    f = new USERNAME(data);
                } else if (entry.getKey().equalsIgnoreCase("EXPLORE_FILTER")) {
                    f = new EXPLORE_FILTER(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE_LIST")) {
                    f = new HIVE_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE")) {
                    f = new HIVE(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE_ID")) {
                    f = new HIVE_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT")) {
                    f = new CHAT(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_ID")) {
                    f = new CHAT_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("CONTEXT")) {
                        f = new CONTEXT(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_LIST")) {
                    f = new CHAT_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("CHAT_SYNC")) {
                    f = new CHAT_SYNC(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE")) {
                    f = new MESSAGE(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_CONTENT")) {
                    f = new MESSAGE_CONTENT(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_CONFIRMATION")) {
                    f = new MESSAGE_CONFIRMATION(data);
                } else if (entry.getKey().equalsIgnoreCase("CHANNEL_MESSAGE_CONFIRMATION")) {
                    f = new CHANNEL_MESSAGE_CONFIRMATION(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_ACK")) {
                    f = new MESSAGE_ACK(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_INTERVAL")) {
                    f = new MESSAGE_INTERVAL(data);
                } else if (entry.getKey().equalsIgnoreCase("MESSAGE_LIST")) {
                    f = new MESSAGE_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("HIVE_USERS_FILTER")) {
                    f = new HIVE_USERS_FILTER(data);
                } else if (entry.getKey().equalsIgnoreCase("INTERVAL")) {
                    f = new INTERVAL(data);
                } else if (entry.getKey().equalsIgnoreCase("YES_NO")) {
                    f = new YES_NO(data);
                } else if (entry.getKey().equalsIgnoreCase("REQUEST_LIST")) {
                    f = new REQUEST_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("REQUEST")) {
                    f = new REQUEST(data);
                } else if (entry.getKey().equalsIgnoreCase("REQUEST_ID")) {
                    f = new REQUEST_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("LOCATION_LIST")) {
                    f = new LOCATION_LIST(data);
                } else if (entry.getKey().equalsIgnoreCase("LOCATION")) {
                    f = new LOCATION(data);
                } else if (entry.getKey().equalsIgnoreCase("LOCATION_ID")) {
                    f = new LOCATION_ID(data);
                } else if (entry.getKey().equalsIgnoreCase("REPORT")) {
                    f = new REPORT(data);
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