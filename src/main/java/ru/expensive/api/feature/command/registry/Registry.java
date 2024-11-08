package ru.expensive.api.feature.command.registry;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Registry<V> {
    private final Deque<V> _entries = new LinkedList<>();
    public final Collection<V> entries = Collections.unmodifiableCollection(_entries);
    private final Set<V> registered = new HashSet<>();

    public boolean registered(V entry) {
        return registered.contains(entry);
    }

    public void register(V entry) {
        if (!registered(entry)) {
            _entries.addFirst(entry);
            registered.add(entry);
        }
    }

    public void unregister(V entry) {
        if (!registered(entry)) {
            return;
        }
        _entries.remove(entry);
        registered.remove(entry);
    }

    public Iterator<V> iterator() {
        return _entries.iterator();
    }

    public Iterator<V> descendingIterator() {
        return _entries.descendingIterator();
    }

    public Stream<V> stream() {
        return _entries.stream();
    }

    public Stream<V> descendingStream() {
        Spliterator<V> spliterator = Spliterators.spliterator(
                descendingIterator(),
                _entries.size(),
                Spliterator.SIZED | Spliterator.SUBSIZED);

        return StreamSupport.stream(
                spliterator,
                false);
    }
}
