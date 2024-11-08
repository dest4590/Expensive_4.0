package ru.expensive.api.event.events;

public interface Cancellable {

    /**
     * Gets the current cancelled state of the event.
     *
     * @return True if the event is cancelled.
     */
    boolean isCancelled();

    /**
     * Sets the cancelled state of the event.
     *
     * @param state Whether the event should be cancelled or not.
     */
    void cancel();

}