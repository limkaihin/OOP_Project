package com.example.app.engine.render;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Color;

public interface IRenderer {
    void begin();
    void end();
    void draw(String key, float x, float y, float radius, float width, float height);
    void drawText(BitmapFont font, String text, float x, float y);
    void drawRect(float x, float y, float w, float h, Color color);
    void drawCircle(float x, float y, float radius, Color color);
    void drawLine(float x1, float y1, float x2, float y2, Color color);
    void loadTexture(String key, String path);
    void unloadTexture(String key);
    void flushSprites();
}