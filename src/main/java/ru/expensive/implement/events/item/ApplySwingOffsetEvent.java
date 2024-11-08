package ru.expensive.implement.events.item;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import ru.expensive.api.event.events.callables.EventCancellable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class ApplySwingOffsetEvent extends EventCancellable {
    MatrixStack matrices;
    Arm arm;
    float swingProgress;
}
