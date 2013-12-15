package com.chattyhive.backend.util.events;

/**
 * Created by Jonathan on 15/12/13.
 */
public class EventArgs {
    private static final EventArgs _empty = new EventArgs();
    public static final EventArgs Empty() { return _empty; }
    public EventArgs() { }
}
