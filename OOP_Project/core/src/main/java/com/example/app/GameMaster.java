package com.example.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.app.demo.scenes.MenuScene;
import com.example.app.engine.EngineConfig;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.SimpleCollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.io.*;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.scene.SceneManager;

/**
 * GameMaster: libGDX ApplicationAdapter entry point (UML-aligned).
 */
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

        EngineConfig config = new EngineConfig(640, 480, "OOP_project");
        SceneManager sceneManager = new SceneManager();
        EntityManager entityManager = new EntityManager();
        MovementManager movementManager = new MovementManager();
        SimpleCollisionManager collisionManager = new SimpleCollisionManager(config.collisionCellSize);

        // Input bindings
        InputBinding bindings = new InputBinding();
        bindings.bind(Input.Keys.LEFT, InputAction.STOP);
        bindings.bind(Input.Keys.RIGHT, InputAction.PLAY);
        bindings.bind(Input.Keys.UP, InputAction.VOLUME_UP);
        bindings.bind(Input.Keys.DOWN, InputAction.VOLUME_DOWN);

        // ALSO allow WASD
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
        ioManager.playMusic("background.mp3");
        ioManager.log("Audio", "Background music started via engine");
        ctx = new EngineContext(config, sceneManager, entityManager, movementManager, collisionManager, ioManager);

        // Start at menu
        sceneManager.push(new MenuScene(ctx));
        ioManager.log("GameMaster", "Engine started");
    }

    @Override
    public void render() {
        float realDt = Gdx.graphics.getDeltaTime();
        ctx.update(realDt);

        ScreenUtils.clear(0.10f, 0.10f, 0.14f, 1f);

        // Draw shapes from engine RenderQueue
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        for (RenderCommand cmd : ctx.renderQueue.view()) {
            shapes.setColor(cmd.r, cmd.g, cmd.b, cmd.a);

            if (cmd.type == RenderCommand.ShapeType.RECT) {
                shapes.rect(cmd.x, cmd.y, cmd.w, cmd.h);
            } else if (cmd.type == RenderCommand.ShapeType.CIRCLE) {
                shapes.circle(cmd.cx, cmd.cy, cmd.radius);
            } else if (cmd.type == RenderCommand.ShapeType.FULLSCREEN_FADE) {
                shapes.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
        shapes.end();

        // UI instructions (top-left)
        batch.begin();
        float x = 10f;
        float y = Gdx.graphics.getHeight() - 10f;

        font.draw(batch, "Controls:", x, y);
        font.draw(batch, "WASD: move", x, y - 18f);
        font.draw(batch, "Left/Right: Stop/Play", x, y - 36f);
        font.draw(batch, "Up/Down: Volume +-", x, y - 54f);
        font.draw(batch, "Volume: " + ctx.ioManager.getOutputHandler().getAudioPlayer().getVolumePercentage() + "%", x,
                y - 72f);
        font.draw(batch, "Space: spawn obstacle", x, y - 90f);
        font.draw(batch, "Enter: start / (if paused) step 1 frame", x, y - 108f);
        font.draw(batch, "P: pause/resume  |  Esc: quit", x, y - 126f);

        batch.end();
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.PLAY)) {
            ctx.ioManager.getOutputHandler().playMusic("background.mp3");
        }
        if (ctx.ioManager.getInputHandler().getState().isJustPressed(InputAction.STOP)) {
            ctx.ioManager.getOutputHandler().stopMusic();
        }
        if (ctx.ioManager.getInputHandler().getState().isPressed(InputAction.VOLUME_UP)) {
            ctx.ioManager.getOutputHandler().getAudioPlayer().increaseVolume(0.1f);
        }
        if (ctx.ioManager.getInputHandler().getState().isPressed(InputAction.VOLUME_DOWN)) {
            ctx.ioManager.getOutputHandler().getAudioPlayer().decreaseVolume(0.1f);
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
