package com.example.app.demo.scenes;

import java.util.Random;

import com.example.app.engine.EngineContext;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.render.RenderableComponent;
import com.example.app.engine.scene.AbstractBaseScene;


public final class PlatformScene extends AbstractBaseScene {
     private final EngineContext ctx;
     private final int initialSpawnCount;
     private Entity player;
     private Entity npc;
     private final Random rng = new Random();
     private float npcHitCooldown = 0f;      
     private float timer = 30f;


 
    public PlatformScene (EngineContext ctx, int initialSpawnCount) {
        this.ctx = ctx;
        this.initialSpawnCount = initialSpawnCount;
    }

    @Override
    public void onLoad() {
        ctx.ioManager.log("PlatformScene", "Loading platform scene");
        //Create Player
        this.player = ctx.playerFactory.create(100, 100, "passenger_npc_09.png");
        //Create Platform
        spawnPlatformObjects();
        //Create passengers
        spawnPassengers();

        ctx.ioManager.log("PlatformScene", "Platform loaded with passengers");
    }

    @Override
    public void update(float dt) {
        // Simple timer to auto-return to menu after 30s
        timer -= dt;
        if (timer <= 0) {
            ctx.sceneManager.switchTo(new MenuScene(ctx));
        }
    }

    @Override
    public void render() {
     // --- UI Panels (Top of screen) ---
    ctx.renderer.draw("score_ui_panel.png", 100, 450, 0, 100, 60);        // Top-left
    ctx.renderer.draw("timer_ui_panel.png", 550, 450, 0, 100, 60);       // Top-right
    ctx.renderer.draw("instruction_banner.png", 350, 450, 0, 300, 60);  // Center-top

    for (Entity e : ctx.entityManager.getAll()) {
        if (e.hasComponent(RenderableComponent.class) && e.hasComponent(TransformComponent.class)) {
            RenderableComponent rc = e.getComponent(RenderableComponent.class);
            TransformComponent tc = e.getComponent(TransformComponent.class);
            // Calling renderer draw method
            ctx.renderer.draw(rc.renderKey, tc.x, tc.y, 0, rc.width, rc.height);
        	}
    	}
    }
    
    private void spawnPlatformObjects() {
    	// --- Platform MRT elements ---
        // MRT side 
        spawnEnvrionmentEntity("mrt_train_side.png", 400, 330 ,1000, 200);       
        //door
        spawnEnvrionmentEntity("train_door.png", 400, 330, 200, 150);
        
        //Boarding Zone
        spawnEnvrionmentEntity("boarding_zone_marker.png", 400, 180, 200, 60);    
        
        // --- Waiting zones (small markers on floor, left & right of door) ---
        spawnEnvrionmentEntity("waiting_zone_floor_marker.png", 100, 180, 400, 100 );
        spawnEnvrionmentEntity("waiting_zone_floor_marker.png", 600, 180, 300, 100 );

        // --- Station objects (on the platform, below waiting zones) ---
        spawnEnvrionmentEntity("platform_pillar.png", 150, 220, 40, 120);      // far left
        spawnEnvrionmentEntity("platform_pillar.png", 650, 220, 40, 120);      // far right
        spawnEnvrionmentEntity("station_information_sign_stand.png", 400, 50, 60, 80);  // center
        spawnEnvrionmentEntity("platform_bench.png", 150, 50, 200, 60);       // left side near pillar    

    }
    
    private void spawnEnvrionmentEntity(String key, float x, float y, float w, float h) {
        Entity e = ctx.entityManager.create();
        e.addComponent(TransformComponent.class, new TransformComponent(x, y));
        e.addComponent(RenderableComponent.class, new RenderableComponent(key, w, h));
    }

    private void spawnPassengers() {
    	ctx.ioManager.log("PlatformScene", "Spawning passengers");
            // Exiting passengers
            for (int i = 0; i < 5; i++) {
                ctx.enemyFactory.create(400, 260 + (i * 30), "passenger_npc_01.png");
                ctx.ioManager.log("PlatformScene", "Spawned exiting passangers");
            }

            // Waiting passengers
            for (int i = 0; i < 3; i++) {
                ctx.enemyFactory.create(260 + (i * 20), 330, "passenger_npc_03.png");
                ctx.ioManager.log("PlatformScene", "Spawned waiting passangers");
            }
    }
    

    public EngineContext getCtx() {
        return ctx;
    }

    public int getInitialSpawnCount() {
        return initialSpawnCount;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public Entity getNpc() {
        return npc;
    }

    public void setNpc(Entity npc) {
        this.npc = npc;
    }

    public Random getRng() {
        return rng;
    }

    public float getNpcHitCooldown() {
        return npcHitCooldown;
    }

    public void setNpcHitCooldown(float npcHitCooldown) {
        this.npcHitCooldown = npcHitCooldown;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }
}
