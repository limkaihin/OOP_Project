package com.example.app.demo.factory;

import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.factory.EntityFactory;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.RenderableComponent;

public class PlayerFactory implements EntityFactory {
    private final EntityManager em;
    private static final String PLAYER_TEXTURE_KEY = "PLAYER";
    private static final float WIDTH = 32f;
    private static final float HEIGHT = 32f;

    public PlayerFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public Entity create() {
        return create(120f, 80f, PLAYER_TEXTURE_KEY);
    }

    @Override
    public Entity create(float x, float y, String textureKey) {
        Entity player = em.create();
        player.addComponent(TransformComponent.class, new TransformComponent(x, y));
        player.addComponent(VelocityComponent.class, new VelocityComponent(0, 0));
        player.addComponent(ColliderComponent.class, ColliderComponent.aabb(16, 16));
        player.addComponent(RenderableComponent.class, new RenderableComponent(textureKey, WIDTH, HEIGHT));
        return player;
    }
}