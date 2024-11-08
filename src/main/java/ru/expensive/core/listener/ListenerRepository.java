package ru.expensive.core.listener;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.expensive.core.Expensive;
import ru.expensive.core.listener.impl.PacketEventListener;
import ru.expensive.core.listener.impl.TickEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListenerRepository {
    final List<Listener> listeners = new ArrayList<>();

    public void setup() {
        registerListeners(
                new TickEventListener(),
                new PacketEventListener());
    }

    public void registerListeners(Listener... listeners) {
        this.listeners.addAll(List.of(listeners));
        Arrays.stream(listeners).forEach(listener -> Expensive.getInstance().getEventManager().register(listener));
    }
}
