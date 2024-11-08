package ru.expensive.api.feature.draggable;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.expensive.implement.features.draggables.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DraggableRepository {
    List<AbstractDraggable> draggable = new ArrayList<>();

    public void setup() {
        register(
                new TargetHudDraggable(),
                new PotionsDraggable(),
                new HotKeysDraggable(),
                new ArmorDraggable(),
                new WatermarkDraggable(),
                new CoordsDraggable());
    }

    public void register(AbstractDraggable... module) {
        draggable.addAll(List.of(module));
    }

    public List<AbstractDraggable> draggable() {
        return draggable;
    }
}
