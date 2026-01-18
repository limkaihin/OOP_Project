package com.example.app.engine.movement;

import com.example.app.engine.entity.Entity;

/**
 * Basic movement manager:
 * - If entity has Transform + Velocity: integrates position
 * - If entity also has Acceleration: integrates velocity first
 */
public final class SimpleMovementManager implements MovementManager {

    @Override
    public void update(float dt, Iterable<Entity> entities) {
        if (dt <= 0f) return;

        for (Entity e : entities) {
            if (e == null) continue;

            TransformComponent t = e.getComponent(TransformComponent.class);
            VelocityComponent v = e.getComponent(VelocityComponent.class);

            if (t == null || v == null) continue;

            AccelerationComponent a = e.getComponent(AccelerationComponent.class);
            if (a != null) {
                v.vx += a.ax * dt;
                v.vy += a.ay * dt;
            }

            t.x += v.vx * dt;
            t.y += v.vy * dt;
        }
    }
}
