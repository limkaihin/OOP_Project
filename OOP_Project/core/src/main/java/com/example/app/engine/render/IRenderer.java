package com.example.app.engine.render;

public interface IRenderer {
    void begin();
    void end();
    void draw(String key, float x, float y, float radius, float width, float height);
}
