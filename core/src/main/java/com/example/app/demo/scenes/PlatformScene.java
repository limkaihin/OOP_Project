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
      private PassengerAbstractFactory factory;
      private float timer = 30f;
    private GamePhase phase;

 
    public PlatformScene (EngineContext ctx, int initialSpawnCount) {
        this.ctx = ctx;
        this.initialSpawnCount = initialSpawnCount;
        this.factory = new PolitePassengerFactory(); // stub factory for now
    }

    @Override
    public void onLoad() {
        ctx.ioManager.log("PlatformScene", "Loading platform scene");

        spawnPlatformObjects();
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

    // --- Platform MRT elements ---
    // MRT side 
    ctx.renderer.draw("mrt_train_side.png", 400, 330, 0,1000, 200);       
    //door
    ctx.renderer.draw("train_door.png", 400, 330, 0, 200, 150);
    
    //Boarding Zone
    ctx.renderer.draw("boarding_zone_marker.png", 400, 180, 0, 200, 60);    
    
    // --- Waiting zones (small markers on floor, left & right of door) ---
    ctx.renderer.draw("waiting_zone_floor_marker.png", 100, 180, 0, 400, 100 );
    ctx.renderer.draw("waiting_zone_floor_marker.png", 600, 180, 0, 300, 100 );

    // --- Station objects (on the platform, below waiting zones) ---
    ctx.renderer.draw("platform_pillar.png", 150, 220, 0, 40, 120);      // far left
    ctx.renderer.draw("platform_pillar.png", 650, 220, 0, 40, 120);      // far right
    ctx.renderer.draw("station_information_sign_stand.png", 400, 50, 0, 60, 80);  // center
    ctx.renderer.draw("platform_bench.png", 150, 50, 0, 200, 60);       // left side near pillar    

     // --- UI Panels (Top of screen) ---
    ctx.renderer.draw("score_ui_panel.png", 100, 450, 0, 100, 60);        // Top-left
    ctx.renderer.draw("timer_ui_panel.png", 550, 450, 0, 100, 60);       // Top-right
    ctx.renderer.draw("instruction_banner.png", 350, 450, 0, 300, 60);  // Center-top


    }
    private void spawnPlatformObjects() {
        // This method is optional if you want to spawn them as entities instead of just drawing in render()
        // For now, we are just using the renderer directly
    }

    private void spawnPassengers() {
        // Stubs: Just create entities with RenderableComponents so they appear
        for (int i = 0; i < 5; i++) {
            Entity exiting = ctx.entityManager.create();
            exiting.addComponent(TransformComponent.class, new TransformComponent(400, 260 + i * 30));
            exiting.addComponent(RenderableComponent.class, new RenderableComponent("passenger_npc_01.png"));
        }

        for (int i = 0; i < 3; i++) {
            Entity waiting = ctx.entityManager.create();
            waiting.addComponent(TransformComponent.class, new TransformComponent(260 + i * 20, 330));
            waiting.addComponent(RenderableComponent.class, new RenderableComponent("passenger_npc_03.png"));
        }
    }

    // Stub factory interface and class so the code compiles
    interface PassengerAbstractFactory {
        void createExitingPassenger(EngineContext ctx, float x, float y);
        void createEnteringPassenger(EngineContext ctx, float x, float y);
    }

    static class PolitePassengerFactory implements PassengerAbstractFactory {
        @Override
        public void createExitingPassenger(EngineContext ctx, float x, float y) {
            Entity e = ctx.entityManager.create();
            e.addComponent(RenderableComponent.class, new RenderableComponent("passenger_npc_01.png"));
            e.addComponent(TransformComponent.class, new TransformComponent(x, y));
        }

        @Override
        public void createEnteringPassenger(EngineContext ctx, float x, float y) {
            Entity e = ctx.entityManager.create();
            e.addComponent(RenderableComponent.class, new RenderableComponent("passenger_npc_03.png"));
            e.addComponent(TransformComponent.class, new TransformComponent(x, y));
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

    public PassengerAbstractFactory getFactory() {
        return factory;
    }

    public void setFactory(PassengerAbstractFactory factory) {
        this.factory = factory;
    }

    public float getTimer() {
        return timer;
    }

    public void setTimer(float timer) {
        this.timer = timer;
    }
}
