package org.wallentines.midnightlib.event;

import java.util.concurrent.*;

/**
 * A HandlerList which invokes event handlers concurrently.
 * @param <T> The type of event to handle. This should be read-only or otherwise thread safe.
 */
public class ConcurrentHandlerList<T> extends HandlerList<T> {

    private final Executor executor;

    public ConcurrentHandlerList(Executor executor) {
        this.executor = executor;
    }

    public void invoke(T event) {
        invokeAsync(event).join();
    }

    public CompletableFuture<Void> invokeAsync(T event) {
        clearExpiredHandlers();
        if(handlers.isEmpty()) {
            CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.allOf(handlers.stream()
                .map(wh -> CompletableFuture.runAsync(() -> handle(wh.handler, event), executor))
                .toArray(CompletableFuture[]::new)
        );

    }

}
