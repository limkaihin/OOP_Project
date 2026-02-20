package com.example.app.engine.entity;

import com.example.app.engine.components.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Entity {
    private final int id;
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Map<Class<? extends Component>, Component> viewComponents() {
        return Collections.unmodifiableMap(components);
    }

    public <T extends Component> void addComponent(Class<T> type, T component) {
        if (type == null){
             throw new IllegalArgumentException("type cannot be null");
        }
        if (component == null){ 
            throw new IllegalArgumentException("component cannot be null");
        }
        components.put(type, component);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        Object c = components.get(type);
        if (c == null){
            return null;
        }
        return type.cast(c);
    }

    public <T extends Component> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    public <T extends Component> void removeComponent(Class<T> type) {
        components.remove(type);
    }

    public <T extends Component> void updateComponent(Class<T> type, T component) {
        if (type == null || component == null) {
            throw new IllegalArgumentException("Type and component cannot be null");
        }
        components.put(type, component);
    }

    public String toString() {
        return "Entity{id=" + id + ", components=" + components.size() + "}";
    }
}
