package com.example.app.engine.render;

public interface IRenderer {
    void begin();
    void end();
    void draw(String key, float x, float y, float radius, float width, float height);
    void drawText(InterfaceFont font, String text, float x, float y);
    void drawRect(float x, float y, float w, float h, EngineColor color);
    void drawCircle(float x, float y, float radius, EngineColor color);
    void drawLine(float x1, float y1, float x2, float y2, EngineColor color);
    void loadTexture(String key, String path);
    void unloadTexture(String key);
    void flushSprites();
}