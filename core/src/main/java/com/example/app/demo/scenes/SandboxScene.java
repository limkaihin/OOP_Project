package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputState;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.RenderCommand;
import com.example.app.engine.render.RenderableComponent;
import com.example.app.engine.scene.BaseScene;

import java.util.Random;

public final class SandboxScene extends BaseScene {

    private final EngineContext ctx;
    private final int initialSpawnCount;

    private Entity player;
    private Entity npc;

    private final Random rng = new Random();

    private Sound hitSound;
    private float npcHitCooldown = 0f;

    public SandboxScene(EngineContext ctx, int initialSpawnCount) {
        this.ctx = ctx;
        this.initialSpawnCount = initialSpawnCount;
    }

    @Override
    public void onLoad() {
        // Load sound effect (assets/hit.wav)
        if (Gdx.files.internal("hit.wav").exists()) {
            hitSound = Gdx.audio.newSound(Gdx.files.internal("hit.wav"));
        } else {
            ctx.outputManager.log("Audio", "Missing assets/hit.wav (NPC hit sound disabled)");
        }

        // Player (RECT)
        player = ctx.entityManager.create();
        player.addComponent(TransformComponent.class, new TransformComponent(120, 120));
        player.addComponent(VelocityComponent.class, new VelocityComponent(0, 0));
        player.addComponent(ColliderComponent.class, ColliderComponent.aabb(18, 14)); // half extents
        player.addComponent(RenderableComponent.class, RenderableComponent.rect(36, 28, 0.25f, 0.75f, 1f, 1f));

        // NPC (CIRCLE) auto-move
        npc = ctx.entityManager.create();
        npc.addComponent(TransformComponent.class, new TransformComponent(520, 300));
        npc.addComponent(VelocityComponent.class, new VelocityComponent(-140f, 90f));
        npc.addComponent(ColliderComponent.class, ColliderComponent.circle(18));
        npc.addComponent(RenderableComponent.class, RenderableComponent.circle(18, 1.0f, 0.55f, 0.10f, 1f));

        // Spawn initial red obstacles (CIRCLE + ObstacleTag)
        for (int i = 0; i < initialSpawnCount; i++) {
            spawnObstacle(
                    60 + rng.nextInt(ctx.config.width - 120),
                    60 + rng.nextInt(ctx.config.height - 120)
            );
        }

        // Collision events (decoupled via engine event bus)
        ctx.collisionEvents.subscribe(this::onCollision);

        ctx.outputManager.log("SandboxScene", "Loaded (initial obstacles=" + initialSpawnCount + ")");
    }

    @Override
    public void update(float dt) {
        if (npcHitCooldown > 0f) npcHitCooldown -= dt;

        // Player movement (WASD + Arrow keys already mapped in Main to these actions)
        InputState in = ctx.inputManager.getState();
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        pv.vx = 0;
        pv.vy = 0;

        float speed = 200f;
        if (in.isPressed(InputAction.MOVE_LEFT))  pv.vx -= speed;
        if (in.isPressed(InputAction.MOVE_RIGHT)) pv.vx += speed;
        if (in.isPressed(InputAction.MOVE_UP))    pv.vy += speed;
        if (in.isPressed(InputAction.MOVE_DOWN))  pv.vy -= speed;

        // Space (ACTION_1) spawns a new obstacle near the player
        if (in.isJustPressed(InputAction.ACTION_1)) {
            TransformComponent pt = player.getComponent(TransformComponent.class);

            float ox = pt.x + (rng.nextFloat() - 0.5f) * 120f;
            float oy = pt.y + (rng.nextFloat() - 0.5f) * 120f;

            spawnObstacle(ox, oy);
            ctx.outputManager.log("SandboxScene", "Spawned obstacle (SPACE)");
        }

        // Keep everything within borders (clamp + bounce)
        for (Entity e : ctx.entityManager.getAll()) {
            if (e != null) keepInsideWindowBounce(e);
        }

        // Submit render commands (draw everything)
        for (Entity e : ctx.entityManager.getAll()) {
            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderableComponent r = e.getComponent(RenderableComponent.class);
            if (t == null || r == null) continue;

            if (r.shape == RenderableComponent.RenderShape.RECT) {
                ctx.renderQueue.submit(RenderCommand.rect(
                        t.x - r.w / 2f, t.y - r.h / 2f, r.w, r.h,
                        r.r, r.g, r.b, r.a
                ));
            } else {
                ctx.renderQueue.submit(RenderCommand.circle(
                        t.x, t.y, r.radius,
                        r.r, r.g, r.b, r.a
                ));
            }
        }
    }

    private void onCollision(CollisionEvent event) {
        Entity a = event.getPair().getA();
        Entity b = event.getPair().getB();

        // NPC hits Player => play sound + bounce both (visible)
        if (isPair(a, b, npc, player)) {
            if (npcHitCooldown <= 0f && hitSound != null) {
                hitSound.play(1.0f); // Sound.play overlays if triggered repeatedly [web:219]
                npcHitCooldown = 0.15f;
            }

            bounceEntity(player);
            bounceEntity(npc);

            ctx.outputManager.log("Collision", "NPC hit player!");
            return;
        }

        // Player hits any obstacle (including newly spawned ones) => bounce player + nudge obstacle
        if (isInPair(a, b, player) && (isObstacle(a) || isObstacle(b))) {
            Entity obstacle = isObstacle(a) ? a : b;

            bounceEntity(player);
            bounceEntity(obstacle);

            ctx.outputManager.log("Collision", "Player hit obstacle!");
        }
    }

    private boolean isObstacle(Entity e) {
        return e != null && e.getComponent(ObstacleTag.class) != null;
    }

    private boolean isPair(Entity a, Entity b, Entity x, Entity y) {
        return (a == x && b == y) || (a == y && b == x);
    }

    private boolean isInPair(Entity a, Entity b, Entity x) {
        return a == x || b == x;
    }

    private void bounceEntity(Entity e) {
        VelocityComponent v = e.getComponent(VelocityComponent.class);
        if (v != null) {
            v.vx = -v.vx;
            v.vy = -v.vy;
        }
    }

    private void spawnObstacle(float x, float y) {
        float radius = 12f + rng.nextInt(10);

        Entity e = ctx.entityManager.create();
        e.addComponent(ObstacleTag.class, new ObstacleTag());

        e.addComponent(TransformComponent.class, new TransformComponent(x, y));

        // Give it some movement so it behaves like an "active obstacle"
        e.addComponent(VelocityComponent.class, new VelocityComponent(
                (rng.nextFloat() - 0.5f) * 110f,
                (rng.nextFloat() - 0.5f) * 110f
        ));

        e.addComponent(ColliderComponent.class, ColliderComponent.circle(radius));
        e.addComponent(RenderableComponent.class, RenderableComponent.circle(radius, 1.0f, 0.35f, 0.35f, 1f));

        // Ensure it never spawns out of bounds
        keepInsideWindowBounce(e);
    }

    private void keepInsideWindowBounce(Entity e) {
        TransformComponent t = e.getComponent(TransformComponent.class);
        ColliderComponent c = e.getComponent(ColliderComponent.class);
        VelocityComponent v = e.getComponent(VelocityComponent.class);

        if (t == null || c == null) return;

        float minX, minY, maxX, maxY;
        if (c.type == ColliderComponent.ShapeType.CIRCLE) {
            minX = c.radius;
            maxX = ctx.config.width - c.radius;
            minY = c.radius;
            maxY = ctx.config.height - c.radius;
        } else { // AABB
            minX = c.halfWidth;
            maxX = ctx.config.width - c.halfWidth;
            minY = c.halfHeight;
            maxY = ctx.config.height - c.halfHeight;
        }

        float oldX = t.x;
        float oldY = t.y;

        // clamp
        t.x = Math.max(minX, Math.min(maxX, t.x));
        t.y = Math.max(minY, Math.min(maxY, t.y));

        // bounce on impact
        if (v != null) {
            if (t.x != oldX) v.vx = -v.vx;
            if (t.y != oldY) v.vy = -v.vy;
        }
    }

    @Override
    public void onUnload() {
        if (hitSound != null) {
            hitSound.dispose();
            hitSound = null;
        }
    }
}
