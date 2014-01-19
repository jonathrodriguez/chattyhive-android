package com.chattyhive.backend.util.events;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Jonathan on 15/12/13.
 * This is a generic class to provide .NET style events implementation. A class may have as many
 * event objects as different events it will fire. In order to instantiate the ArrayList, the event
 * constructor MAY be called. To fire an event, the fire method HAS TO be invoked.
 * Any object that wishes to be informed about an event HAS TO declare a public method which takes
 * two arguments. First argument is an Object; the sender of the event. Second argument are the event
 * arguments. Event arguments CAN be EventArgs or any derived class. In order to subscribe to the
 * event, an EventHandler must be created with the method to be invoked.
 * If the class that contains the event defines it as private, then it HAS TO provide any way for
 * other objects to subscribe to the event, else firing the event will do nothing as there will not
 * be any method to invoke.
 */
// TODO: Provide a way to unsubscribe.

public class Event<T extends EventArgs> {
    private ArrayList<EventHandler<T>> _eventHandler;

    /**
     * Public constructor. Initialises the ArrayList which will contain the eventHandlers
     * to the methods to be invoked.
     */
    public Event () {
        this._eventHandler = new ArrayList<EventHandler<T>>();
    }


    /**
     * Adds an eventHandler to the list.
     * @param eventHandler An eventHandler to the method which may be invoked when event is fired.
     */
    public void add(EventHandler<T> eventHandler) {
        this._eventHandler.add(eventHandler);
    }

    /**
     * Clears the list, leaving it empty. Useful for memory freeing on object destruction.
     */
    public void clear() {
        this._eventHandler.clear();
    }

    /**
     * Invokes all the methods in the eventHandlers of the list. Every method that can not be invoked
     * is removed from the collection.
     * @param sender An Object representing the object which fired the event.
     * @param eventArgs The event arguments. They CAN be of any class but HAVE TO extend EventArgs.
     */
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
