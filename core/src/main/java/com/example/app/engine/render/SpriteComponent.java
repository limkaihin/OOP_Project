package com.example.app.engine.render;

import com.example.app.engine.components.Component;

public class SpriteComponent implements Component {
    public String texturePath;
    public float width;
    public float height;

    public SpriteComponent(String texturePath, float width, float height) {
        this.texturePath = texturePath;
        this.width = width;
        this.height = height;
    }
}
