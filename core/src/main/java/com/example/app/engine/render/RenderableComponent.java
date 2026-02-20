package com.example.app.engine.render;

import com.example.app.engine.components.Component;

public final class RenderableComponent implements Component {

    public String renderKey;

    public RenderableComponent(String key) {
        this.renderKey = key;
    }
}