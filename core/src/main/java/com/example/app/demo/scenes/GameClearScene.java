package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import com.example.app.demo.render.LibGdxFont;
import com.example.app.engine.EngineContext;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.scene.AbstractBaseScene;

public final class GameClearScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float W;
    private final float H;
    private float btnX, btnY, btnW, btnH, bannerH, bannerY;

    // Static colors for performance (no GC churn)
    private static final Color COL_BG = new Color(0.04f, 0.04f, 0.10f, 1f);
    private static final Color COL_BTN = new Color(0.18f, 0.45f, 0.22f, 1f);
    private static final Color COL_SUBTEXT = new Color(0.85f, 0.85f, 0.95f, 1f);
    private static final Color COL_HINT = new Color(0.6f, 0.6f, 0.7f, 1f);

    private float pulse = 0f;

    public GameClearScene(EngineContext ctx) {
        this.ctx = ctx;
        this.W = ctx.config.width;
        this.H = ctx.config.height;
        this.bannerY = H / 2f + 100f;
        this.btnW = 240f;
        this.btnH = 54f;
        this.btnX = (W - btnW) / 2f;
        this.btnY = H / 2f - 100f;
        this.bannerH = 90f;
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

        float W = ctx.config.width;
        float H = ctx.config.height;
        btnW = 240f;
        btnH = 54f;
        btnX = (W - btnW) / 2f;
        btnY = H / 2f - 100f;

        ctx.ioManager.log("GameClearScene", "Loaded");
    }

    @Override
    public void onUnload() {
        if (bigFont != null) bigFont.dispose();
        if (font != null) font.dispose();
    }

    @Override
    public void update(float dt) {
        pulse += dt;

        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.BACK)) {
            Gdx.app.exit();
            return;
        }

        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.ACTION_1)) {
            float mx = Gdx.input.getX();
            float my = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (mx >= btnX && mx <= btnX + btnW && my >= btnY && my <= btnY + btnH) {
                restartGame();
            }
        }
    }

    @Override
    public void render() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        ctx.renderer.drawRect(0, 0, W, H, COL_BG);

        // Dark panel
        ctx.renderer.drawRect(0, bannerY, W, bannerH, new Color(0.05f, 0.12f, 0.08f, 0.92f));
        // Green accent line at top of panel
        ctx.renderer.drawRect(0, bannerY + bannerH - 3f, W, 3f, new Color(0.13f, 0.67f, 0.53f, 1f));
        // Green accent line at bottom
        ctx.renderer.drawRect(0, bannerY, W, 3f, new Color(0.13f, 0.67f, 0.53f, 1f));

        // Restart button background
        ctx.renderer.drawRect(btnX, btnY, btnW, btnH, COL_BTN);
    }

    @Override
    public void renderHud() {
        float W = ctx.config.width;
        float H = ctx.config.height;
        float bannerCenter = bannerY + bannerH / 2f;

        // Title
        bigFont.setColor(new Color(0.13f, 0.77f, 0.53f, 1f));
        layout.setText(bigFont.bitmapFont, "YOU WIN!");
        ctx.renderer.drawText(bigFont, "YOU WIN!", W / 2f - layout.width / 2f, bannerCenter + layout.height / 2f);

        // Text
        font.setColor(COL_SUBTEXT);
        layout.setText(font.bitmapFont, "You beat all 5 levels!");
        ctx.renderer.drawText(font, "You beat all 5 levels!", W / 2f - layout.width / 2f, H / 2f + 50f);

        // Restart button
        font.setColor(Color.WHITE);
        drawCenteredInBox("Click to Restart", btnX, btnY, btnW, btnH);

        // ESC hint
        font.setColor(COL_HINT);
        layout.setText(font.bitmapFont, "Press ESC to Quit");
        ctx.renderer.drawText(font, "Press ESC to Quit", W / 2f - layout.width / 2f, btnY - 30f);
        font.setColor(Color.WHITE);
    }

    private void drawCenteredInBox(String text, float boxX, float boxY, float boxW, float boxH) {
        layout.setText(font.bitmapFont, text);
        float textX = boxX + (boxW - layout.width) / 2f;
        // Adding layout.height ensures the baseline is adjusted for vertical centering
        float textY = boxY + (boxH + layout.height) / 2f;
        ctx.renderer.drawText(font, text, textX, textY);
    }

    private void restartGame() {
        LevelSelectScene.maxUnlockedLevel = 1;
        while (ctx.sceneManager.size() > 1) {
            ctx.sceneManager.pop();
        }
        ctx.sceneManager.switchTo(new MenuScene(ctx));
        ctx.ioManager.playMusic("music1.mp3");
    }
}