package com.example.app.demo.scenes;
 
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.app.demo.render.LibGdxRenderer;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputState;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.RenderableComponent;
import com.example.app.engine.scene.AbstractBaseScene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class TrainScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private final int level;
    private final int initialNpcCount;
    private final float timeLimit;

    private Entity player;
    private final List<Entity> npcs = new ArrayList<>();
    private final Random rng = new Random();

    private SpriteBatch hudBatch;
    private BitmapFont font;

    private float timer = 0f;
    private boolean won = false;
    private boolean lost = false;
    private boolean gameStarted = false;
    private float postGameTimer = 0f;

    public TrainScene(EngineContext ctx, int level) {
        this.ctx = ctx;
        this.level = level;
        this.initialNpcCount = Math.round(30f + ((level - 1) * (20f / 9f)));
        this.timeLimit = 20f - ((level - 1) * (10f / 9f));
    }

    @Override
    public void onLoad() {
        hudBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // Load textures
        ctx.renderer.loadTexture("mrt_bg", "mrt_train_side.png");
        ctx.renderer.loadTexture("platform_floor", "waiting_zone_floor_marker.png");
        ctx.renderer.loadTexture("door", "train_door.png");
        ctx.renderer.loadTexture("player", "player_down.png");

        for (int i = 1; i <= 24; i++) {
            String key = String.format("passenger_npc_%02d.png", i);
            ctx.renderer.loadTexture(key, key);
        }
        
        ctx.ioManager.playSound("alert.mp3"); // Play sound (no dedicated voice clip provided, using what is there)
        ctx.ioManager.log("TrainScene", "Loaded (NPCs=" + initialNpcCount + ")");
        setupWorld();
    }

    private void setupWorld() {
        // Player (Outside the train, bottom center)
        player = ctx.playerFactory.create(120f, 80f, "player");
        
        // MRT Walls (Top half of the screen bounds)
        float trainYBase = 240f;

        // Left wall
        Entity wallL = ctx.entityManager.create();
        wallL.addComponent(TransformComponent.class, new TransformComponent(160, trainYBase + 120));
        wallL.addComponent(ColliderComponent.class, ColliderComponent.aabb(160, 120));
 
        // Right wall
        Entity wallR = ctx.entityManager.create();
        wallR.addComponent(TransformComponent.class, new TransformComponent(640 - 160, trainYBase + 120));
        wallR.addComponent(ColliderComponent.class, ColliderComponent.aabb(160, 120));

        // Spawn NPCs inside the train (y > trainYBase + 40)
        for (int i = 0; i < initialNpcCount; i++) {
            spawnNPC(
                    60 + rng.nextInt(640 - 120),
                    trainYBase + 60 + rng.nextInt(120));
        }

        ctx.collisionEvents.subscribe(this::onCollision);
    }

    private void spawnNPC(float x, float y) {
        String textureKey = String.format("passenger_npc_%02d.png", rng.nextInt(24) + 1);
 
        // Use factory to create the NPC with correct components
        Entity npc = ctx.enemyFactory.create(x, y, textureKey);
 
        // Set initial velocity toward the door
        float targetX = 320f + (rng.nextFloat() - 0.5f) * 40f;
        float targetY = 160f;
        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float speed = 40f + rng.nextFloat() * 40f;
 
        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        v.vx = (dx / dist) * speed;
        v.vy = (dy / dist) * speed;
 
        npcs.add(npc);
    }

    @Override
    public void update(float dt) {
        if (!gameStarted) {
            timer += dt;
            if (timer > 2f) { // wait 2 seconds before game starts/npcs move
                gameStarted = true;
                timer = 0f;
            } else {
                return;
            }
        }

        if (won || lost) {
            postGameTimer += dt;
            if (postGameTimer > 2.5f) {
                if (won) {
                    LevelSelectScene.maxUnlockedLevel = Math.max(LevelSelectScene.maxUnlockedLevel, level + 1);
                }
                ctx.sceneManager.switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 0.6f));
            }
            return; // don't update player/NPCs while transitioning
        } else {
            timer += dt;
            if (timer > timeLimit) {
                lost = true;
                ctx.ioManager.getOutputHandler().stopSound();
                ctx.ioManager.playSound("level_fail.mp3"); // play fail sound
            }
        }

        InputState in = ctx.ioManager.getInputHandler().getState();
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        pv.vx = 0;
        pv.vy = 0;

        float speed = 150f;
        if (in.isPressed(InputAction.MOVE_LEFT)) pv.vx -= speed;
        if (in.isPressed(InputAction.MOVE_RIGHT)) pv.vx += speed;
        if (in.isPressed(InputAction.MOVE_UP)) pv.vy += speed;
        if (in.isPressed(InputAction.MOVE_DOWN)) pv.vy -= speed;
        
        // Check win condition: player is inside train
        TransformComponent pTransform = player.getComponent(TransformComponent.class);
        if (pTransform.y > 280f && Math.abs(pTransform.x - 320) < 60) {
            won = true;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_clear.mp3"); // play clear sound
        }
 
        // NPC logic — use explicit list, no component sniffing
        List<Entity> toRemove = new ArrayList<>();
        for (Entity npc : npcs) {
            TransformComponent t = npc.getComponent(TransformComponent.class);
            VelocityComponent v = npc.getComponent(VelocityComponent.class);
            if (t == null || v == null) continue;
 
            if (t.y < 200f) {
                // Exiting train — walk downward, skip bounds clamping
                v.vx = 0;
                v.vy = -60f;
            } else {
                keepInsideBounds(npc, false);
            }
 
            // Destroy once fully offscreen
            if (t.y < 0 || t.y > ctx.config.height || t.x < 0 || t.x > ctx.config.width) {
                ctx.entityManager.destroy(npc);
                toRemove.add(npc);
            }
        }
        npcs.removeAll(toRemove);
 
        keepInsideBounds(player, true);
    }

    @Override
    public void render() {
        // Background
        ctx.renderer.draw("mrt_bg", 320, 360, 0, 640, 240);
        ctx.renderer.draw("platform_floor", 320, 120, 0, 640, 240);
 
        // Through IRenderer
        for (Entity e : ctx.entityManager.getAll()) {
            TransformComponent t = e.getComponent(TransformComponent.class);
            RenderableComponent r = e.getComponent(RenderableComponent.class);
            if (t == null || r == null) continue;
            ctx.renderer.draw(r.renderKey, t.x, t.y, 0, r.width, r.height);
        }
 
        // Flush Sprite draws before switching to HUD text batch
        ctx.renderer.flushSprites();
 
        // HUD text — SpriteBatch only for BitmapFont, not game entities
        hudBatch.begin();
        font.draw(hudBatch, "Level: " + level, 10, 470);
        float timeRemaining = Math.max(0, timeLimit - timer);
        font.draw(hudBatch, "Time Left: " + String.format("%.1f", timeRemaining) + "s", 10, 440);
 
        if (!gameStarted) {
            font.draw(hudBatch, "Please let the alighting passengers\nalight before boarding, thank you.", 100, 300);
        } else if (won) {
            font.setColor(Color.GREEN);
            font.draw(hudBatch, "SUCCESS! Level " + level + " Cleared!", 200, 240);
            if (level == 10) font.draw(hudBatch, "YOU BEAT THE GAME!", 220, 200);
            font.setColor(Color.WHITE);
        } else if (lost) {
            font.setColor(Color.RED);
            font.draw(hudBatch, "TIME'S UP! The door closed.\nTry again!", 200, 240);
            font.setColor(Color.WHITE);
        }
        hudBatch.end();
    }

    private void onCollision(CollisionEvent event) {
        Entity a = event.getPair().getA();
        Entity b = event.getPair().getB();
 
        if (!isPlayerAndNPC(a, b)) return;
 
        Entity npc = a == player ? b : a;
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        VelocityComponent nv = npc.getComponent(VelocityComponent.class);
        TransformComponent pt = player.getComponent(TransformComponent.class);
        TransformComponent nt = npc.getComponent(TransformComponent.class);
 
        float dx = pt.x - nt.x;
        float dy = pt.y - nt.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) dist = 0.001f;
 
        float pushForce = 10f + ((level - 1) * 2.22f);
 
        pt.x += (dx / dist) * pushForce;
        pt.y += (dy / dist) * pushForce;
        nt.x -= (dx / dist) * pushForce;
        nt.y -= (dy / dist) * pushForce;
 
        if (nv != null) {
            nv.vx = -nv.vx * 1.5f;
            nv.vy = -nv.vy * 1.5f;
        }
        if (pv != null) {
            pv.vx += (dx / dist) * pushForce * 10f;
            pv.vy += (dy / dist) * pushForce * 10f;
        }
    }

    private boolean isPlayerAndNPC(Entity a, Entity b) {
        if (a == player) return npcs.contains(b);
        if (b == player) return npcs.contains(a);
        return false;
    }
 
    private void keepInsideBounds(Entity e, boolean isPlayer) {
        TransformComponent t = e.getComponent(TransformComponent.class);
        ColliderComponent c = e.getComponent(ColliderComponent.class);
        VelocityComponent v = e.getComponent(VelocityComponent.class);
        if (t == null || c == null) return;
 
        float minX, minY, maxX, maxY;
        if (c.type == ColliderComponent.ColShapeType.CIRCLE) {
            minX = c.radius;
            maxX = ctx.config.width - c.radius;
            minY = c.radius;
            maxY = ctx.config.height - c.radius;
        } else {
            minX = c.halfWidth;
            maxX = ctx.config.width - c.halfWidth;
            minY = c.halfHeight;
            maxY = ctx.config.height - c.halfHeight;
        }
 
        // Prevent player from walking through train walls via the door gap
        if (isPlayer && t.y > 220f) {
            if (t.x < 320 - 40 || t.x > 320 + 40) {
                t.y = 220f;
            }
        }
 
        float oldX = t.x, oldY = t.y;
        t.x = Math.max(minX, Math.min(maxX, t.x));
        t.y = Math.max(minY, Math.min(maxY, t.y));
 
        if (v != null) {
            if (t.x != oldX)
                v.vx = -v.vx;
            if (t.y != oldY)
                v.vy = -v.vy;
        }
    }

    @Override
    public void onUnload() {
        if (hudBatch != null) hudBatch.dispose();
        if (font != null) font.dispose();
        // Textures live in the renderer — unload the ones we registered
        ctx.renderer.unloadTexture("mrt_bg");
        ctx.renderer.unloadTexture("platform_floor");
        ctx.renderer.unloadTexture("player");
        for (int i = 1; i <= 24; i++) {
            ctx.renderer.unloadTexture(String.format("passenger_npc_%02d.png", i));
        }
        npcs.clear();
    }
}
