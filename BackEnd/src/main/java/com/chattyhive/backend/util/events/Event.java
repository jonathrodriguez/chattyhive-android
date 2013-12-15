package com.chattyhive.backend.util.events;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Jonathan on 15/12/13.
 */
public class Event<T extends EventArgs> {
    private ArrayList<EventHandler<T>> _eventHandler;

    public Event () {
        this._eventHandler = new ArrayList<EventHandler<T>>();
    }

    public void add(EventHandler<T> eventHandler) {
        this._eventHandler.add(eventHandler);
    }

    public void clear() {
        this._eventHandler.clear();
    }

    public void fire(Object sender,T eventArgs) {
        Iterator<EventHandler<T>> iterator = this._eventHandler.iterator();
        while (iterator.hasNext()) {
            EventHandler<T> eventHandler = iterator.next();
            try {
                eventHandler.Invoke(sender,eventArgs);
            } catch (InvocationTargetException invocationTargetException) {
                iterator.remove();
                continue;
            } catch (IllegalAccessException illegalAccessException) {
                iterator.remove();
                continue;
            }
        }
    }
}
