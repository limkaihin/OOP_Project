package com.example.app;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.ScreenUtils;
import com.example.app.demo.factory.EnemyFactory;
import com.example.app.demo.factory.PlayerFactory;
import com.example.app.demo.render.LibGdxRenderer;
import com.example.app.demo.scenes.MenuScene;
import com.example.app.demo.scenes.SettingsScene;
import com.example.app.engine.EngineConfig;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.SimpleCollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.factory.EntityFactory;
import com.example.app.engine.io.*;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.IRenderer;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.scene.Scene;

public class GameMaster extends ApplicationAdapter {

    private SpriteBatch batch;
    private ShapeRenderer shapes;
    private EngineContext ctx;
    private final Vector3 mouseVec = new Vector3();
    private OrthographicCamera camera;
    private Viewport viewport;

    @Override
    public void create() {
        batch  = new SpriteBatch();
        shapes = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(640, 480, camera);
        viewport.apply();

        IRenderer renderer = new LibGdxRenderer(shapes, batch);
        EngineConfig config = new EngineConfig(640, 480, "Board The Train!");
        SceneManager sceneManager = new SceneManager();
        EntityManager entityManager = new EntityManager();
        MovementManager movementManager = new MovementManager();
        SimpleCollisionManager collisionManager = new SimpleCollisionManager(config.collisionCellSize);

        // Input bindings
        InputBinding bindings = new InputBinding();
        bindings.bind(Input.Keys.UP, InputAction.VOLUME_UP);
        bindings.bind(Input.Keys.DOWN, InputAction.VOLUME_DOWN);
        bindings.bind(Input.Keys.A, InputAction.MOVE_LEFT);
        bindings.bind(Input.Keys.D, InputAction.MOVE_RIGHT);
        bindings.bind(Input.Keys.W, InputAction.MOVE_UP);
        bindings.bind(Input.Keys.S, InputAction.MOVE_DOWN);
        bindings.bind(Input.Keys.ENTER, InputAction.CONFIRM);
        bindings.bind(Input.Keys.ESCAPE, InputAction.BACK);
        bindings.bind(Input.Keys.E, InputAction.OPEN_SETTINGS);
        bindings.bindMouse(Input.Buttons.LEFT, InputAction.ACTION_1);

        InputHandler inputHandler = new InputHandler(bindings);
        AudioPlayer audioPlayer = new AudioPlayer();
        ErrorLogger errorLogger = new ErrorLogger();
        OutputHandler outputHandler = new OutputHandler(audioPlayer, errorLogger);
        IOManager ioManager = new IOManager(inputHandler, outputHandler);

        EntityFactory playerFactory = new PlayerFactory(entityManager);
        EntityFactory enemyFactory = new EnemyFactory(entityManager);

        ctx = new EngineContext(config, sceneManager, entityManager, movementManager,
                collisionManager, ioManager, renderer, playerFactory, enemyFactory);

        ctx.addGlobalInputHandler(() -> {
            if (ioManager.getInputHandler().getState().isJustPressed(InputAction.OPEN_SETTINGS)) {
                Scene cur = ctx.getSceneManager().current();
                if (!(cur instanceof SettingsScene)) {
                    ctx.getSceneManager().push(new SettingsScene(ctx));
                }
            }
        });

        ctx.addGlobalInputHandler(() -> {
            if (ioManager.getInputHandler().getState().isJustPressed(InputAction.PAUSE)) {
                ctx.getClock().togglePause();
                ioManager.log("Engine", ctx.getClock().isPaused() ? "Paused" : "Resumed");
            }
        });

        ctx.getSceneManager().push(new MenuScene(ctx));
        ioManager.log("GameMaster", "Engine started");
        ioManager.getOutputHandler().playMusic("music1.mp3");
    }

    @Override
    public void render() {
        mouseVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouseVec);
        ctx.getIoManager().getInputHandler().setMousePosition(mouseVec.x, mouseVec.y);

        float realDt = Gdx.graphics.getDeltaTime();
        ctx.update(realDt);

        ScreenUtils.clear(0.10f, 0.10f, 0.14f, 1f);

        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        Scene current = ctx.getSceneManager().current();
        if (current != null) {
            ctx.getRenderer().begin();
            current.render();
            ctx.getRenderer().end();
            current.renderHud();
            ctx.getRenderer().flushSprites();
        }
    }

    @Override
    public void dispose() {
        if (ctx != null) ctx.dispose();
        if (shapes != null) shapes.dispose();
        if (batch != null) batch.dispose();
    }

    // Resize
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
}