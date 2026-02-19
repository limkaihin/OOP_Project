package com.example.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.app.demo.render.LibGdxRenderer;
import com.example.app.demo.scenes.MenuScene;
import com.example.app.engine.EngineConfig;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.SimpleCollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.io.*;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.IRenderer;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.scene.Scene;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private EngineContext ctx;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapes = new ShapeRenderer();

        IRenderer renderer = new LibGdxRenderer(shapes);
        EngineConfig config = new EngineConfig(640, 480, "OOP_project");
        SceneManager sceneManager = new SceneManager();
        EntityManager entityManager = new EntityManager();
        MovementManager movementManager = new MovementManager();
        SimpleCollisionManager collisionManager = new SimpleCollisionManager(config.collisionCellSize);

        // Input bindings
        InputBinding bindings = new InputBinding();
        bindings.bind(Input.Keys.NUM_1, InputAction.SONG1);
        bindings.bind(Input.Keys.NUM_2, InputAction.SONG2);
        bindings.bind(Input.Keys.NUM_3, InputAction.SONG3);
        bindings.bind(Input.Keys.LEFT, InputAction.STOP);
        bindings.bind(Input.Keys.RIGHT, InputAction.PLAY);
        bindings.bind(Input.Keys.UP, InputAction.VOLUME_UP);
        bindings.bind(Input.Keys.DOWN, InputAction.VOLUME_DOWN);

        // Allow WASD input
        bindings.bind(Input.Keys.A, InputAction.MOVE_LEFT);
        bindings.bind(Input.Keys.D, InputAction.MOVE_RIGHT);
        bindings.bind(Input.Keys.W, InputAction.MOVE_UP);
        bindings.bind(Input.Keys.S, InputAction.MOVE_DOWN);

        // Space to spawn
        bindings.bind(Input.Keys.SPACE, InputAction.ACTION_1); // demo: spawn obstacle

        bindings.bind(Input.Keys.ENTER, InputAction.CONFIRM);
        bindings.bind(Input.Keys.ESCAPE, InputAction.BACK);
        bindings.bind(Input.Keys.P, InputAction.PAUSE);

        InputHandler inputHandler = new InputHandler(bindings);

        // Output
        AudioPlayer audioPlayer = new AudioPlayer();
        ErrorLogger errorLogger = new ErrorLogger();
        OutputHandler outputHandler = new OutputHandler(audioPlayer, errorLogger);

        IOManager ioManager = new IOManager(inputHandler, outputHandler);

        ctx = new EngineContext(config, sceneManager, entityManager, movementManager, collisionManager, ioManager, renderer);

        // Start at menu
        sceneManager.push(new MenuScene(ctx));
        ioManager.log("GameMaster", "Engine started");
    }

    @Override
    public void render() {
        float realDt = Gdx.graphics.getDeltaTime();
        ctx.update(realDt);

        // Clear once
        ScreenUtils.clear(0.10f, 0.10f, 0.14f, 1f);

        Scene current = ctx.sceneManager.current();
        if (current != null) {
            ctx.renderer.begin();
            current.render();
            ctx.renderer.end();
        }

        // Draw shapes from engine RenderQueue
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (RenderCommand cmd : ctx.renderQueue.view()) {
            shapes.setColor(cmd.r, cmd.g, cmd.b, cmd.a);
            shapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        shapes.end();

        // UI instructions (top-left)
        batch.begin();
        float x = 10f;
        float y = Gdx.graphics.getHeight() - 10f;

        font.draw(batch, "Controls:", x, y);
        font.draw(batch, "WASD: move", x, y - 18f);
        font.draw(batch, "1 : hit effect music", x, y - 36f);
        font.draw(batch, "2 : kahoot music", x, y - 54f);
        font.draw(batch, "3 : crazy frog music", x, y - 72f);
        font.draw(batch, "Left/Right: Stop/Play", x, y - 90f);
        font.draw(batch, "Up/Down: Volume +-", x, y - 108f);
        font.draw(batch, "Volume: " + ctx.ioManager.getOutputHandler().getAudioPlayer().getVolumePercentage() + "%", x,
                y - 126f);
        font.draw(batch, "Space: spawn obstacle", x, y - 144f);
        font.draw(batch, "Enter: start / (if paused) step 1 frame", x, y - 162f);
        font.draw(batch, "P: pause/resume  |  Esc: quit", x, y - 180f);

        batch.end();

        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.SONG1)) {
            ctx.ioManager.getOutputHandler().stopMusic();
            ctx.ioManager.getOutputHandler().playMusic("hit.wav");
        }
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.SONG2)) {
            ctx.ioManager.getOutputHandler().stopMusic();
            ctx.ioManager.getOutputHandler().playMusic("music1.mp3");
        }
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.SONG3)) {
            ctx.ioManager.getOutputHandler().stopMusic();
            ctx.ioManager.getOutputHandler().playMusic("music2.mp3");
        }
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.PLAY)) {
            String lastMusicFilePath = ctx.ioManager.getOutputHandler().getAudioPlayer().getLastMusicFilePath();
            if (lastMusicFilePath != null) {
                ctx.ioManager.getOutputHandler().playMusic(lastMusicFilePath);
            } else {
                ctx.ioManager.log("Audio", "No current music to play");
            }
        }
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.STOP)) {
            ctx.ioManager.getOutputHandler().stopMusic();
        }
        if (ctx.ioManager.getInputHandler().getState().isPressed(InputAction.VOLUME_UP)) {
            ctx.ioManager.getOutputHandler().getAudioPlayer().increaseVolume(0.1f * Gdx.graphics.getDeltaTime());
        }
        if (ctx.ioManager.getInputHandler().getState().isPressed(InputAction.VOLUME_DOWN)) {
            ctx.ioManager.getOutputHandler().getAudioPlayer().decreaseVolume(0.1f * Gdx.graphics.getDeltaTime());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        if (ctx != null)
            ctx.dispose();
        if (shapes != null)
            shapes.dispose();
        if (batch != null)
            batch.dispose();
        if (font != null)
            font.dispose();
    }
}
