package com.example.app.engine;

import com.example.app.engine.collision.CollisionManager;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.io.InputManager;
import com.example.app.engine.io.OutputManager;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.RenderQueue;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.util.EngineClock;
import com.example.app.engine.util.EventBus;

public final class EngineContext {
    public final EngineConfig config;

    public final SceneManager sceneManager;
    public final EntityManager entityManager;

    public final MovementManager movementManager;
    public final CollisionManager collisionManager;

    public final InputManager inputManager;
    public final OutputManager outputManager;

    // Added
    public final RenderQueue renderQueue;
    public final EngineClock clock;
    public final EventBus<CollisionEvent> collisionEvents;

    public EngineContext(
            EngineConfig config,
            SceneManager sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            InputManager inputManager,
            OutputManager outputManager
    ) {
        this.config = config;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.inputManager = inputManager;
        this.outputManager = outputManager;

        this.renderQueue = new RenderQueue();
        this.clock = new EngineClock(config.fixedDt);
        this.collisionEvents = new EventBus<>();
    }
}