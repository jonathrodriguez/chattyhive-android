package com.chattyhive.Core.Util.Events;

import com.chattyhive.Core.ContentProvider.SynchronousDataPath.AvailableCommands;
import com.chattyhive.Core.ContentProvider.Formats.Format;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Jonathan on 13/07/2014.
 */
public class CommandCallbackEventArgs extends EventArgs {

    private ArrayList<Format> receivedFormats;
    private ArrayList<Format> sentFormats;
    private AvailableCommands command;
    private Object additionalData;

    public CommandCallbackEventArgs() { super(); }

    public CommandCallbackEventArgs(AvailableCommands command, Collection<Format> receivedFormats, Collection<Format> sentFormats, Object additionalData) {
        super();
        if (receivedFormats != null)
            this.receivedFormats = new ArrayList<Format>(receivedFormats);
        if (sentFormats != null)
            this.sentFormats = new ArrayList<Format>(sentFormats);

        this.command = command;
        this.additionalData = additionalData;
    }

    public ArrayList<Format> getSentFormats() {
        return this.sentFormats;
    }
    public ArrayList<Format> getReceivedFormats() {
        return this.receivedFormats;
    }
    public AvailableCommands getCommand() {
        return this.command;
    }

    public Object getAdditionalData() {
        return this.additionalData;
    }

    public int countReceivedFormats() {
        if (this.receivedFormats != null) return this.receivedFormats.size();
        else return 0;
    }
    public int countSentFormats() {
        if (this.sentFormats != null) return this.sentFormats.size();
        else return 0;
    }
}
