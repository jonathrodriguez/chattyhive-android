package com.chattyhive.backend.util.events;

import com.chattyhive.backend.contentprovider.formats.Format;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 13/07/2014.
 */
public class CommandCallbackEventArgs extends EventArgs {

    private ArrayList<Format> receivedFormats;
    private ArrayList<Format> sentFormats;

    public CommandCallbackEventArgs() { super(); }

    public CommandCallbackEventArgs(Collection<Format> receivedFormats, Collection<Format> sentFormats) {
        super();
        this.receivedFormats = new ArrayList<Format>(receivedFormats);
        this.sentFormats = new ArrayList<Format>(sentFormats);
    }

    public ArrayList<Format> getSentFormats() {
        return this.sentFormats;
    }

    public ArrayList<Format> getReceivedFormats() {
        return this.receivedFormats;
    }

    public int countReceivedFormats() {
        if (this.receivedFormats != null) return this.receivedFormats.size();
        else return -1;
    }

    public int countSentFormats() {
        if (this.sentFormats != null) return this.sentFormats.size();
        else return 0;
    }
}
