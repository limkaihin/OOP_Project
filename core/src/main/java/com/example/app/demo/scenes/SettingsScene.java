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

public final class SettingsScene extends AbstractBaseScene {

    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont titleFont;
    private final GlyphLayout layout = new GlyphLayout();

    private final float W, H;

    private final float panelX, panelY;
    private final float btnCol, rightArrX;
    private final float row1Y, row2Y, row3Y, row4Y;
    private final float backX, backY, backW, backH;

    private static final float PANEL_W = 420f;
    private static final float PANEL_H = 340f;
    private static final float VOL_STEP = 0.05f;
    private static final float PAD = 4f;
    private static final float TOG_W = 90f;
    private static final float TOG_H = 36f;
    private static final float ARR_W = 40f;
    private static final float ARR_H = 36f;
    private static final float ROW_H = 58f;

    private static final EngineColor COL_OVERLAY = new EngineColor(0f, 0f, 0f, 0.75f);
    private static final EngineColor COL_PANEL = new EngineColor(0.12f, 0.13f, 0.18f, 1f);
    private static final EngineColor COL_BTN_OFF = new EngineColor(0.60f, 0.20f, 0.20f, 1f);
    private static final EngineColor COL_BTN_ON = new EngineColor(0.20f, 0.60f, 0.30f, 1f);
    private static final EngineColor COL_BTN_NAV = new EngineColor(0.25f, 0.35f, 0.55f, 1f);
    private static final EngineColor COL_BTN_BCK = new EngineColor(0.22f, 0.22f, 0.32f, 1f);
    private static final EngineColor COL_BTN_OFF_HOVER = new EngineColor(0.68f, 0.36f, 0.36f, 1f);
    private static final EngineColor COL_BTN_ON_HOVER  = new EngineColor(0.36f, 0.68f, 0.44f, 1f);
    private static final EngineColor COL_BTN_NAV_HOVER = new EngineColor(0.40f, 0.48f, 0.64f, 1f);
    private static final EngineColor COL_BTN_BCK_HOVER = new EngineColor(0.38f, 0.38f, 0.46f, 1f);
    private static final EngineColor COL_VOL = new EngineColor(0.95f, 0.85f, 0.45f, 1f);

    public SettingsScene(EngineContext ctx) {
        this.ctx = ctx;
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
        this.panelX = (W - PANEL_W) / 2f;
        this.panelY = (H - PANEL_H) / 2f;
        this.btnCol = panelX + PANEL_W - TOG_W - PAD;
        this.rightArrX = btnCol + ARR_W + 50f;
        float topY = panelY + PANEL_H - 90f;
        this.row1Y = topY;
        this.row2Y = topY - ROW_H;
        this.row3Y = topY - ROW_H * 2f;
        this.row4Y = topY - ROW_H * 3f;
        this.backW = 160f;
        this.backH = 40f;
        this.backX = panelX + (PANEL_W - backW) / 2f;
        this.backY = panelY + PAD;
    }

    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter titleParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        titleParams.size = 28;
        titleFont = new LibGdxFont(generator.generateFont(titleParams));

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 16;
        font = new LibGdxFont(generator.generateFont(smallParams));

        generator.dispose();

        ctx.getIoManager().log("SettingsScene", "Loaded");
    }

    @Override
    public void onUnload() {
        if (titleFont != null) titleFont.dispose();
        if (font != null) font.dispose();
    }

    @Override
    public void update(float dt) {
        if (ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.BACK)) {
            ctx.getSceneManager().pop();
            return;
        }

        if (!ctx.getIoManager().getInputHandler().getState().isJustPressed(InputAction.ACTION_1)) return;

        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        if (hit(mx, my, btnCol, row1Y, TOG_W, TOG_H)) toggleMusic();
        if (hit(mx, my, btnCol, row2Y, TOG_W, TOG_H)) toggleSfx();
        if (hit(mx, my, btnCol, row3Y, ARR_W, ARR_H)) adjustMusicVol(-VOL_STEP);
        if (hit(mx, my, rightArrX, row3Y, ARR_W, ARR_H)) adjustMusicVol(+VOL_STEP);
        if (hit(mx, my, btnCol, row4Y, ARR_W, ARR_H)) adjustSfxVol(-VOL_STEP);
        if (hit(mx, my, rightArrX, row4Y, ARR_W, ARR_H)) adjustSfxVol(+VOL_STEP);
        if (hit(mx, my, backX, backY, backW, backH)) ctx.getSceneManager().pop();
    }

    @Override
    public void render() {
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        ctx.getRenderer().drawRect(0, 0, W, H, COL_OVERLAY);
        ctx.getRenderer().drawRect(panelX, panelY, PANEL_W, PANEL_H, COL_PANEL);

        boolean musHover = hit(mx, my, btnCol, row1Y, TOG_W, TOG_H);
        ctx.getRenderer().drawRect(btnCol, row1Y, TOG_W, TOG_H,
            ctx.getIoManager().isMusicEnabled()
                ? (musHover ? COL_BTN_ON_HOVER : COL_BTN_ON)
                : (musHover ? COL_BTN_OFF_HOVER : COL_BTN_OFF));

        boolean sfxHover = hit(mx, my, btnCol, row2Y, TOG_W, TOG_H);
        ctx.getRenderer().drawRect(btnCol, row2Y, TOG_W, TOG_H,
            ctx.getIoManager().isSfxEnabled()
                ? (sfxHover ? COL_BTN_ON_HOVER : COL_BTN_ON)
                : (sfxHover ? COL_BTN_OFF_HOVER : COL_BTN_OFF));

        ctx.getRenderer().drawRect(btnCol, row3Y, ARR_W, ARR_H, hit(mx, my, btnCol, row3Y, ARR_W, ARR_H) ? COL_BTN_NAV_HOVER : COL_BTN_NAV);
        ctx.getRenderer().drawRect(rightArrX, row3Y, ARR_W, ARR_H, hit(mx, my, rightArrX, row3Y, ARR_W, ARR_H) ? COL_BTN_NAV_HOVER : COL_BTN_NAV);
        ctx.getRenderer().drawRect(btnCol, row4Y, ARR_W, ARR_H, hit(mx, my, btnCol, row4Y, ARR_W, ARR_H) ? COL_BTN_NAV_HOVER : COL_BTN_NAV);
        ctx.getRenderer().drawRect(rightArrX, row4Y, ARR_W, ARR_H, hit(mx, my, rightArrX, row4Y, ARR_W, ARR_H) ? COL_BTN_NAV_HOVER : COL_BTN_NAV);
        ctx.getRenderer().drawRect(backX, backY, backW, backH, hit(mx, my, backX, backY, backW, backH) ? COL_BTN_BCK_HOVER : COL_BTN_BCK);
    }

    @Override
    public void renderHud() {
        // Title
        titleFont.setColor(EngineColor.WHITE);
        layout.setText(titleFont.bitmapFont, "SETTINGS");
        ctx.getRenderer().drawText(titleFont, "SETTINGS", panelX + (PANEL_W - layout.width) / 2f, panelY + PANEL_H + 15f);

        float labelX = panelX + PAD;
        font.setColor(EngineColor.LIGHT_GRAY);
        drawCenteredY("Music", labelX, row1Y, TOG_H);
        drawCenteredY("Sound FX", labelX, row2Y, TOG_H);
        drawCenteredY("Music Vol", labelX, row3Y, ARR_H);
        drawCenteredY("SFX Vol", labelX, row4Y, ARR_H);

        font.setColor(EngineColor.WHITE);
        drawCenteredInBox(ctx.getIoManager().isMusicEnabled() ? "ON" : "OFF", btnCol, row1Y, TOG_W, TOG_H);
        drawCenteredInBox(ctx.getIoManager().isSfxEnabled() ? "ON" : "OFF", btnCol, row2Y, TOG_W, TOG_H);
        drawCenteredInBox("<", btnCol, row3Y, ARR_W, ARR_H);
        drawCenteredInBox(">", rightArrX, row3Y, ARR_W, ARR_H);
        drawCenteredInBox("<", btnCol, row4Y, ARR_W, ARR_H);
        drawCenteredInBox(">", rightArrX, row4Y, ARR_W, ARR_H);

        font.setColor(COL_VOL);
        int mVol = Math.round(ctx.getIoManager().getMusicVolume() * 100);
        int sVol = Math.round(ctx.getIoManager().getSfxVolume()   * 100);
        float midGapW = rightArrX - (btnCol + ARR_W);
        drawCenteredInBox(mVol + "%", btnCol + ARR_W, row3Y, midGapW, ARR_H);
        drawCenteredInBox(sVol + "%", btnCol + ARR_W, row4Y, midGapW, ARR_H);

        font.setColor(EngineColor.WHITE);
        drawCenteredInBox("Back (ESC)", backX, backY, backW, backH);
    }

    private void drawCenteredY(String text, float x, float boxY, float boxH) {
        layout.setText(font.bitmapFont, text);
        ctx.getRenderer().drawText(font, text, x, boxY + (boxH + layout.height) / 2f);
    }

    private void drawCenteredInBox(String text, float boxX, float boxY, float boxW, float boxH) {
        layout.setText(font.bitmapFont, text);
        float textX = boxX + (boxW - layout.width) / 2f;
        float textY = boxY + (boxH + layout.height) / 2f;
        ctx.getRenderer().drawText(font, text, textX, textY);
    }

    private static boolean hit(float mx, float my, float bx, float by, float bw, float bh) {
        return mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
    }

    private void toggleMusic() { ctx.getIoManager().setMusicEnabled(!ctx.getIoManager().isMusicEnabled()); }
    private void toggleSfx() { ctx.getIoManager().setSfxEnabled(!ctx.getIoManager().isSfxEnabled()); }
    private void adjustMusicVol(float d) { ctx.getIoManager().setMusicVolume(Math.max(0f, Math.min(1f, ctx.getIoManager().getMusicVolume() + d))); }
    private void adjustSfxVol(float d) { ctx.getIoManager().setSfxVolume(Math.max(0f, Math.min(1f, ctx.getIoManager().getSfxVolume() + d))); }
}