package com.example.app.demo.game;

import com.example.app.demo.game.TrainLayout;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.render.IRenderer;

public class TrainRenderer {

    private final IRenderer renderer;

    // Train colours
    private static final EngineColor COL_TRAIN_BODY = new EngineColor(0.95f, 0.94f, 0.92f, 1f);
    private static final EngineColor COL_TRAIN_PANEL = new EngineColor(0.12f, 0.12f, 0.14f, 1f);
    private static final EngineColor COL_TRAIN_STRIPE = new EngineColor(0.83f, 0.15f, 0.15f, 1f);
    private static final EngineColor COL_TRAIN_SHADOW = new EngineColor(0.50f, 0.50f, 0.52f, 1f);
    private static final EngineColor COL_WIN_BORDER = new EngineColor(0.15f, 0.15f, 0.18f, 1f);
    private static final EngineColor COL_WIN_GLASS = new EngineColor(0.35f, 0.55f, 0.65f, 1f);
    private static final EngineColor COL_WIN_SHINE = new EngineColor(0.60f, 0.78f, 0.88f, 0.7f);
    private static final EngineColor COL_DOOR_BG = new EngineColor(0.73f, 0.73f, 0.75f, 1f);
    private static final EngineColor COL_DOOR_PANEL = new EngineColor(0.82f, 0.82f, 0.84f, 1f);
    private static final EngineColor COL_DOOR_LINE = new EngineColor(0.75f, 0.75f, 0.77f, 1f);
    private static final EngineColor COL_DOOR_FRAME = new EngineColor(0.53f, 0.53f, 0.56f, 1f);
    private static final EngineColor COL_DOOR_SILL = new EngineColor(0.96f, 0.77f, 0.09f, 1f);
    private static final EngineColor COL_TACTILE = new EngineColor(0.94f, 0.75f, 0.06f, 1f);
    private static final EngineColor COL_BUMP = new EngineColor(0.83f, 0.65f, 0.04f, 1f);
    private static final EngineColor COL_MARKER = new EngineColor(0.83f, 0.18f, 0.18f, 0.7f);

    // Platform colours
    private static final EngineColor COL_PLATFORM = new EngineColor(0.83f, 0.81f, 0.78f, 1f);
    private static final EngineColor COL_GRID = new EngineColor(0.75f, 0.73f, 0.70f, 1f);

    // Overlay
    private static final EngineColor COL_ENDGAME_OVERLAY = new EngineColor(0.15f, 0.15f, 0.15f, 0.65f);

    public TrainRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    public void render(float W, float H, float doorOpenAmount, boolean lost) {
        drawPlatform(W, H);
        drawTrain(W, H, doorOpenAmount);
        if (lost)
            renderer.drawRect(0, 0, W, H, COL_ENDGAME_OVERLAY);
    }

    private void drawPlatform(float W, float H) {
        renderer.drawRect(0, 0, W, H, COL_PLATFORM);
        for (float y = 80f; y < H - TrainLayout.DOOR_Y; y += 80f)
            renderer.drawLine(0, y, W, y, COL_GRID);
        for (float x = 80f; x < W; x += 80f)
            renderer.drawLine(x, 0, x, H - TrainLayout.DOOR_Y, COL_GRID);
    }

    private void drawTrain(float W, float H, float doorOpenAmount) {
        float trainY = H - TrainLayout.DOOR_Y;
        renderer.drawRect(0, trainY, W, TrainLayout.DOOR_Y, COL_TRAIN_BODY);
        renderer.drawRect(0, trainY, W, 28f, COL_TRAIN_PANEL);
        renderer.drawRect(0, trainY + 28f, W, 12f, COL_TRAIN_STRIPE);
        renderer.drawRect(0, trainY, W, 4f, COL_TRAIN_SHADOW);

        drawWindows(H);
        drawDoor(trainY, doorOpenAmount);
        drawTactileStrip(W, H);
        drawWaitingMarkers(H);
    }

    private void drawWindows(float H) {
        float[] windowX = { 55f, 155f, 395f, 495f };
        for (float x : windowX) {
            renderer.drawRect(x, H - TrainLayout.DOOR_Y + 40f, 80f, 48f, COL_WIN_BORDER);
            renderer.drawRect(x + 3f, H - TrainLayout.DOOR_Y + 43f, 74f, 42f, COL_WIN_GLASS);
            renderer.drawRect(x + 3f, H - TrainLayout.DOOR_Y + 75f, 22f, 8f, COL_WIN_SHINE);
        }
    }

    private void drawDoor(float trainY, float doorOpenAmount) {
        // Background recess
        renderer.drawRect(TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH - 4f, trainY, TrainLayout.DOOR_WIDTH * 2f + 8f,
                TrainLayout.DOOR_Y, COL_DOOR_BG);

        // Animated sliding panels
        float panelWidth = TrainLayout.DOOR_WIDTH * (1f - doorOpenAmount);
        renderer.drawRect(TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH, trainY, panelWidth, TrainLayout.DOOR_Y - 8f,
                COL_DOOR_PANEL);
        renderer.drawRect(TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH + panelWidth * 0.6f, trainY, 2f,
                TrainLayout.DOOR_Y - 8f, COL_DOOR_LINE);
        renderer.drawRect(TrainLayout.DOOR_X + TrainLayout.DOOR_WIDTH - panelWidth, trainY, panelWidth,
                TrainLayout.DOOR_Y - 8f, COL_DOOR_PANEL);
        renderer.drawRect(TrainLayout.DOOR_X + TrainLayout.DOOR_WIDTH - panelWidth * 0.4f, trainY, 2f,
                TrainLayout.DOOR_Y - 8f, COL_DOOR_LINE);

        // Door frame and sill
        renderer.drawRect(TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH - 6f, trainY, 6f, TrainLayout.DOOR_Y,
                COL_DOOR_FRAME);
        renderer.drawRect(TrainLayout.DOOR_X + TrainLayout.DOOR_WIDTH, trainY, 6f, TrainLayout.DOOR_Y, COL_DOOR_FRAME);
        renderer.drawRect(TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH - 6f, trainY, TrainLayout.DOOR_WIDTH * 2f + 12f,
                9f, COL_DOOR_SILL);
    }

    private void drawTactileStrip(float W, float H) {
        renderer.drawRect(0, H - TrainLayout.DOOR_Y - 10f, W, 10f, COL_TACTILE);
        for (float x = 20f; x < W; x += 25f)
            renderer.drawCircle(x, H - TrainLayout.DOOR_Y - 5f, 3.5f, COL_BUMP);
    }

    private void drawWaitingMarkers(float H) {
        float mx = TrainLayout.DOOR_X - TrainLayout.DOOR_WIDTH - 6f;
        float my = H - TrainLayout.DOOR_Y - 26f;
        float mw = TrainLayout.DOOR_WIDTH * 2f + 12f;
        renderer.drawRect(mx, my, mw, 5f, COL_MARKER);
        renderer.drawRect(mx, my, 5f, 22f, COL_MARKER);
        renderer.drawRect(TrainLayout.DOOR_X + TrainLayout.DOOR_WIDTH + 1f, my, 5f, 22f, COL_MARKER);
    }
}