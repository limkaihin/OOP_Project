package com.example.app.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class EventBus<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public void subscribe(Consumer<T> listener) {
        Preconditions.notNull(listener, "listener");
        listeners.add(listener);
    }

    public void publish(T event) {
        for (Consumer<T> l : listeners) {
            l.accept(event);
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}
