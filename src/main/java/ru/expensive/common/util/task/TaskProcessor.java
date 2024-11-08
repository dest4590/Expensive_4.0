package ru.expensive.common.util.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.expensive.api.feature.module.Module;

import java.util.PriorityQueue;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TaskProcessor<T> {
    int tickCounter = 0;
    public PriorityQueue<Task<T>> activeTasks = new PriorityQueue<>((r1, r2) -> Integer.compare(r2.priority, r1.priority));

    public void tick(int deltaTime) {
        tickCounter += deltaTime;
    }

    public void addTask(Task<T> task) {
        activeTasks.removeIf(r -> r.provider.equals(task.provider));
        task.expiresIn += tickCounter;
        this.activeTasks.add(task);
    }

    public T fetchActiveTaskValue() {
        while (!activeTasks.isEmpty() && activeTasks.peek() != null
                && (activeTasks.peek().expiresIn <= tickCounter || !activeTasks.peek().provider.isState())) {
            activeTasks.poll();
        }

        if (activeTasks.isEmpty()) {
            return null;
        } else if (activeTasks.peek() != null) {
            return activeTasks.peek().value;
        } else {
            return null;
        }
    }

    @ToString
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    public static class Task<T> {
        @NonFinal
        int expiresIn;
        int priority;
        Module provider;
        T value;
    }
}