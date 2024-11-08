package ru.expensive.api.system.animation;

public interface AnimationCalculation {
    default double calculation(double value) {
        return 0;
    }
}
