package com.example.app.demo.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.example.app.engine.render.IRenderer;

public class LibGdxRenderer implements IRenderer {
    private final ShapeRenderer shapes;

    public LibGdxRenderer(ShapeRenderer shapes) {
        this.shapes = shapes;
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
        if ("PLAYER".equals(key)) {
            shapes.setColor(0.25f, 0.75f, 1f, 1f);
            shapes.rect(x - w/2, y - h/2, w, h);
        } else if ("NPC".equals(key)) {
            shapes.setColor(1.0f, 0.55f, 0.10f, 1f);
            shapes.circle(x, y, radius);
        } else if ("OBSTACLE".equals(key)) {
            shapes.setColor(1.0f, 0.35f, 0.35f, 1f);
            shapes.circle(x, y, radius);
        }
    }
}
