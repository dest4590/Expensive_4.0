package ru.expensive.implement.events.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import ru.expensive.api.event.events.callables.EventCancellable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BoundingBoxControlEvent extends EventCancellable {
    Box box;
    Box changedBox;
    Entity entity;

    public BoundingBoxControlEvent(Box box, Entity entity) {
        this.box = box;
        this.entity = entity;
    }
}
