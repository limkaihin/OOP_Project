package com.example.app.demo.scenes;

import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.example.app.demo.render.LibGdxFont;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.EngineContext;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.scene.Scene;

public final class TransitionScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private final Scene next;
    private final float duration;
    private float t = 0f;
    private boolean switched = false;

    private final float W;
    private final float H;
    private static final float HALF = 0.5f;

    private LibGdxFont font;
    private final GlyphLayout layout = new GlyphLayout();

    // Dot animation state
    private int dotCount = 0;
    private float dotTimer = 0f;
    private static final float dotInterval = 0.4f;
    private static final String[] loadingFrames = { "Loading.", "Loading..", "Loading..." };

    public TransitionScene(EngineContext ctx, Scene next, float durationSeconds) {
        this.ctx = ctx;
        this.next = next;
        this.duration = Math.max(0.8f, durationSeconds); // minimum 0.8s for text to be readable
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
    }

    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                com.badlogic.gdx.Gdx.files.internal("Oswald-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 22;
        font = new LibGdxFont(generator.generateFont(params));
        generator.dispose();

        ctx.getIoManager().log("TransitionScene", "Transitioning to: " + next.getClass().getSimpleName());
    }

    @Override
    public void onUnload() {
        if (font != null)
            font.dispose();
    }

    @Override
    public void update(float dt) {
        t += dt;

        // Animate dots
        dotTimer += dt;
        if (dotTimer >= dotInterval) {
            dotTimer = 0f;
            dotCount = (dotCount + 1) % loadingFrames.length;
        }

        if (t >= duration && !switched) {
            switched = true;
            ctx.getSceneManager().switchTo(next);
        }
    }

    @Override
    public void render() {
        float progress = Math.min(1f, t / duration);
        float alpha;

        if (progress <= HALF) {
            // Fade to black
            alpha = Interpolation.fade.apply(progress / HALF);
        } else {
            // Fade from black
            alpha = 1f - Interpolation.fade.apply((progress - HALF) / HALF);
        }

        ctx.getRenderer().drawRect(0, 0, W, H, new EngineColor(0f, 0f, 0f, alpha));
    }

    @Override
    public void renderHud() {
        float progress = Math.min(1f, t / duration);

        // Only show loading text when screen is mostly black
        float visibility = 0f;
        if (progress > 0.3f && progress < 0.7f) {
            float mid = (progress - 0.3f) / 0.4f;
            visibility = mid <= 0.5f
                    ? Interpolation.fade.apply(mid / 0.5f)
                    : Interpolation.fade.apply((1f - mid) / 0.5f);
        }

        if (visibility <= 0.01f)
            return;

        // Loading text
        String text = loadingFrames[dotCount];
        font.setColor(new EngineColor(0.85f, 0.85f, 0.85f, visibility));
        layout.setText(font.bitmapFont, text);
        ctx.getRenderer().drawText(font, text, W / 2f - layout.width / 2f, H / 2f + layout.height / 2f);

        // Small progress bar at bottom
        float barW = W * 0.4f;
        float barH = 3f;
        float barX = W / 2f - barW / 2f;
        float barY = H / 2f - 30f;

        // Background track
        ctx.getRenderer().drawRect(barX, barY, barW, barH, new EngineColor(0.3f, 0.3f, 0.3f, visibility));
        // Filled portion
        ctx.getRenderer().drawRect(barX, barY, barW * progress, barH, new EngineColor(0.13f, 0.67f, 0.53f, visibility));
    }
}