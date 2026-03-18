package com.example.app.engine;

import java.util.ArrayList;
import java.util.List;

import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.collision.CollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.factory.EntityFactory;
import com.example.app.engine.io.IOManager;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.IRenderer;
import com.example.app.engine.scene.Scene;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.util.EngineClock;
import com.example.app.engine.util.EventBus;

public final class EngineContext {

    private final EntityFactory playerFactory;
    private final EntityFactory enemyFactory;

    private final EngineConfig config;
    private final SceneManager sceneManager;
    private final EntityManager entityManager;
    private final MovementManager movementManager;
    private final CollisionManager collisionManager;
    private final IOManager ioManager;
    private IRenderer renderer;

    private final EngineClock clock;
    private final EventBus<CollisionEvent> collisionEvents;

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
        this.renderer = renderer;
        this.playerFactory = playerFactory;
        this.enemyFactory = enemyFactory;
        this.clock = new EngineClock(config.fixedDt);
        this.collisionEvents = new EventBus<>();
    }

    // ---- Getters ----

    public EngineConfig getConfig() { return config; }
    public SceneManager getSceneManager() { return sceneManager; }
    public EntityManager getEntityManager() { return entityManager; }
    public MovementManager getMovementManager() { return movementManager; }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public IOManager getIoManager() { return ioManager; }
    public IRenderer getRenderer() { return renderer; }
    public void setRenderer(IRenderer renderer) { this.renderer = renderer; }
    public EngineClock getClock() { return clock; }
    public EventBus<CollisionEvent> getCollisionEvents() { return collisionEvents; }
    public EntityFactory getPlayerFactory() { return playerFactory; }
    public EntityFactory getEnemyFactory() { return enemyFactory; }
    
    public void addGlobalInputHandler(Runnable handler) {
        if (handler != null) globalInputHandlers.add(handler);
    }

    public void update(float realDt) {
        ioManager.update(realDt);

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