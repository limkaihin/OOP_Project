package com.example.app.demo.factory;

import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.SpriteComponent;

public class EnemyFactory implements EntityFactory {
    private final EntityManager em;
    private static final String Enemy_Sprite = "passenger_npc_01.png";
    private static final float Enemy_Dimension_Width = 50;
    private static final float Enemy_Dimension_Length = 50;

    public EnemyFactory(EntityManager em) {
        this.em = em;
    }

    @Override
    public Entity create(float x, float y, float vx, float vy, String textureKey, float w, float h) {
        Entity enemy = em.create();
        enemy.addComponent(TransformComponent.class, new TransformComponent(x, y));
        enemy.addComponent(VelocityComponent.class, new VelocityComponent(vx, vy));
        enemy.addComponent(SpriteComponent.class, new SpriteComponent(textureKey, w, h));
        enemy.addComponent(ColliderComponent.class, ColliderComponent.circle(w / 2f));
        return enemy;
    }

    @Override
	public Entity create(float x, float y, String textureKey) {
    	return create(x, y, 0, 0, textureKey, Enemy_Dimension_Width, Enemy_Dimension_Length);	
    }
    
    @Override
    public Entity create() {
        return create(400, 300, 0, 0, Enemy_Sprite, Enemy_Dimension_Width, Enemy_Dimension_Length);
    }
}