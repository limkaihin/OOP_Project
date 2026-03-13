package com.example.app.demo.factory;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.render.RenderableComponent;

public class PlayerFactory implements EntityFactory{
	private final EntityManager em;
	private static final String Player_Sprite = "passenger_npc_09.png";
	private static final float Player_Dimension_Width = 50;
	private static final float Player_Dimension_Length = 50;
	
    public PlayerFactory(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public Entity create() {
        return create(100, 100, "PLAYER");
    }

	@Override
	public Entity create(float x, float y, String textureKey) {
		Entity player = em.create();
        player.addComponent(TransformComponent.class, new TransformComponent(100, 100));
        player.addComponent(RenderableComponent.class, new RenderableComponent(Player_Sprite,Player_Dimension_Width,Player_Dimension_Length));
        return player;
	}
}
