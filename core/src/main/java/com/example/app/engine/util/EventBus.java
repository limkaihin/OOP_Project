package com.example.app.engine.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class EventBus<T> {
    private final List<Consumer<T>> listeners = new ArrayList<>();

    public final class Subscription {
        private final Consumer<T> listener;

        private Subscription(Consumer<T> listener) {
            this.listener = listener;
        }

        public void cancel() {
            listeners.remove(listener);
        }
    }

    public Subscription subscribe(Consumer<T> listener) {
        Preconditions.notNull(listener, "listener");
        listeners.add(listener);
        return new Subscription(listener);
    }

    public void publish(T event) {
        for (Consumer<T> l : new ArrayList<>(listeners)) {
            l.accept(event);
        }
    }

    public int listenerCount() {
        return listeners.size();
    }
}