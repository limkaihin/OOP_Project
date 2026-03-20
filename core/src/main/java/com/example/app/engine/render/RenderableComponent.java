package com.example.app.engine.render;

import com.example.app.engine.components.Component;

public final class RenderableComponent implements Component {

    public String renderKey;
    public float width;
    public float height;

    public RenderableComponent(String key) {
        this.renderKey = key;
    }

    public RenderableComponent(String renderKey, float width, float height) {
        this.renderKey = renderKey;
        this.width = width;
        this.height = height;
    }
}