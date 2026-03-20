package com.example.app.demo.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.example.app.demo.render.LibGdxFont;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.render.IRenderer;

public class TrainHud {

    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();

    private static final int MAX_LIVES = 3;

    private static final EngineColor COL_HUD_BG = new EngineColor(0.10f, 0.10f, 0.16f, 0.88f);
    private static final EngineColor COL_HEART_ON = new EngineColor(0.83f, 0.18f, 0.18f, 1f);
    private static final EngineColor COL_HEART_OFF = new EngineColor(0.35f, 0.35f, 0.40f, 1f);
    private static final EngineColor COL_TIMER = new EngineColor(0.96f, 0.77f, 0.09f, 1f);
    private static final EngineColor COL_PLAYER_HI = new EngineColor(0.13f, 0.67f, 0.53f, 1f);
    private static final EngineColor COL_LOST_TEXT = new EngineColor(0.95f, 0.95f, 0.95f, 1f);

    public void load() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Oswald-Regular.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 18;
        font = new LibGdxFont(generator.generateFont(smallParams));

        FreeTypeFontGenerator.FreeTypeFontParameter bigParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigParams.size = 72;
        bigFont = new LibGdxFont(generator.generateFont(bigParams));

        generator.dispose();
        font.setColor(EngineColor.WHITE);
    }

    public void dispose() {
        if (font != null)
            font.dispose();
        if (bigFont != null)
            bigFont.dispose();
    }

    public void render(IRenderer renderer, int level, float timeRemaining, int lives,
            boolean won, boolean lost, boolean gameStarted, float W, float H) {
        drawHudBar(renderer, W);
        drawLives(renderer, lives);
        drawHudText(renderer, level, timeRemaining, W);
        drawMidScreenMessage(renderer, won, lost, gameStarted, W, H);
        font.setColor(EngineColor.WHITE);
    }

    private void drawHudBar(IRenderer renderer, float W) {
        renderer.drawRect(0, 0, W, 42f, COL_HUD_BG);
    }

    private void drawLives(IRenderer renderer, int lives) {
        for (int i = 0; i < MAX_LIVES; i++) {
            renderer.drawCircle(20f + i * 22f, 19f, 9f, i < lives ? COL_HEART_ON : COL_HEART_OFF);
        }
    }

    private void drawHudText(IRenderer renderer, int level, float timeRemaining, float W) {
        font.setColor(EngineColor.WHITE);
        String levelStr = "LVL " + level;
        layout.setText(font.bitmapFont, levelStr);
        renderer.drawText(font, levelStr, W / 2f - layout.width / 2f, 28f);

        String timerStr = String.format("%.1fs", timeRemaining);
        layout.setText(font.bitmapFont, timerStr);
        font.setColor(COL_TIMER);
        renderer.drawText(font, timerStr, W - layout.width - 10f, 28f);
    }

    private void drawMidScreenMessage(IRenderer renderer, boolean won, boolean lost,
            boolean gameStarted, float W, float H) {
        float midY = H / 2f + 36f;

        if (!gameStarted) {
            String msg = "Avoid passengers, board the train!";
            layout.setText(font.bitmapFont, msg);
            font.setColor(EngineColor.BLACK);
            renderer.drawText(font, msg, W / 2f - layout.width / 2f, H / 2f + layout.height / 2f);

        } else if (won) {
            layout.setText(bigFont.bitmapFont, "BOARDED!");
            bigFont.setColor(COL_PLAYER_HI);
            renderer.drawText(bigFont, "BOARDED!", W / 2f - layout.width / 2f, midY + layout.height / 2f);

        } else if (lost) {
            layout.setText(bigFont.bitmapFont, "YOU LOST!");
            bigFont.setColor(COL_LOST_TEXT);
            renderer.drawText(bigFont, "YOU LOST!", W / 2f - layout.width / 2f, midY + layout.height / 2f);
        }

        font.setColor(EngineColor.WHITE);
    }
}