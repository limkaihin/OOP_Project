package com.example.app.demo.factory;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.render.RenderableComponent;

public class EnemyFactory implements EntityFactory {
    private final EntityManager em;
    private static final String DEFAULT_TEXTURE_KEY = "passenger_npc_01.png";
    private static final float WIDTH = 24f;
    private static final float HEIGHT = 24f;
    private static final float COLLIDER_RADIUS = 12f;
 
    public EnemyFactory(EntityManager em) {
        this.em = em;
    }
 
    @Override
    public Entity create() {
        return create(400f, 300f, DEFAULT_TEXTURE_KEY);
    }
 
    @Override
    public Entity create(float x, float y, String textureKey) {
        Entity npc = em.create();
        npc.addComponent(TransformComponent.class, new TransformComponent(x, y));
        npc.addComponent(VelocityComponent.class, new VelocityComponent(0, 0));
        npc.addComponent(ColliderComponent.class, ColliderComponent.circle(COLLIDER_RADIUS));
        npc.addComponent(RenderableComponent.class, new RenderableComponent(textureKey, WIDTH, HEIGHT));
        return npc;
    }
}