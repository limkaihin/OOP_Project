package com.example.app.demo.scenes;
 
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputState;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.util.EventBus;

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

    private BitmapFont font;

    private static final float doorX = 320f;
    private static final float DOOR_Y = 105f;
    private int npcsRemaining;
    private float spawnTimer = 0f;
    private static final float spawnInterval = 0.4f;

    private int lives = 3;
    private float doorOpenAmount = 0f;    // 0 = closed, 1 = fully open
    private boolean doorsOpen = false;
    private static final float DOOR_OPEN_SPEED = 1.5f;
    private static final float DOOR_WIDTH = 50f; // half width of door opening

    private float introTimer= 0f;
    private float gameTimer= 0f;
    private float postGameTimer = 0f;
    private boolean won = false;
    private boolean lost = false;
    private boolean gameStarted = false;

    private EventBus<CollisionEvent>.Subscription collisionSub;

    public TrainScene(EngineContext ctx, int level) {
        this.ctx = ctx;
        this.level = level;
        this.initialNpcCount = Math.round(30f + ((level - 1) * (20f / 9f)));
        this.timeLimit = 20f - ((level - 1) * (10f / 9f));
    }

    @Override
    public void onLoad() {
        font = new BitmapFont();
        font.getData().setScale(1.2f);
        font.setColor(Color.WHITE);

        ctx.ioManager.playSound("alert.mp3");
        ctx.ioManager.log("TrainScene", "Loaded (NPCs=" + initialNpcCount + ")");
        setupWorld();
    }

    private void setupWorld() {
        npcsRemaining = initialNpcCount;
        player = ctx.playerFactory.create(320f, 80f, "player");
        collisionSub = ctx.collisionEvents.subscribe(this::onCollision);
    }

    private void spawnNPC(float x, float y) {
        Entity npc = ctx.enemyFactory.create(x, y, "NPC");

        // Force initial direction downward into the platform
        float angle = MathUtils.PI + (rng.nextFloat() - 0.5f) * MathUtils.PI; // downward arc only
        float speed = 60f + rng.nextFloat() * 40f;

        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        v.vx = MathUtils.cos(angle) * speed;
        v.vy = MathUtils.sin(angle) * speed;

        npcs.add(npc);
    }

    @Override
    public void update(float dt) {
        if (!gameStarted) {
            introTimer += dt;
            // Animate doors opening during intro
            doorOpenAmount = Math.min(1f, introTimer / 2f);
            if (introTimer > 2f) {
                gameStarted = true;
                doorsOpen = true;
            } else {
                return;
            }
        }

        if (won || lost) {
            // Close doors on win/loss
            doorOpenAmount = Math.max(0f, doorOpenAmount - dt * DOOR_OPEN_SPEED);
            postGameTimer += dt;
            if (postGameTimer > 2.5f) {
                if (won) {
                    LevelSelectScene.maxUnlockedLevel = Math.max(LevelSelectScene.maxUnlockedLevel, level + 1);
                }
                ctx.sceneManager.switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 0.6f));
            }
            return;
        }

        // Close doors warning when time is low
        if (timeLimit - gameTimer < 5f) {
            doorOpenAmount = Math.max(0.2f, doorOpenAmount - dt * 0.3f);
        }

        gameTimer += dt;
        if (gameTimer > timeLimit) {
            lost = true;
            doorsOpen = false;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_fail.mp3");
        }

        if (npcsRemaining > 0) {
            spawnTimer += dt;
            if (spawnTimer >= spawnInterval) {
                spawnTimer = 0f;
                spawnNPC(320f, ctx.config.height - DOOR_Y + 50f);
                npcsRemaining--;
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
        boolean nearDoor = false;
        if (Math.abs(pTransform.x - doorX) < 30f && pTransform.y > ctx.config.height - DOOR_Y - 30f) {
            nearDoor = true;
        }

        if (nearDoor) {
            won = true;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_clear.mp3");
        }
 
        // NPC logic
        for (int i = 0; i < npcs.size(); i++) {
            Entity npc = npcs.get(i);
            TransformComponent t = npc.getComponent(TransformComponent.class);
            VelocityComponent v = npc.getComponent(VelocityComponent.class);
            if (t == null || v == null) continue;

            boolean isPushy = (i % 3 == 0); // every 3rd NPC is pushy

            if (isPushy) {
                // Home toward player
                TransformComponent pt = player.getComponent(TransformComponent.class);
                float dx = pt.x - t.x;
                float dy = pt.y - t.y;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist > 0) {
                    float chaseSpeed = 60f + level * 5f;
                    v.vx = (dx / dist) * chaseSpeed;
                    v.vy = (dy / dist) * chaseSpeed;
                }
            } else {
                if (rng.nextFloat() < 0.02f) {
                    float angle = rng.nextFloat() * MathUtils.PI2;
                    float npcSpeed = 40f + rng.nextFloat() * 40f;
                    v.vx = MathUtils.cos(angle) * npcSpeed;
                    v.vy = MathUtils.sin(angle) * npcSpeed;
                }
            }

            keepInsideBounds(npc, false);
            if (v.vy > 0) v.vy = -v.vy;
        }

        keepInsideBounds(player, true);
    }

    @Override
    public void renderHud() {
        float W = ctx.config.width;
        float H = ctx.config.height;
        float timeRemaining = Math.max(0, timeLimit - gameTimer);

        // Dark HUD bar at bottom
        ctx.renderer.drawRect(0, 0, W, 42f, new Color(0.10f, 0.10f, 0.16f, 0.88f));

        // Lives
        for (int i = 0; i < 3; i++) {
            Color heartColor = i < lives
                ? new Color(0.83f, 0.18f, 0.18f, 1f)
                : new Color(0.35f, 0.35f, 0.40f, 1f);
            ctx.renderer.drawCircle(20f + i * 22f, 21f, 9f, heartColor);
        }

        // Text
        // NPC Count
        font.setColor(new Color(0.85f, 0.44f, 0.44f, 1f));
        ctx.renderer.drawText(font, "NPC: " + npcs.size(), W / 4f, 28f);

        // Level
        font.setColor(Color.WHITE);
        ctx.renderer.drawText(font, "LVL " + level, W / 2f - 30f, 28f);

        // Timer
        font.setColor(new Color(0.96f, 0.77f, 0.09f, 1f));
        ctx.renderer.drawText(font, String.format("%.1fs", timeRemaining), W - 80f, 28f);

        // Mid screen messages
        if (!gameStarted) {
            ctx.renderer.drawRect(80f, H / 2f - 30f, W - 160f, 44f, new Color(0.10f, 0.10f, 0.16f, 0.75f));
            font.setColor(new Color(0.96f, 0.77f, 0.09f, 1f));
            ctx.renderer.drawText(font, "Avoid Passengers and Board the Train!", 120f, H / 2f);
        } else if (won) {
            ctx.renderer.drawRect(200f, H / 2f - 30f, 280f, 44f, new Color(0.10f, 0.10f, 0.16f, 0.75f));
            font.setColor(new Color(0.13f, 0.67f, 0.53f, 1f));
            ctx.renderer.drawText(font, "CLEARED!", W / 2f - 60f, H / 2f);
            if (level == 10) ctx.renderer.drawText(font, "YOU BEAT THE GAME!", W / 2f - 120f, H / 2f - 40f);
        } else if (lost) {
            ctx.renderer.drawRect(210f, H / 2f - 30f, 260f, 44f, new Color(0.10f, 0.10f, 0.16f, 0.75f));
            font.setColor(new Color(0.83f, 0.18f, 0.18f, 1f));
            ctx.renderer.drawText(font, "YOU BUMPER CAR!", W / 2f - 60f, H / 2f);
        }

        font.setColor(Color.WHITE);
    } 

    @Override
    public void render() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        // --- 1. Platform floor - warmer grey ---
        ctx.renderer.drawRect(0, 0, W, H, new Color(0.83f, 0.81f, 0.78f, 1f));

        // Floor tile grid
        Color gridColor = new Color(0.75f, 0.73f, 0.70f, 1f);
        for (float y = 80f; y < H - DOOR_Y; y += 80f)
            ctx.renderer.drawLine(0, y, W, y, gridColor);
        for (float x = 80f; x < W; x += 80f)
            ctx.renderer.drawLine(x, 0, x, H - DOOR_Y, gridColor);

        // --- 2. NPCs before train ---
        Color shadow   = new Color(0.60f, 0.59f, 0.57f, 1f);
        Color npcRed   = new Color(0.77f, 0.36f, 0.36f, 1f);
        Color npcRedHi = new Color(0.85f, 0.44f, 0.44f, 1f);
        Color npcOrg   = new Color(0.72f, 0.44f, 0.25f, 1f);
        Color npcOrgHi = new Color(0.80f, 0.50f, 0.31f, 1f);
        for (int i = 0; i < npcs.size(); i++) {
            TransformComponent t = npcs.get(i).getComponent(TransformComponent.class);
            if (t == null) continue;
            boolean orange = i % 2 == 0;
            ctx.renderer.drawCircle(t.x + 2f, t.y - 2f, 14f, shadow);
            ctx.renderer.drawCircle(t.x, t.y, 13f, orange ? npcOrg   : npcRed);
            ctx.renderer.drawCircle(t.x, t.y,  7f, orange ? npcOrgHi : npcRedHi);
        }

        // Train body - white/cream
        ctx.renderer.drawRect(0, H - DOOR_Y, W, DOOR_Y, new Color(0.95f, 0.94f, 0.92f, 1f));

        // Dark lower panel (black band below windows)
        ctx.renderer.drawRect(0, H - DOOR_Y, W, 28f, new Color(0.12f, 0.12f, 0.14f, 1f));

        // Red stripe
        ctx.renderer.drawRect(0, H - DOOR_Y + 28f, W, 12f, new Color(0.83f, 0.15f, 0.15f, 1f));

        // Train bottom shadow
        ctx.renderer.drawRect(0, H - DOOR_Y, W, 4f, new Color(0.50f, 0.50f, 0.52f, 1f));

        // Left windows
        ctx.renderer.drawRect(55f,  415f, 80f, 48f, new Color(0.15f, 0.15f, 0.18f, 1f));
        ctx.renderer.drawRect(58f,  418f, 74f, 42f, new Color(0.35f, 0.55f, 0.65f, 1f));
        ctx.renderer.drawRect(58f,  450f, 22f, 8f,  new Color(0.60f, 0.78f, 0.88f, 0.7f));

        ctx.renderer.drawRect(155f, 415f, 80f, 48f, new Color(0.15f, 0.15f, 0.18f, 1f));
        ctx.renderer.drawRect(158f, 418f, 74f, 42f, new Color(0.35f, 0.55f, 0.65f, 1f));
        ctx.renderer.drawRect(158f, 450f, 22f, 8f,  new Color(0.60f, 0.78f, 0.88f, 0.7f));

        // Right windows
        ctx.renderer.drawRect(395f, 415f, 80f, 48f, new Color(0.15f, 0.15f, 0.18f, 1f));
        ctx.renderer.drawRect(398f, 418f, 74f, 42f, new Color(0.35f, 0.55f, 0.65f, 1f));
        ctx.renderer.drawRect(398f, 450f, 22f, 8f,  new Color(0.60f, 0.78f, 0.88f, 0.7f));

        ctx.renderer.drawRect(495f, 415f, 80f, 48f, new Color(0.15f, 0.15f, 0.18f, 1f));
        ctx.renderer.drawRect(498f, 418f, 74f, 42f, new Color(0.35f, 0.55f, 0.65f, 1f));
        ctx.renderer.drawRect(498f, 450f, 22f, 8f,  new Color(0.60f, 0.78f, 0.88f, 0.7f));

        // Door opening - darker than platform to show depth
        ctx.renderer.drawRect(doorX - DOOR_WIDTH - 4f, H - DOOR_Y, DOOR_WIDTH * 2f + 8f, DOOR_Y, new Color(0.73f, 0.73f, 0.75f, 1f));

        // Animated door panels
        float panelWidth = DOOR_WIDTH * (1f - doorOpenAmount);
        Color doorPanel = new Color(0.82f, 0.82f, 0.84f, 1f);
        Color doorPanelLine = new Color(0.75f, 0.75f, 0.77f, 1f);
        Color doorFrame = new Color(0.53f, 0.53f, 0.56f, 1f);
        ctx.renderer.drawRect(doorX - DOOR_WIDTH, H - DOOR_Y, panelWidth, DOOR_Y - 8f, doorPanel);
        ctx.renderer.drawRect(doorX - DOOR_WIDTH + panelWidth * 0.6f, H - DOOR_Y, 2f, DOOR_Y - 8f, doorPanelLine);
        ctx.renderer.drawRect(doorX + DOOR_WIDTH - panelWidth, H - DOOR_Y, panelWidth, DOOR_Y - 8f, doorPanel);
        ctx.renderer.drawRect(doorX + DOOR_WIDTH - panelWidth * 0.4f, H - DOOR_Y, 2f, DOOR_Y - 8f, doorPanelLine);

        // Door frames - darker
        ctx.renderer.drawRect(doorX - DOOR_WIDTH - 6f, H - DOOR_Y, 6f, DOOR_Y, doorFrame);
        ctx.renderer.drawRect(doorX + DOOR_WIDTH,       H - DOOR_Y, 6f, DOOR_Y, doorFrame);

        // Door sill
        ctx.renderer.drawRect(doorX - DOOR_WIDTH - 6f, H - DOOR_Y, DOOR_WIDTH * 2f + 12f, 9f, new Color(0.96f, 0.77f, 0.09f, 1f));

        // Yellow tactile strip
        ctx.renderer.drawRect(0, H - DOOR_Y - 10f, W, 10f, new Color(0.94f, 0.75f, 0.06f, 1f));
        Color bump = new Color(0.83f, 0.65f, 0.04f, 1f);
        for (float x = 20f; x < W; x += 25f)
            ctx.renderer.drawCircle(x, H - DOOR_Y - 5f, 3.5f, bump);

        // Waiting zone markers - wider to match door
        Color marker = new Color(0.83f, 0.18f, 0.18f, 0.7f);
        ctx.renderer.drawRect(doorX - DOOR_WIDTH - 6f, H - DOOR_Y - 26f, DOOR_WIDTH * 2f + 12f, 5f, marker);
        ctx.renderer.drawRect(doorX - DOOR_WIDTH - 6f, H - DOOR_Y - 26f, 5f, 22f, marker);
        ctx.renderer.drawRect(doorX + DOOR_WIDTH + 1f,  H - DOOR_Y - 26f, 5f, 22f, marker);

        // --- 4. Player ---
        TransformComponent pt = player.getComponent(TransformComponent.class);
        ctx.renderer.drawCircle(pt.x + 2f, pt.y - 2f, 18f, shadow);
        ctx.renderer.drawCircle(pt.x, pt.y, 16f, new Color(0.10f, 0.54f, 0.43f, 1f));
        ctx.renderer.drawCircle(pt.x, pt.y, 10f, new Color(0.13f, 0.67f, 0.53f, 1f));
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

        // Lose a life
        lives--;
        if (lives <= 0) {
            lost = true;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_fail.mp3");
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

        float radius = (c.type == ColliderComponent.ColShapeType.CIRCLE)
            ? c.radius : Math.max(c.halfWidth, c.halfHeight);

        float minX = radius;
        float maxX = ctx.config.width - radius;
        float minY = radius;
        float maxY = ctx.config.height - radius;

        if (!isPlayer) {
            // NPCs stay on platform, cant re-enter train
            maxY = ctx.config.height - DOOR_Y - radius;
        } else {
            // Player blocked by train wall everywhere except door gap
            float platformCeiling = ctx.config.height - DOOR_Y - radius;
            boolean inDoorGap = Math.abs(t.x - doorX) < DOOR_WIDTH - radius;
            if (t.y > platformCeiling && !inDoorGap) {
                t.y = platformCeiling;
                if (v != null) v.vy = 0;
            }
        }

        float oldX = t.x, oldY = t.y;
        t.x = Math.max(minX, Math.min(maxX, t.x));
        t.y = Math.max(minY, Math.min(maxY, t.y));

        if (v != null) {
            if (t.x != oldX) v.vx = -v.vx;
            if (t.y != oldY) v.vy = -v.vy;
        }
    }

    @Override
    public void onUnload() {
        if (collisionSub != null) collisionSub.cancel();
        if (font != null) font.dispose();
        for (Entity npc : npcs) ctx.entityManager.destroy(npc);
        npcs.clear();
    }
}
