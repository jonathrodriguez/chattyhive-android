package com.chattyhive.backend.Util.Events;

/**
 * Created by Jonathan on 15/12/13.
 * Generic class for event arguments. This class is useful for events with no arguments. To pass
 * any information to the subscriber this class MAY be extended. In order to use this class, please
 * do not construct it, only call the static Empty() method, which returns an empty instance of
 * EventArgs.
 */
public class EventArgs {
    private static final EventArgs _empty = new EventArgs();
    public static final EventArgs Empty() { return _empty; }
    public EventArgs() { }
}
