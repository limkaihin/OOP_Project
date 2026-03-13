package com.example.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.app.demo.factory.EnemyFactory;
import com.example.app.demo.factory.PlayerFactory;
import com.example.app.demo.factory.EntityFactory;
import com.example.app.demo.render.LibGdxRenderer;
import com.example.app.demo.scenes.MenuScene;
import com.example.app.demo.scenes.TrainScene;
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

        // Removed Space spawn obstacle
        bindings.bind(Input.Keys.ENTER, InputAction.CONFIRM);
        bindings.bind(Input.Keys.ESCAPE, InputAction.BACK);
        bindings.bind(Input.Keys.P, InputAction.PAUSE);

        InputHandler inputHandler = new InputHandler(bindings);

        // Output
        AudioPlayer audioPlayer = new AudioPlayer();
        ErrorLogger errorLogger = new ErrorLogger();
        OutputHandler outputHandler = new OutputHandler(audioPlayer, errorLogger);

        IOManager ioManager = new IOManager(inputHandler, outputHandler);

        // Entities
        EntityFactory playerFactory = new PlayerFactory(entityManager);
        EntityFactory enemyFactory = new EnemyFactory(entityManager);

        ctx = new EngineContext(config, sceneManager, entityManager, movementManager, collisionManager, ioManager,
                renderer, playerFactory, enemyFactory);

        // Start at Menu Scene
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

        // UI instructions (top-left) removed because scenes handle their own UI

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
