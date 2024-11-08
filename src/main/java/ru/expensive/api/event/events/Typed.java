package ru.expensive.api.event.events;

public interface Typed {

    /**
     * Gets the current type of the event.
     *
     * @return The type ID of the event.
     */
    byte getType();

}
