package com.example.app.demo.game;

import com.badlogic.gdx.math.MathUtils;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.EngineColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPCController {

    private final EngineContext ctx;
    private final List<Entity> npcs = new ArrayList<>();
    private final Random rng = new Random();

    private final float W, H, doorY;
    private final int level;

    // Pushy NPC
    private static final int PUSHY_NPC_COUNT = 3;
    // Wandering NPC
    private static final float WANDER_TURN_CHANCE = 0.02f;

    private static final EngineColor COL_SHADOW = new EngineColor(0.60f, 0.59f, 0.57f, 1f);
    private static final EngineColor COL_NPC_RED = new EngineColor(0.77f, 0.36f, 0.36f, 1f);
    private static final EngineColor COL_NPC_RED_HI = new EngineColor(0.85f, 0.44f, 0.44f, 1f);
    private static final EngineColor COL_NPC_ORG = new EngineColor(0.72f, 0.44f, 0.25f, 1f);
    private static final EngineColor COL_NPC_ORG_HI = new EngineColor(0.80f, 0.50f, 0.31f, 1f);

    public NPCController(EngineContext ctx, float W, float H, float doorY, int level) {
        this.ctx = ctx;
        this.W = W;
        this.H = H;
        this.doorY = doorY;
        this.level = level;
    }

    public void spawnOne(float x, float y) {
        Entity npc = ctx.getFactory("npc").create(x, y, "NPC");
        float angle = MathUtils.PI + (rng.nextFloat() - 0.5f) * MathUtils.PI;
        float speed = 60f + rng.nextFloat() * 40f;
        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        v.vx = MathUtils.cos(angle) * speed;
        v.vy = MathUtils.sin(angle) * speed;
        npcs.add(npc);
    }

    public void updateAll(Entity player) {
        for (int i = 0; i < npcs.size(); i++) {
            Entity npc = npcs.get(i);
            TransformComponent t = npc.getComponent(TransformComponent.class);
            VelocityComponent v = npc.getComponent(VelocityComponent.class);
            if (t == null || v == null)
                continue;

            if (i % PUSHY_NPC_COUNT == 0) {
                updatePushy(t, v, player);
            } else {
                updateWander(v);
            }

            keepInsideBounds(npc);
            if (v.vy > 0)
                v.vy = -v.vy; // NPCs always move downwards
        }
    }

    private void updatePushy(TransformComponent t, VelocityComponent v, Entity player) {
        if (player == null)
            return;
        TransformComponent pt = player.getComponent(TransformComponent.class);
        float dx = pt.x - t.x;
        float dy = pt.y - t.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            float speed = 60f + level * 5f;
            v.vx = (dx / dist) * speed;
            v.vy = (dy / dist) * speed;
        }
    }

    private void updateWander(VelocityComponent v) {
        if (rng.nextFloat() < WANDER_TURN_CHANCE) {
            float angle = rng.nextFloat() * MathUtils.PI2;
            float speed = 40f + rng.nextFloat() * 40f;
            v.vx = MathUtils.cos(angle) * speed;
            v.vy = MathUtils.sin(angle) * speed;
        }
    }

    private void keepInsideBounds(Entity npc) {
        TransformComponent t = npc.getComponent(TransformComponent.class);
        ColliderComponent c = npc.getComponent(ColliderComponent.class);
        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        if (t == null || c == null)
            return;

        float r = (c.type == ColliderComponent.ColShapeType.CIRCLE)
                ? c.radius
                : Math.max(c.halfWidth, c.halfHeight);

        float oldX = t.x, oldY = t.y;
        t.x = Math.max(r, Math.min(W - r, t.x));
        t.y = Math.max(r, Math.min(H - doorY - r, t.y));

        if (v != null) {
            if (t.x != oldX)
                v.vx = -v.vx;
            if (t.y != oldY)
                v.vy = -v.vy;
        }
    }

    public void applyCollisionPush(Entity npc, float dx, float dy, float dist, int level) {
        TransformComponent nt = npc.getComponent(TransformComponent.class);
        VelocityComponent nv = npc.getComponent(VelocityComponent.class);
        float push = 10f + ((level - 1) * 2.22f);
        nt.x -= (dx / dist) * push;
        nt.y -= (dy / dist) * push;
        if (nv != null) {
            nv.vx = -nv.vx * 1.5f;
            nv.vy = -nv.vy * 1.5f;
        }
    }

    public void drawAll() {
        for (int i = 0; i < npcs.size(); i++) {
            TransformComponent t = npcs.get(i).getComponent(TransformComponent.class);
            if (t == null)
                continue;
            boolean orange = i % 2 == 0;
            ctx.getRenderer().drawCircle(t.x + 2f, t.y - 2f, 14f, COL_SHADOW);
            ctx.getRenderer().drawCircle(t.x, t.y, 13f, orange ? COL_NPC_ORG : COL_NPC_RED);
            ctx.getRenderer().drawCircle(t.x, t.y, 7f, orange ? COL_NPC_ORG_HI : COL_NPC_RED_HI);
        }
    }

    public void destroyAll() {
        for (Entity npc : npcs)
            ctx.getEntityManager().destroy(npc);
        npcs.clear();
    }

    public List<Entity> getAll() {
        return npcs;
    }
}