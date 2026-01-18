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
import com.example.app.engine.EngineImpl;
import com.example.app.engine.collision.SimpleCollisionManager;
import com.example.app.engine.entity.DefaultEntityManager;
import com.example.app.engine.io.GdxOutputManager;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputBindings;
import com.example.app.engine.io.LibGdxInputManager;
import com.example.app.engine.movement.SimpleMovementManager;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.scene.StackSceneManager;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private BitmapFont font;
    private ShapeRenderer shapes;

    private EngineImpl engine;
    private EngineContext ctx;

    private MenuScene menuScene;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        shapes = new ShapeRenderer();

        StackSceneManager sceneManager = new StackSceneManager();
        DefaultEntityManager entityManager = new DefaultEntityManager();
        SimpleMovementManager movementManager = new SimpleMovementManager();
        SimpleCollisionManager collisionManager = new SimpleCollisionManager();

        InputBindings bindings = new InputBindings();
        // Arrow keys (already present)
        bindings.bind(Input.Keys.LEFT,  InputAction.MOVE_LEFT);
        bindings.bind(Input.Keys.RIGHT, InputAction.MOVE_RIGHT);
        bindings.bind(Input.Keys.UP,    InputAction.MOVE_UP);
        bindings.bind(Input.Keys.DOWN,  InputAction.MOVE_DOWN);
        // ALSO allow WASD
        bindings.bind(Input.Keys.A, InputAction.MOVE_LEFT);
        bindings.bind(Input.Keys.D, InputAction.MOVE_RIGHT);
        bindings.bind(Input.Keys.W, InputAction.MOVE_UP);
        bindings.bind(Input.Keys.S, InputAction.MOVE_DOWN);
        // Space to spawn
        bindings.bind(Input.Keys.SPACE, InputAction.SPAWN);

        bindings.bind(Input.Keys.ENTER, InputAction.CONFIRM);
        bindings.bind(Input.Keys.ESCAPE, InputAction.BACK);
        bindings.bind(Input.Keys.P, InputAction.PAUSE);

        LibGdxInputManager inputManager = new LibGdxInputManager(bindings);
        GdxOutputManager outputManager = new GdxOutputManager();

        EngineConfig config = new EngineConfig(640, 480, "OOP_project");
        ctx = new EngineContext(config, sceneManager, entityManager, movementManager, collisionManager, inputManager, outputManager);
        engine = new EngineImpl(ctx);

        menuScene = new MenuScene(ctx);
        sceneManager.push(menuScene);

        outputManager.log("Main", "Engine started");
    }

    @Override
    public void render() {
        float realDt = Gdx.graphics.getDeltaTime();
        engine.update(realDt);

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
        font.draw(batch, "WASD / Arrow keys: move", x, y - 18f);
        font.draw(batch, "Space: spawn obstacle", x, y - 36f);
        font.draw(batch, "Enter: start / (if paused) step 1 frame", x, y - 54f);
        font.draw(batch, "P: pause/resume  |  Esc: quit", x, y - 72f);
        
        batch.end();


        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {
        if (engine != null) engine.dispose();
        if (shapes != null) shapes.dispose();
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
    }
}