package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.app.engine.EngineContext;
import com.example.app.engine.scene.AbstractBaseScene;

public final class LevelSelectScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private SpriteBatch batch;
    private BitmapFont font;
    
    public static int maxUnlockedLevel = 1;

    public LevelSelectScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLoad() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        ctx.ioManager.log("LevelSelectScene", "Loaded (Max unlocked: " + maxUnlockedLevel + ")");
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int mx = Gdx.input.getX();
            int my = Gdx.graphics.getHeight() - Gdx.input.getY(); // Convert to bottom-left origin
            
            // Check collisions with level boxes
            float startX = 80f;
            float startY = 320f;
            float boxSize = 80f;
            float gapX = 20f;
            float gapY = 30f;
            
            for (int i = 0; i < 10; i++) {
                int row = i / 5;
                int col = i % 5;
                float bx = startX + col * (boxSize + gapX);
                float by = startY - row * (boxSize + gapY);
                
                // Box collision
                if (mx >= bx && mx <= bx + boxSize && my >= by && my <= by + boxSize) {
                    if (i + 1 <= maxUnlockedLevel) {
                        ctx.sceneManager.switchTo(new TransitionScene(ctx, new TrainScene(ctx, i + 1), 0.6f));
                    } else {
                        ctx.ioManager.playSound("hit.wav"); // "error" sound
                    }
                }
            }
        }
    }

    @Override
    public void render() {
        // GameMaster has already called ctx.renderer.begin()
        float startX = 80f;
        float startY = 320f;
        float boxSize = 80f;
        float gapX = 20f;
        float gapY = 30f;
        
        for (int i = 0; i < 10; i++) {
            int row = i / 5;
            int col = i % 5;
            float bx = startX + col * (boxSize + gapX);
            float by = startY - row * (boxSize + gapY);
            
            // Draw box using the "PLAYER" (blue rect) type
            ctx.renderer.draw("PLAYER", bx + boxSize/2, by + boxSize/2, 0, boxSize, boxSize);
        }
        ctx.renderer.end(); // Stop ShapeRenderer before starting SpriteBatch
        
        // Now draw the text over the boxes
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "SELECT LEVEL", 220, 440);
        
        for (int i = 0; i < 10; i++) {
            int row = i / 5;
            int col = i % 5;
            float bx = startX + col * (boxSize + gapX);
            float by = startY - row * (boxSize + gapY);
            
            String text = String.valueOf(i + 1);
            if (i + 1 > maxUnlockedLevel) {
                font.setColor(Color.RED);
            } else {
                font.setColor(Color.GREEN);
            }
            font.draw(batch, "Lv " + text, bx + 15, by + 50);
        }
        batch.end();

        ctx.renderer.begin(); // Restart ShapeRenderer for GameMaster
    }

    @Override
    public void onUnload() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}
