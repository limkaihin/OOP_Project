package com.example.app.demo.factory;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.entity.EntityManager;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.render.RenderableComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.SpriteComponent;
import com.example.app.engine.collision.ColliderComponent;


public class PlayerFactory implements EntityFactory{
	private final EntityManager em;
	private static final float Player_Dimension_Width = 50;
	private static final float Player_Dimension_Length = 50;
    private static final float Player_Default_X = 50;
	private static final float Player_Default_Y = 50;


    public PlayerFactory(EntityManager em) {
        this.em = em;
    }
    
    @Override
    public Entity create() {
        return create(Player_Default_X, Player_Default_Y, "PLAYER");
    }
    
    @Override
    public Entity create(float x, float y, String textureKey) {
    	return create(x, y, 0f, 0f, textureKey, Player_Dimension_Width, Player_Dimension_Length);    
    }

    @Override
	public Entity create(float x, float y, float vx, float vy, String textureKey, float w, float h) {
		Entity player = em.create();
        
        player.addComponent(TransformComponent.class, new TransformComponent(x, y));
        player.addComponent(VelocityComponent.class, new VelocityComponent(0, 0));
        player.addComponent(ColliderComponent.class, ColliderComponent.aabb(Player_Dimension_Width/2, Player_Dimension_Length/2));
        player.addComponent(SpriteComponent.class, new SpriteComponent(textureKey, Player_Dimension_Width, Player_Dimension_Length));
        return player;
	}
}
