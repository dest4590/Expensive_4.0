package ru.expensive.common.util.task;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum TaskPriority {
    CRITICAL_FOR_USER_PROTECTION(60),
    CRUCIAL_FOR_PLAYER_LIFE(40),
    HIGH_IMPORTANCE_3(35),
    HIGH_IMPORTANCE_2(30),
    HIGH_IMPORTANCE_1(20),
    STANDARD(0),
    LOW_PRIORITY(-20);

    int priority;
}
