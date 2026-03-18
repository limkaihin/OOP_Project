package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import com.example.app.demo.render.LibGdxFont;

import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.scene.AbstractBaseScene;

public final class LevelSelectScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float W, H;
    private float startX, startY;
    private final float boxSize = 80f;
    private final float gapX = 20f;
    private final float gapY = 30f;
    private final int totalLevels = 5;

    private static final EngineColor COL_BG = new EngineColor(0.06f, 0.06f, 0.10f, 1f);
    private static final EngineColor COL_UNLOCKED  = new EngineColor(0.20f, 0.55f, 0.30f, 1f);
    private static final EngineColor COL_LOCKED = new EngineColor(0.30f, 0.30f, 0.35f, 1f);
    private static final EngineColor COL_LOCKED_TEXT  = new EngineColor(0.55f, 0.55f, 0.60f, 1f);

    public LevelSelectScene(EngineContext ctx) {
        this.ctx = ctx;
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
        float totalWidth = totalLevels * boxSize + (totalLevels - 1) * gapX;
        this.startX = (W - totalWidth) / 2f;
        this.startY = H / 2f + 20f;
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
        font.setColor(EngineColor.WHITE);
        ctx.getIoManager().log("LevelSelectScene", "Loaded (Max unlocked: " + GameProgress.maxUnlockedLevel + ")");
    }

    @Override
    public void update(float dt) {
        if (!ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.ACTION_1)) return;

        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;
            if (mx >= bx && mx <= bx + boxSize && my >= by && my <= by + boxSize) {
                if (i + 1 <= GameProgress.maxUnlockedLevel) {
                    ctx.getSceneManager().switchTo(new TransitionScene(ctx, new TrainScene(ctx, i + 1), 1.5f));
                } else {
                    ctx.getIoManager().playSound("hit.wav");
                }
            }
        }
    }

    @Override
    public void render() {
        int mx = Gdx.input.getX();
        int my = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Background
        ctx.getRenderer().drawRect(0, 0, W, H, COL_BG);

        // Level boxes
        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;
            boolean isHovered = mx >= bx && mx <= bx + boxSize && my >= by && my <= by + boxSize;
            boolean isUnlocked = i + 1 <= GameProgress.maxUnlockedLevel;

            float size = (isHovered && isUnlocked) ? boxSize * 1.1f : boxSize;
            float offset = (size - boxSize) / 2f;

            EngineColor boxColor = isUnlocked ? COL_UNLOCKED : COL_LOCKED;

            ctx.getRenderer().drawRect(bx - offset, by - offset, size, size, boxColor);
        }
    }

    @Override
    public void renderHud() {
        // Title
        bigFont.setColor(EngineColor.WHITE);
        layout.setText(bigFont.bitmapFont, "SELECT LEVEL");
        ctx.getRenderer().drawText(bigFont, "SELECT LEVEL", W / 2f - layout.width / 2f, startY + boxSize + 80f);

        // Level numbers inside boxes
        for (int i = 0; i < totalLevels; i++) {
            float bx = startX + i * (boxSize + gapX);
            float by = startY;

            String text = "LV " + (i + 1);
            font.setColor(i + 1 <= GameProgress.maxUnlockedLevel ? EngineColor.WHITE : COL_LOCKED_TEXT);
            layout.setText(font.bitmapFont, text);
            ctx.getRenderer().drawText(font, text, bx + boxSize / 2f - layout.width / 2f, by + boxSize / 2f + layout.height / 2f);
        }

        // Locked hint
        font.setColor(COL_LOCKED_TEXT);
        layout.setText(font.bitmapFont, "Grey levels are locked");
        ctx.getRenderer().drawText(font, "Grey levels are locked", W / 2f - layout.width / 2f, startY - 40f);

        font.setColor(EngineColor.WHITE);
    }

    @Override
    public void onUnload() {
        if (font != null) font.dispose();
        if (bigFont != null) bigFont.dispose();
    }
}