package com.chattyhive.backend.ContentProvider.pubsubservice;

/**
 * Created by Jonathan on 14/11/13.
 * A class representing a change in pusher connection state. It has the same members as the corresponding
 * class provided with the pusher library. A connection state change is represented by two states, the previous
 * state and the new or current state.
 */
public class ConnectionStateChange {
    private final ConnectionState previousState;
    private final ConnectionState currentState;

    /**
     * Used within the library to create a connection state change. Not to be used used as part of the API.
     * @param previousState
     * @param currentState
     */
    public ConnectionStateChange(ConnectionState previousState,
                                 ConnectionState currentState) {

        if (previousState == currentState) {
            throw new IllegalArgumentException(
                    "Attempted to create an connection state update where both previous and current state are: "
                            + currentState);
        }

        this.previousState = previousState;
        this.currentState = currentState;
    }

    /**
     * The previous connections state. The state the connection has transitioned from.
     * @return
     */
    public ConnectionState getPreviousState() {
        return previousState;
    }

    /**
     * The current connection state. The state the connection has transitioned to.
     * @return
     */
    public ConnectionState getCurrentState() {
        return currentState;
    }

    @Override
    public int hashCode() {
        return previousState.hashCode() + currentState.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof ConnectionStateChange) {
            ConnectionStateChange other = (ConnectionStateChange) obj;
            return (this.currentState == other.currentState)
                    && (this.previousState == other.previousState);
        }

        return false;
    }
}
