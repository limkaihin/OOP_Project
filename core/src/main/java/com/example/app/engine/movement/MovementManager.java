package com.example.app.engine.movement;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;

public class MovementManager {

    private float gravity = 0f;      // units/sec^2 applied to vy
    private float maxSpeed = 0f;     // 0 = no clamp

    public MovementManager() {}

    public MovementManager(float gravity, float maxSpeed) {
        this.gravity = gravity;
        this.maxSpeed = maxSpeed;
    }

    public float getGravity() { return gravity; }
    public void setGravity(float gravity) { this.gravity = gravity; }

    public float getMaxSpeed() { return maxSpeed; }
    public void setMaxSpeed(float maxSpeed) { this.maxSpeed = maxSpeed; }

    // Update all entities in the list
    public void update(float dt, Iterable<Entity> entities) {
        if (dt <= 0f || entities == null) return;
        for (Entity e : entities) {
            if (e == null) continue;
            moveEntity(dt, e);
        }
    }

    // Backward-compatible update signature used by older code
    public void update(float dt, EntityManager entityManager) {
        if (entityManager == null) return;
        update(dt, entityManager.getAll());
    }

    // Updates a single entity's velocity and position
    public void moveEntity(float dt, Entity e) {
        TransformComponent t = e.getComponent(TransformComponent.class);
        VelocityComponent v = e.getComponent(VelocityComponent.class);
        if (t == null || v == null) return;

        movePhysics(dt, e, t, v);
    }

    // Applies acceleration/gravity to velocity, then integrates position
    public void movePhysics(float dt, Entity e, TransformComponent t, VelocityComponent v) {
        AccelerationComponent a = e.getComponent(AccelerationComponent.class);
        if (a != null) {
            v.vx += a.ax * dt;
            v.vy += a.ay * dt;
        }
        if (gravity != 0f) {
            v.vy += gravity * dt;
        }

        // Optional clamp
        if (maxSpeed > 0f) {
            float speedSq = v.vx * v.vx + v.vy * v.vy;
            float maxSq = maxSpeed * maxSpeed;
            if (speedSq > maxSq) {
                float invLen = (float)(1.0 / Math.sqrt(speedSq));
                v.vx = v.vx * invLen * maxSpeed;
                v.vy = v.vy * invLen * maxSpeed;
            }
        }

        t.x += v.vx * dt;
        t.y += v.vy * dt;
    }
}
