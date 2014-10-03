package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.content.Context;
import android.content.SharedPreferences;

import com.chattyhive.chattyhive.framework.Util.ApplicationContextProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Jonathan on 18/07/2014.
 */
public class CookieStore implements java.net.CookieStore {

    private final static String STORE = "chCookieStore";

    @Override
    public void add(URI uri, HttpCookie cookie) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        String URIKey = uri.getHost();
        if (URIKey == null)
            URIKey = uri.toASCIIString();

        List<HttpCookie> cookies = null;

        if (sharedPreferences.contains(URIKey)) {
            String cookieString = sharedPreferences.getString(URIKey,null);
            if ((cookieString != null) && (!cookieString.isEmpty()))
                cookies = HTTPCookieFromString(cookieString);
        }

        if (cookies == null)
            cookies = new ArrayList<HttpCookie>();

        TreeMap<String,HttpCookie> cookieMap = new TreeMap<String, HttpCookie>();
        for (HttpCookie httpCookie : cookies)
            cookieMap.put(httpCookie.getName(),httpCookie);

        cookieMap.put(cookie.getName(),cookie);

        sharedPreferencesEditor.putString(URIKey, HTTPCookieToString(cookieMap.values()));
        sharedPreferencesEditor.apply();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);

        String URIKey = uri.getHost();
        if (URIKey == null)
            URIKey = uri.toASCIIString();

        List<HttpCookie> cookies = null;

        if (sharedPreferences.contains(URIKey)) {
            String cookieString = sharedPreferences.getString(URIKey,null);
            if ((cookieString != null) && (!cookieString.isEmpty()))
                cookies = HTTPCookieFromString(cookieString);
        }

        if (cookies == null)
            cookies = new ArrayList<HttpCookie>();

        return cookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);

        List<HttpCookie> cookies = null;

        for (String URIKey : sharedPreferences.getAll().keySet()) {
            if (sharedPreferences.contains(URIKey)) {
                String cookieString = sharedPreferences.getString(URIKey, null);
                if ((cookieString != null) && (!cookieString.isEmpty()))
                    if (cookies == null)
                        cookies = HTTPCookieFromString(cookieString);
                    else
                        cookies.addAll(HTTPCookieFromString(cookieString));
            }
        }

        if (cookies == null)
            cookies = new ArrayList<HttpCookie>();

        return cookies;
    }

    @Override
    public List<URI> getURIs() {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);

        List<URI> URIs = null;

        for (String URIKey : sharedPreferences.getAll().keySet()) {
            if (URIs == null) URIs = new ArrayList<URI>();
            URIs.add(URI.create(URIKey));
        }

        if (URIs == null)
            URIs = new ArrayList<URI>();

        return URIs;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        String URIKey = uri.getHost();
        if (URIKey == null)
            URIKey = uri.toASCIIString();

        List<HttpCookie> cookies = null;

        if (sharedPreferences.contains(URIKey)) {
            String cookieString = sharedPreferences.getString(URIKey,null);
            if ((cookieString != null) && (!cookieString.isEmpty()))
                cookies = HTTPCookieFromString(cookieString);
        }

        if (cookies == null)
            return false;

        Boolean result = cookies.remove(cookie);

        sharedPreferencesEditor.putString(URIKey, HTTPCookieToString(cookies));
        sharedPreferencesEditor.apply();

        return result;
    }

    @Override
    public boolean removeAll() {

        Context context = ApplicationContextProvider.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(STORE,context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();

        Boolean result = (sharedPreferences.getAll().size() > 0);

        sharedPreferencesEditor.clear().apply();

        result = result && (sharedPreferences.getAll().size() == 0);

        return result;
    }

    private String HTTPCookieToString(Collection<HttpCookie> cookies) {
        return HTTPCookieToJson(cookies).toString();
    }
    private List<HttpCookie> HTTPCookieFromString(String cookies) {
        return HTTPCookieFromJson(new JsonParser().parse(cookies));
    }

    private JsonElement HTTPCookieToJson(Collection<HttpCookie> cookies) {
        JsonArray cookieArray = new JsonArray();

        for (HttpCookie cookie : cookies)
            cookieArray.add(HTTPCookieToJson(cookie));

        return cookieArray;
    }
    private List<HttpCookie> HTTPCookieFromJson (JsonElement cookies) {
        List<HttpCookie> result = new ArrayList<HttpCookie>();

        if (cookies.isJsonArray()) {
            for (JsonElement cookie : cookies.getAsJsonArray())
                result.addAll(HTTPCookieFromJson(cookie));
        } else if (cookies.isJsonObject()) {
            JsonObject cookie = cookies.getAsJsonObject();
            HttpCookie httpCookie;
            try {
                httpCookie = new HttpCookie(cookie.get("Name").getAsString(), cookie.get("Value").getAsString());
            } catch (NullPointerException e) {
                return result;
            }

            if (!cookie.get("MaxAge").isJsonNull())
                httpCookie.setMaxAge(cookie.get("MaxAge").getAsLong());

            if (!cookie.get("Comment").isJsonNull())
                httpCookie.setComment(cookie.get("Comment").getAsString());
            if (!cookie.get("CommentURL").isJsonNull())
                httpCookie.setCommentURL(cookie.get("CommentURL").getAsString());

            if (!cookie.get("Path").isJsonNull())
                httpCookie.setPath(cookie.get("Path").getAsString());
            if (!cookie.get("Domain").isJsonNull())
                httpCookie.setDomain(cookie.get("Domain").getAsString());
            if (!cookie.get("PortList").isJsonNull())
                httpCookie.setPortlist(cookie.get("PortList").getAsString());

            if (!cookie.get("Discard").isJsonNull())
                httpCookie.setDiscard(cookie.get("Discard").getAsBoolean());
            if (!cookie.get("Secure").isJsonNull())
                httpCookie.setSecure(cookie.get("Secure").getAsBoolean());
            if (!cookie.get("Version").isJsonNull())
                httpCookie.setVersion(cookie.get("Version").getAsInt());

            result.add(httpCookie);
        }

        return result;
    }
    private JsonElement HTTPCookieToJson(HttpCookie cookie) {
        JsonObject JsonCookie = new JsonObject();

        JsonCookie.addProperty("Name",cookie.getName());
        JsonCookie.addProperty("Value",cookie.getValue());
        JsonCookie.addProperty("MaxAge",cookie.getMaxAge());

        JsonCookie.addProperty("Comment",cookie.getComment());
        JsonCookie.addProperty("CommentURL",cookie.getCommentURL());

        JsonCookie.addProperty("Path",cookie.getPath());
        JsonCookie.addProperty("Domain",cookie.getDomain());
        JsonCookie.addProperty("PortList",cookie.getPortlist());

        JsonCookie.addProperty("Discard",cookie.getDiscard());
        JsonCookie.addProperty("Secure",cookie.getSecure());
        JsonCookie.addProperty("Version",cookie.getVersion());

        return JsonCookie;
    }
}
