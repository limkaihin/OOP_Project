package com.example.app.engine;

import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.collision.CollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.io.IOManager;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.IRenderer;
import com.example.app.engine.scene.Scene;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.util.EngineClock;
import com.example.app.engine.util.EventBus;
import com.example.app.demo.factory.EntityFactory;

import java.util.ArrayList;
import java.util.List;

public final class EngineContext {

    public final EntityFactory playerFactory;
    public final EntityFactory enemyFactory;

    public final EngineConfig config;
    public final SceneManager sceneManager;
    public final EntityManager entityManager;
    public final MovementManager movementManager;
    public final CollisionManager collisionManager;
    public final IOManager ioManager;
    public IRenderer renderer;

    public final EngineClock clock;
    public final EventBus<CollisionEvent> collisionEvents;

    private final List<Runnable> globalInputHandlers = new ArrayList<>();

    public EngineContext(
            EngineConfig config,
            SceneManager sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            IOManager ioManager,
            IRenderer renderer,
            EntityFactory playerFactory,
            EntityFactory enemyFactory) {
        this.config = config;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;
        this.playerFactory = playerFactory;
        this.enemyFactory = enemyFactory;
        this.renderer = renderer;
        this.clock = new EngineClock(config.fixedDt);
        this.collisionEvents = new EventBus<>();
    }

    public void addGlobalInputHandler(Runnable handler) {
        if (handler != null) globalInputHandlers.add(handler);
    }

    public void update(float realDt) {
        ioManager.update(realDt);

        // Run global input handlers
        for (Runnable handler : globalInputHandlers) {
            handler.run();
        }

        Scene current = sceneManager.current();
        if (current != null) {
            current.update(realDt);
        }

        int steps = clock.consumeSteps(realDt, config.maxSubSteps);
        float dt = clock.fixedDt();

        for (int i = 0; i < steps; i++) {
            movementManager.update(dt, entityManager.getAll());

            List<CollisionEvent> collisions = collisionManager.update(dt, entityManager.getAll());
            if (collisions != null) {
                for (CollisionEvent e : collisions)
                    collisionEvents.publish(e);
            }

            entityManager.update(dt);
        }
    }

    public void dispose() {
        while (sceneManager.size() > 0)
            sceneManager.pop();
        ioManager.dispose();
        ioManager.log("Engine", "Disposed");
    }
}