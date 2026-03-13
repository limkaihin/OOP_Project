package com.example.app.demo.render;

import java.util.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.example.app.engine.render.IRenderer;

public class LibGdxRenderer implements IRenderer {
    private final ShapeRenderer shapes;
    private final Map<String, ShapeDrawer> drawers = new HashMap<>();

    private void initDrawers() {
        drawers.put("PLAYER", (x, y, radius, w, h) -> {
            shapes.setColor(0.25f, 0.75f, 1f, 1f);
            shapes.rect(x - w / 2, y - h / 2, w, h);
        });
        drawers.put("NPC", (x, y, radius, w, h) -> {
            shapes.setColor(1.0f, 0.55f, 0.10f, 1f);
            shapes.circle(x, y, radius);
        });
        drawers.put("OBSTACLE", (x, y, radius, w, h) -> {
            shapes.setColor(1.0f, 0.35f, 0.35f, 1f);
            shapes.circle(x, y, radius);
        });
        drawers.put("MENU", (x, y, radius, w, h) -> {
            shapes.setColor(0.4f, 0.6f, 0.9f, 0.25f);
            shapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        });
        drawers.put("TRANSITION", (x, y, alpha, w, h) -> {
            shapes.setColor(0.4f, 0.6f, 0.9f, alpha);
            shapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        });
    }

    public LibGdxRenderer(ShapeRenderer shapes) {
        this.shapes = shapes;
        initDrawers();
    }

    @Override
    public void begin() {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    public void end() {
        shapes.end();
    }

    @Override
    public void draw(String key, float x, float y, float radius, float w, float h) {
        ShapeDrawer drawer = drawers.get(key);
        if (drawer != null) {
            drawer.draw(x, y, radius, w, h);
        }
    }

    // Helper for Hash Map
    interface ShapeDrawer {
        void draw(float x, float y, float radius, float w, float h);
    }
}
