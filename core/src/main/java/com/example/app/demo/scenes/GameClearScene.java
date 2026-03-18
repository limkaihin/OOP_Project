package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.example.app.demo.render.LibGdxFont;

import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.scene.AbstractBaseScene;

public final class GameClearScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float W, H;
    private static final float bannerH = 90f;
    private final float btnX, btnY, btnW, btnH, bannerY;

    // Static colors for performance
    private static final EngineColor COL_BG = new EngineColor(0.04f, 0.04f, 0.10f, 1f);
    private static final EngineColor COL_BTN = new EngineColor(0.18f, 0.45f, 0.22f, 1f);
    private static final EngineColor COL_SUBTEXT = new EngineColor(0.85f, 0.85f, 0.95f, 1f);
    private static final EngineColor COL_HINT = new EngineColor(0.6f, 0.6f, 0.7f, 1f);
    private static final EngineColor COL_PANEL  = new EngineColor(0.05f, 0.12f, 0.08f, 0.92f);
    private static final EngineColor COL_ACCENT = new EngineColor(0.13f, 0.67f, 0.53f, 1f);
    private static final EngineColor COL_WINTEXT  = new EngineColor(0.13f, 0.77f, 0.53f, 1f);

    private float pulse = 0f;

    public GameClearScene(EngineContext ctx) {
        this.ctx = ctx;
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
        this.bannerY = H / 2f + 100f;
        this.btnW = 240f;
        this.btnH = 54f;
        this.btnX = (W - btnW) / 2f;
        this.btnY = H / 2f - 100f;
    }

    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));
        
        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 18;
        font = new LibGdxFont(generator.generateFont(smallParams));

        FreeTypeFontGenerator.FreeTypeFontParameter bigParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigParams.size = 72;
        bigFont = new LibGdxFont(generator.generateFont(bigParams));

        generator.dispose();

        ctx.getIoManager().log("GameClearScene", "Loaded");
    }

    @Override
    public void onUnload() {
        if (bigFont != null) bigFont.dispose();
        if (font != null) font.dispose();
    }

    @Override
    public void update(float dt) {
        pulse += dt;

        if (ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.BACK)) {
            Gdx.app.exit();
            return;
        }

        if (ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.ACTION_1)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                restartGame();
            }
        }
    }

    @Override
    public void render() {
        ctx.getRenderer().drawRect(0, 0, W, H, COL_BG);

        // Dark panel
        ctx.getRenderer().drawRect(0, bannerY, W, bannerH, COL_PANEL);
        // Green accent line at top of panel
        ctx.getRenderer().drawRect(0, bannerY + bannerH - 3f, W, 3f, COL_ACCENT);
        // Green accent line at bottom
        ctx.getRenderer().drawRect(0, bannerY, W, 3f, COL_ACCENT);

        // Restart button background
        ctx.getRenderer().drawRect(btnX, btnY, btnW, btnH, COL_BTN);
    }

    @Override
    public void renderHud() {
        float bannerCenter = bannerY + bannerH / 2f;

        // Title
        bigFont.setColor(COL_WINTEXT);
        layout.setText(bigFont.bitmapFont, "YOU WIN!");
        ctx.getRenderer().drawText(bigFont, "YOU WIN!", W / 2f - layout.width / 2f, bannerCenter + layout.height / 2f);

        // Text
        font.setColor(COL_SUBTEXT);
        layout.setText(font.bitmapFont, "You beat all 5 levels!");
        ctx.getRenderer().drawText(font, "You beat all 5 levels!", W / 2f - layout.width / 2f, H / 2f + 50f);

        // Restart button
        font.setColor(EngineColor.WHITE);
        drawCenteredInBox("Click to Restart", btnX, btnY, btnW, btnH);

        // ESC hint
        font.setColor(COL_HINT);
        layout.setText(font.bitmapFont, "Press ESC to Quit");
        ctx.getRenderer().drawText(font, "Press ESC to Quit", W / 2f - layout.width / 2f, btnY - 30f);
        font.setColor(EngineColor.WHITE);
    }

    private void drawCenteredInBox(String text, float boxX, float boxY, float boxW, float boxH) {
        layout.setText(font.bitmapFont, text);
        float textX = boxX + (boxW - layout.width) / 2f;
        // Adding layout.height ensures the baseline is adjusted for vertical centering
        float textY = boxY + (boxH + layout.height) / 2f;
        ctx.getRenderer().drawText(font, text, textX, textY);
    }

    private void restartGame() {
        GameProgress.reset();
        while (ctx.getSceneManager().size() > 1) {
            ctx.getSceneManager().pop();
        }
        ctx.getSceneManager().switchTo(new MenuScene(ctx));
        ctx.getIoManager().playMusic("music1.mp3");
    }
}