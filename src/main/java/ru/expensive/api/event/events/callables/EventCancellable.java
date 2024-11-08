package ru.expensive.api.event.events.callables;

import ru.expensive.api.event.events.Cancellable;
import ru.expensive.api.event.events.Event;

public abstract class EventCancellable implements Event, Cancellable {

    private boolean cancelled;

    protected EventCancellable() {
    }

    /**
     * @see com.darkmagician6.eventapi.events.Cancellable.isCancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @see com.darkmagician6.eventapi.events.Cancellable.setCancelled
     */
    @Override
    public void cancel() {
        cancelled = true;
    }
}