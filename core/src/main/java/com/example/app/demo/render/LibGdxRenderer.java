package com.example.app.demo.render;

import java.util.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.example.app.engine.render.IRenderer;

public class LibGdxRenderer implements IRenderer {
    private final ShapeRenderer shapes;
    private final SpriteBatch batch;
    private final Map<String, Texture> textures = new HashMap<>();
    private final Map<String, ShapeDrawer> drawers = new HashMap<>();
    private boolean inShapeMode = false;

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

    public LibGdxRenderer(ShapeRenderer shapes, SpriteBatch batch) {
        this.shapes = shapes;
        this.batch = batch;
        initDrawers();
    }

    public void loadTexture(String key, String internalPath) {
        if (!textures.containsKey(key)) {
            try {
                textures.put(key, new Texture(Gdx.files.internal(internalPath)));
            } catch (Exception e) {
                ctx.ioManager.log("LibGdxRenderer", "Failed to load texture: " + internalPath);
            }
        }
    }

    public void unloadTexture(String key) {
        Texture t = textures.remove(key);
        if (t != null) t.dispose();
    }
 
    public void unloadAllTextures() {
        for (Texture t : textures.values()) t.dispose();
        textures.clear();
    }

    @Override
    public void begin() {
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        inShapeMode = true;
    }

    @Override
    public void end() {
        if (inShapeMode) {
            shapes.end();
            inShapeMode = false;
        }
    }

    @Override
    public void draw(String key, float x, float y, float radius, float w, float h) {
        ShapeDrawer shapeDrawer = shapeDrawers.get(key);
        if (shapeDrawer != null) {
            shapeDrawer.draw(x, y, radius, w, h);
            return;
        }
 
        Texture tex = textures.get(key);
        if (tex != null) {
            // Switch from ShapeRenderer to SpriteBatch
            if (inShapeMode) {
                shapes.end();
                inShapeMode = false;
                batch.begin();
            } else if (!batch.isDrawing()) {
                batch.begin();
            }
            batch.draw(tex, x - w / 2, y - h / 2, w, h);
            return;
        }
 
        ctx.ioManager.log("LibGdxRenderer", "No drawer or texture found for key: " + key);
    }

    public void flushSprites() {
        if (batch.isDrawing()) {
            batch.end();
            // Re-enter shape mode since GameMaster expects ShapeRenderer to be active
            shapes.begin(ShapeRenderer.ShapeType.Filled);
            inShapeMode = true;
        }
    }
 
    public void dispose() {
        unloadAllTextures();
    }

    // Helper for Hash Map
    interface ShapeDrawer {
        void draw(float x, float y, float radius, float w, float h);
    }
}
