package com.example.app.demo;

import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.collision.SimpleCollisionManager;
import com.example.app.engine.entity.DefaultEntityManager;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.SimpleMovementManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;

import java.util.ArrayList;
import java.util.List;

public final class EngineSmokeTest {

    public static void main(String[] args) {
        DefaultEntityManager em = new DefaultEntityManager();

        // Create 2 circles moving toward each other
        Entity a = em.create();
        a.addComponent(TransformComponent.class, new TransformComponent(0, 0));
        a.addComponent(VelocityComponent.class, new VelocityComponent(50, 0));
        a.addComponent(ColliderComponent.class, ColliderComponent.circle(10));

        Entity b = em.create();
        b.addComponent(TransformComponent.class, new TransformComponent(200, 0));
        b.addComponent(VelocityComponent.class, new VelocityComponent(-50, 0));
        b.addComponent(ColliderComponent.class, ColliderComponent.circle(10));

        SimpleMovementManager mm = new SimpleMovementManager();
        SimpleCollisionManager cm = new SimpleCollisionManager();

        final List<CollisionEvent> collisions = new ArrayList<>();
        cm.addListener(collisions::add);

        float dt = 1f / 60f;

        for (int i = 0; i < 300; i++) { // 5 seconds
            mm.update(dt, em.getAll());
            collisions.addAll(cm.update(dt, em.getAll()));
            em.update(dt);

            if (!collisions.isEmpty()) break;
        }

        if (collisions.isEmpty()) {
            throw new AssertionError("No collision detected. Something is wrong with movement/collision setup.");
        }

        System.out.println("OK: collision detected, events=" + collisions.size());
    }
}
