package com.chattyhive.Core.Util.Events;

import com.chattyhive.Core.ContentProvider.Formats.Format;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 11/07/2014.
 */
public class FormatReceivedEventArgs extends EventArgs {

    private ArrayList<Format> receivedFormats;

    public FormatReceivedEventArgs() { super(); }

    public FormatReceivedEventArgs(Collection<? extends Format> receivedFormats) {
        super();
        this.receivedFormats = new ArrayList<Format>(receivedFormats);
    }

    public ArrayList<Format> getReceivedFormats() {
        return this.receivedFormats;
    }

    public int countReceivedFormats() {
        if (this.receivedFormats != null) return this.receivedFormats.size();
        else return -1;
    }
}
