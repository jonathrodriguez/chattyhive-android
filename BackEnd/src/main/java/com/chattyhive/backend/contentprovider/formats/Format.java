package com.chattyhive.backend.contentprovider.formats;

import com.chattyhive.backend.contentprovider.formats.response.AnonymousChatInfoResponseFormat;
import com.chattyhive.backend.contentprovider.formats.response.AnonymousUserResponseFormat;
import com.google.gson.JsonElement;

/**
 * Created by Jonathan on 23/06/2014.
 */
public abstract class Format {
    public abstract JsonElement toJSON();
    public abstract void fromJSON(JsonElement data);

    public static Format getFormat(JsonElement data,Formats format) {

        switch (format) {
            case ANONYMOUS_CHAT_INFO_RESPONSE:
                return new AnonymousChatInfoResponseFormat(data);
            case ANONYMOUS_USER_RESPONSE:
                return new AnonymousUserResponseFormat(data);

            default:
                return null;
        }
    }

    protected Format() {}
    private Format (JsonElement data) {}
}
