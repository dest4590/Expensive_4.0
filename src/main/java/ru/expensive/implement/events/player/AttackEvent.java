package ru.expensive.implement.events.player;

import lombok.Value;
import net.minecraft.entity.Entity;
import ru.expensive.api.event.events.Event;

@Value
public class AttackEvent implements Event {
    Entity entity;
    byte eventType;
}
