package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.scene.AbstractBaseScene;

/**
 * Shown after the player clears the final level.
 * ESC = quit, click "Restart" = full game restart back to MenuScene.
 */
public final class GameClearScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private SpriteBatch batch;
    private BitmapFont  bigFont;
    private BitmapFont  font;

    // Restart button hit-box
    private float btnX, btnY, btnW, btnH;

    // Simple pulse animation
    private float pulse = 0f;

    public GameClearScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    // ---------------------------------------------------------------- lifecycle

    @Override
    public void onLoad() {
        batch   = new SpriteBatch();
        bigFont = new BitmapFont();
        font    = new BitmapFont();

        bigFont.getData().setScale(3.5f);
        font.getData().setScale(1.6f);

        // Restart button centred on screen
        float W = ctx.config.width;
        float H = ctx.config.height;
        btnW = 200f; btnH = 48f;
        btnX = (W - btnW) / 2f;
        btnY = H / 2f - 80f;

        ctx.ioManager.log("GameClearScene", "Loaded");
    }

    @Override
    public void onUnload() {
        if (batch   != null) batch.dispose();
        if (bigFont != null) bigFont.dispose();
        if (font    != null) font.dispose();
    }

    // ---------------------------------------------------------------- update

    @Override
    public void update(float dt) {
        pulse += dt;

        // ESC → quit
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.BACK)) {
            Gdx.app.exit();
            return;
        }

        // Mouse click on Restart button → full reset
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                restartGame();
            }
        }
    }

    // ---------------------------------------------------------------- render

    @Override
    public void render() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        // Dark starry background
        ctx.renderer.drawRect(0, 0, W, H, new Color(0.04f, 0.04f, 0.10f, 1f));

        // Pulsing gold banner behind title
        float glow = 0.5f + 0.5f * (float) Math.sin(pulse * 2.5f);
        ctx.renderer.drawRect(0, H / 2f + 30f, W, 70f,
                new Color(0.55f * glow, 0.45f * glow, 0.05f * glow, 1f));

        // Restart button background
        ctx.renderer.drawRect(btnX, btnY, btnW, btnH,
                new Color(0.18f, 0.45f, 0.22f, 1f));
    }

    @Override
    public void renderHud() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        batch.begin();

        // Title
        float glow = 0.5f + 0.5f * (float) Math.sin(pulse * 2.5f);
        bigFont.setColor(new Color(1.0f, 0.85f + 0.15f * glow, 0.1f, 1f));
        bigFont.draw(batch, "YOU WIN!", 130f, H / 2f + 95f);

        // Sub-text
        font.setColor(new Color(0.85f, 0.85f, 0.95f, 1f));
        font.draw(batch, "You beat all 5 levels!", 170f, H / 2f + 15f);

        // Restart button label
        font.setColor(Color.WHITE);
        font.draw(batch, "  Click to Restart", btnX + 10f, btnY + btnH - 8f);

        // ESC hint
        font.setColor(new Color(0.6f, 0.6f, 0.7f, 1f));
        font.getData().setScale(1.2f);
        font.draw(batch, "Press ESC to Quit", W / 2f - 80f, btnY - 20f);
        font.getData().setScale(1.6f);

        batch.end();
    }

    // ---------------------------------------------------------------- helpers

    private void restartGame() {
        // Reset progression
        LevelSelectScene.maxUnlockedLevel = 1;

        // Clear stack and go back to menu (switchTo pops the current scene first)
        while (ctx.sceneManager.size() > 1) {
            ctx.sceneManager.pop();
        }
        ctx.sceneManager.switchTo(new MenuScene(ctx));

        // Restart background music
        ctx.ioManager.playMusic("music1.mp3");
    }
}
