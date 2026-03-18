package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;

import com.example.app.demo.render.LibGdxFont;

import com.example.app.engine.EngineContext;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.io.InputAction;

public final class LevelSelectScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float boxSize = 80f;
    private final float gapX = 20f;
    private final float gapY = 30f;
    public static int maxUnlockedLevel = 1;
    private final int totalLevels = 5;

    public LevelSelectScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 18;
        font = new LibGdxFont(generator.generateFont(smallParams));

        FreeTypeFontGenerator.FreeTypeFontParameter bigParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigParams.size = 48;
        bigFont = new LibGdxFont(generator.generateFont(bigParams));

        generator.dispose();
        font.setColor(Color.WHITE);
        ctx.ioManager.log("LevelSelectScene", "Loaded (Max unlocked: " + maxUnlockedLevel + ")");
    }

    @Override
    public void update(float dt) {
        if (!ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.ACTION_1)) return;

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        float totalWidth = totalLevels * boxSize + (totalLevels - 1) * gapX;
        float startX = (ctx.config.width - totalWidth) / 2f;
        float startY = ctx.config.height / 2f + 20f;

        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;
            if (mx >= bx && mx <= bx + boxSize && my >= by && my <= by + boxSize) {
                if (i + 1 <= maxUnlockedLevel) {
                    ctx.sceneManager.switchTo(new TransitionScene(ctx, new TrainScene(ctx, i + 1), 1.5f));
                } else {
                    ctx.ioManager.playSound("hit.wav");
                }
            }
        }
    }

    @Override
    public void render() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        float totalWidth = totalLevels * boxSize + (totalLevels - 1) * gapX;
        float startX = (W - totalWidth) / 2f;
        float startY = H / 2f + 20f;

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Background
        ctx.renderer.drawRect(0, 0, W, H, new Color(0.06f, 0.06f, 0.10f, 1f));

        // Level boxes
        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;
            boolean isHovered = mx >= bx && mx <= bx + boxSize && my >= by && my <= by + boxSize;
            boolean isUnlocked = i + 1 <= maxUnlockedLevel;

            float size = (isHovered && isUnlocked) ? boxSize * 1.1f : boxSize;
            float offset = (size - boxSize) / 2f;

            Color boxColor = isUnlocked ? new Color(0.20f, 0.55f, 0.30f, 1f) : new Color(0.30f, 0.30f, 0.35f, 1f);

            ctx.renderer.drawRect(bx - offset, by - offset, size, size, boxColor);
        }
    }

    @Override
    public void renderHud() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        float totalWidth = totalLevels * boxSize + (totalLevels - 1) * gapX;
        float startX = (W - totalWidth) / 2f;
        float startY = H / 2f + 20f;

        // Title
        bigFont.setColor(Color.WHITE);
        layout.setText(bigFont.bitmapFont, "SELECT LEVEL");
        ctx.renderer.drawText(bigFont, "SELECT LEVEL", W / 2f - layout.width / 2f, startY + boxSize + 80f);

        // Level numbers inside boxes
        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;

            String text = "LV " + (i + 1);
            font.setColor(i + 1 <= maxUnlockedLevel ? Color.WHITE : new Color(0.55f, 0.55f, 0.60f, 1f));
            layout.setText(font.bitmapFont, text);
            ctx.renderer.drawText(font, text, bx + boxSize / 2f - layout.width / 2f, by + boxSize / 2f + layout.height / 2f);
        }

        // Locked hint
        font.setColor(new Color(0.55f, 0.55f, 0.60f, 1f));
        layout.setText(font.bitmapFont, "Grey levels are locked");
        ctx.renderer.drawText(font, "Grey levels are locked", W / 2f - layout.width / 2f, startY - 40f);

        font.setColor(Color.WHITE);
    }

    @Override
    public void onUnload() {
        if (font != null) font.dispose();
        if (bigFont != null) bigFont.dispose();
    }
}