package ru.expensive.common.util.coroutine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoroutineContext {
    private final ExecutorService executor;

    public CoroutineContext() {
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void launch(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}