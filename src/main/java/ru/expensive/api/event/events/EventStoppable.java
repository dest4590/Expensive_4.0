package ru.expensive.api.event.events;

public abstract class EventStoppable implements Event {

    private boolean stopped;

    /**
     * No need for the constructor to be public.
     */
    protected EventStoppable() {
    }

    /**
     * Sets the stopped state to true.
     */
    public void stop() {
        stopped = true;
    }

    /**
     * Checks the stopped boolean.
     *
     * @return True if the EventStoppable is stopped.
     */
    public boolean isStopped() {
        return stopped;
    }

}