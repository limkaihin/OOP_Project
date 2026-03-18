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
 * Settings overlay — pushed on top of the current scene.
 * Press ESC (BACK) or click the Back button to pop back to the previous scene.
 */
public final class SettingsScene extends AbstractBaseScene {

    // ---- layout constants ----
    private static final float PANEL_W  = 420f;
    private static final float PANEL_H  = 340f;
    private static final float VOL_STEP = 0.05f;   // 5 % per click
    private static final float PAD      = 24f;

    // Button sizes — large enough to click comfortably
    private static final float TOG_W  = 90f;
    private static final float TOG_H  = 36f;
    private static final float ARR_W  = 40f;
    private static final float ARR_H  = 36f;

    // Row height (spacing between rows)
    private static final float ROW_H  = 58f;

    private final EngineContext ctx;
    private SpriteBatch batch;
    private BitmapFont  titleFont;
    private BitmapFont  font;

    // ---- computed coordinates (set in onLoad) ----
    private float panelX, panelY;

    // Right-hand column x for buttons
    private float btnCol;

    // Row y-baselines (bottom of the hit box for each row)
    private float row1Y, row2Y, row3Y, row4Y;

    // Back button
    private float backX, backY, backW, backH;

    // Arrow right x positions
    private float rightArrX;

    public SettingsScene(EngineContext ctx) {
        this.ctx = ctx;
    }

    // ---------------------------------------------------------------- lifecycle

    @Override
    public void onLoad() {
        batch     = new SpriteBatch();
        titleFont = new BitmapFont();
        font      = new BitmapFont();

        titleFont.getData().setScale(2.0f);
        titleFont.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        font.setColor(Color.WHITE);

        float W = ctx.config.width;
        float H = ctx.config.height;
        panelX = (W - PANEL_W) / 2f;
        panelY = (H - PANEL_H) / 2f;

        // Right column: where buttons start
        btnCol   = panelX + PANEL_W - TOG_W - PAD;
        rightArrX = btnCol + ARR_W + 50f;   // right arrow x (left arrow is btnCol)

        // Rows from top of panel downward (y = bottom edge of button)
        float topY = panelY + PANEL_H - 90f;
        row1Y = topY;
        row2Y = topY - ROW_H;
        row3Y = topY - ROW_H * 2f;
        row4Y = topY - ROW_H * 3f;

        // Back button — centred near bottom
        backW = 140f; backH = 40f;
        backX = panelX + (PANEL_W - backW) / 2f;
        backY = panelY + PAD;

        ctx.ioManager.log("SettingsScene", "Loaded");
    }

    @Override
    public void onUnload() {
        if (batch     != null) batch.dispose();
        if (titleFont != null) titleFont.dispose();
        if (font      != null) font.dispose();
    }

    // ---------------------------------------------------------------- update

    @Override
    public void update(float dt) {
        // Keyboard back
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.BACK)) {
            ctx.sceneManager.pop();
            return;
        }

        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        // LibGDX mouse Y is top-down; convert to bottom-up to match our coordinate system
        float mx = Gdx.input.getX();
        float my = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Music toggle
        if (hit(mx, my, btnCol, row1Y, TOG_W, TOG_H)) toggleMusic();

        // SFX toggle
        if (hit(mx, my, btnCol, row2Y, TOG_W, TOG_H)) toggleSfx();

        // Music volume < >
        if (hit(mx, my, btnCol,    row3Y, ARR_W, ARR_H)) adjustMusicVol(-VOL_STEP);
        if (hit(mx, my, rightArrX, row3Y, ARR_W, ARR_H)) adjustMusicVol(+VOL_STEP);

        // SFX volume < >
        if (hit(mx, my, btnCol,    row4Y, ARR_W, ARR_H)) adjustSfxVol(-VOL_STEP);
        if (hit(mx, my, rightArrX, row4Y, ARR_W, ARR_H)) adjustSfxVol(+VOL_STEP);

        // Back button
        if (hit(mx, my, backX, backY, backW, backH)) ctx.sceneManager.pop();
    }

    // ---------------------------------------------------------------- render  (ShapeRenderer)

    @Override
    public void render() {
        // Dim overlay
        ctx.renderer.drawRect(0, 0, ctx.config.width, ctx.config.height,
                new Color(0f, 0f, 0f, 0.58f));

        // Panel
        ctx.renderer.drawRect(panelX, panelY, PANEL_W, PANEL_H,
                new Color(0.10f, 0.10f, 0.16f, 1f));
        // Top accent bar
        ctx.renderer.drawRect(panelX, panelY + PANEL_H - 4f, PANEL_W, 4f,
                new Color(0.4f, 0.6f, 1.0f, 1f));

        // Toggle buttons (Music, SFX)
        ctx.renderer.drawRect(btnCol, row1Y, TOG_W, TOG_H,
                ctx.ioManager.isMusicEnabled() ? new Color(0.12f, 0.48f, 0.18f, 1f) : new Color(0.50f, 0.12f, 0.12f, 1f));
        ctx.renderer.drawRect(btnCol, row2Y, TOG_W, TOG_H,
                ctx.ioManager.isSfxEnabled() ? new Color(0.12f, 0.48f, 0.18f, 1f) : new Color(0.50f, 0.12f, 0.12f, 1f));

        // Arrow buttons (Music vol)
        ctx.renderer.drawRect(btnCol,    row3Y, ARR_W, ARR_H, new Color(0.22f, 0.32f, 0.52f, 1f));
        ctx.renderer.drawRect(rightArrX, row3Y, ARR_W, ARR_H, new Color(0.22f, 0.32f, 0.52f, 1f));
        // Arrow buttons (SFX vol)
        ctx.renderer.drawRect(btnCol,    row4Y, ARR_W, ARR_H, new Color(0.22f, 0.32f, 0.52f, 1f));
        ctx.renderer.drawRect(rightArrX, row4Y, ARR_W, ARR_H, new Color(0.22f, 0.32f, 0.52f, 1f));

        // Back button
        ctx.renderer.drawRect(backX, backY, backW, backH, new Color(0.22f, 0.22f, 0.32f, 1f));
    }

    // ---------------------------------------------------------------- renderHud (SpriteBatch / BitmapFont)

    @Override
    public void renderHud() {
        batch.begin();

        // Title
        titleFont.setColor(new Color(0.75f, 0.88f, 1.0f, 1f));
        titleFont.draw(batch, "SETTINGS", panelX + 130f, panelY + PANEL_H - 12f);

        float labelX   = panelX + PAD;
        // Text Y: BitmapFont draws from *top* of the glyph — centre it inside TOG_H
        float textOff  = TOG_H - 8f;   // vertical offset to sit nicely inside the button

        // ---- Row labels ----
        font.setColor(Color.WHITE);
        font.draw(batch, "Music",     labelX, row1Y + textOff);
        font.draw(batch, "Sound FX",  labelX, row2Y + textOff);
        font.draw(batch, "Music Vol", labelX, row3Y + textOff);
        font.draw(batch, "SFX Vol",   labelX, row4Y + textOff);

        // ---- Toggle labels ----
        boolean musicOn = ctx.ioManager.isMusicEnabled();
        boolean sfxOn   = ctx.ioManager.isSfxEnabled();

        font.setColor(musicOn ? new Color(0.3f, 1f, 0.55f, 1f) : new Color(1f, 0.38f, 0.38f, 1f));
        font.draw(batch, musicOn ? "  ON" : " OFF", btnCol + 10f, row1Y + textOff);

        font.setColor(sfxOn ? new Color(0.3f, 1f, 0.55f, 1f) : new Color(1f, 0.38f, 0.38f, 1f));
        font.draw(batch, sfxOn ? "  ON" : " OFF", btnCol + 10f, row2Y + textOff);

        // ---- Arrow labels ----
        font.setColor(Color.WHITE);
        font.draw(batch, "  <", btnCol + 4f,     row3Y + textOff);
        font.draw(batch, "  >", rightArrX + 4f,  row3Y + textOff);
        font.draw(batch, "  <", btnCol + 4f,     row4Y + textOff);
        font.draw(batch, "  >", rightArrX + 4f,  row4Y + textOff);

        // ---- Volume percentages (between arrows) ----
        font.setColor(new Color(0.95f, 0.82f, 0.25f, 1f));
        int mVol = Math.round(ctx.ioManager.getMusicVolume() * 100);
        int sVol = Math.round(ctx.ioManager.getSfxVolume()   * 100);
        font.draw(batch, mVol + "%", btnCol + ARR_W + 8f, row3Y + textOff);
        font.draw(batch, sVol + "%", btnCol + ARR_W + 8f, row4Y + textOff);

        // ---- Back button label ----
        font.setColor(new Color(0.85f, 0.85f, 0.95f, 1f));
        font.draw(batch, "  Back  (ESC)", backX + 8f, backY + backH - 6f);

        batch.end();
    }

    // ---------------------------------------------------------------- helpers

    /** Returns true if the point (mx, my) is inside the rectangle. */
    private static boolean hit(float mx, float my, float bx, float by, float bw, float bh) {
        return mx >= bx && mx <= bx + bw && my >= by && my <= by + bh;
    }

    private void toggleMusic() {
        ctx.ioManager.setMusicEnabled(!ctx.ioManager.isMusicEnabled());
    }

    private void toggleSfx() {
        ctx.ioManager.setSfxEnabled(!ctx.ioManager.isSfxEnabled());
    }

    private void adjustMusicVol(float delta) {
        ctx.ioManager.setMusicVolume(
                Math.max(0f, Math.min(1f, ctx.ioManager.getMusicVolume() + delta)));
    }

    private void adjustSfxVol(float delta) {
        ctx.ioManager.setSfxVolume(
                Math.max(0f, Math.min(1f, ctx.ioManager.getSfxVolume() + delta)));
    }
}
