package com.example.app.engine;

import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.collision.CollisionManager;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.io.IOManager;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.movement.MovementManager;
import com.example.app.engine.render.RenderQueue;
import com.example.app.engine.scene.Scene;
import com.example.app.engine.scene.SceneManager;
import com.example.app.engine.util.EngineClock;
import com.example.app.engine.util.EventBus;

import java.util.List;

/**
 * EngineContext: central registry for engine subsystems (UML-aligned).
 */
public final class EngineContext {

    public final EngineConfig config;

    public final SceneManager sceneManager;
    public final EntityManager entityManager;
    public final MovementManager movementManager;
    public final CollisionManager collisionManager;
    public final IOManager ioManager;

    // Internal helpers (kept from the original engine to preserve behavior)
    public final RenderQueue renderQueue;
    public final EngineClock clock;
    public final EventBus<CollisionEvent> collisionEvents;

    public EngineContext(
            EngineConfig config,
            SceneManager sceneManager,
            EntityManager entityManager,
            MovementManager movementManager,
            CollisionManager collisionManager,
            IOManager ioManager) {
        this.config = config;
        this.sceneManager = sceneManager;
        this.entityManager = entityManager;
        this.movementManager = movementManager;
        this.collisionManager = collisionManager;
        this.ioManager = ioManager;

        this.renderQueue = new RenderQueue();
        this.clock = new EngineClock(config.fixedDt);
        this.collisionEvents = new EventBus<>();
    }

    /**
     * Performs one engine tick (previously EngineImpl.update).
     */
    public void update(float realDt) {
        // Clear render queue each frame; scenes submit draw commands.
        renderQueue.clear();

        // IO updates on real time
        ioManager.update(realDt);

        // Engine-level pause/step controls (generic)
        if (ioManager.getInputHandler().getState().isJustPressed(InputAction.PAUSE)) {
            clock.togglePause();
            ioManager.log("Engine", clock.isPaused() ? "Paused" : "Resumed");
        }
        if (clock.isPaused() && ioManager.getInputHandler().getState().isJustPressed(InputAction.CONFIRM)) {
            clock.requestStep();
        }

        int steps = clock.consumeSteps(realDt, config.maxSubSteps);
        float dt = clock.fixedDt();

        for (int i = 0; i < steps; i++) {
            Scene current = sceneManager.current();
            if (current != null){
                current.update(dt);
            }
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
