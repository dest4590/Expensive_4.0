package ru.expensive.common.util.coroutine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coroutine {
    private final ExecutorService executor;
    private final CompletableFuture<Void> future;

    public Coroutine(Runnable task) {
        this.executor = Executors.newSingleThreadExecutor();
        this.future = CompletableFuture.runAsync(task, executor);
    }

    public void await() throws Exception {
        future.get();
    }

    public void shutdown() {
        executor.shutdown();
    }
}