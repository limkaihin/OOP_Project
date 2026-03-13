package com.example.app.demo.factory;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.render.RenderableComponent;

public class EnemyFactory implements EntityFactory {
    private final EntityManager em;
	private static final String Enemy_Sprite = "passenger_npc_01.png";
	private static final float Enemy_Dimension_Width = 50;
	private static final float Enemy_Dimension_Length = 50;


    public EnemyFactory(EntityManager em) {
        this.em = em;
    }

    public Entity create(float x, float y, String textureKey) {
        Entity enemy = em.create();
        enemy.addComponent(TransformComponent.class, new TransformComponent(x, y));
        enemy.addComponent(RenderableComponent.class, new RenderableComponent(textureKey,Enemy_Dimension_Width,Enemy_Dimension_Length));
        return enemy;
    }

	@Override
	public Entity create() {
		return create(400, 300, Enemy_Sprite);
	}
}