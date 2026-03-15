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

    private static final float[] DOOR_X = { 85f, 255f, 340f, 425f, 595f };
    private static final float DOOR_Y = 105f;
    private int npcsRemaining;
    private float spawnTimer = 0f;
    private static final float spawnInterval = 0.4f;

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
        font.getData().setScale(2f);
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

        float angle = rng.nextFloat() * MathUtils.PI2;
        float speed = 40f + rng.nextFloat() * 40f;

        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        v.vx = MathUtils.cos(angle) * speed;
        v.vy = MathUtils.sin(angle) * speed;

        npcs.add(npc);
    }

    @Override
    public void update(float dt) {
        if (!gameStarted) {
            introTimer += dt;
            if (introTimer > 2f) { // wait 2 seconds before game starts/npcs move
                gameStarted = true;
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
            gameTimer += dt;
            if (gameTimer > timeLimit) {
                lost = true;
                ctx.ioManager.getOutputHandler().stopSound();
                ctx.ioManager.playSound("level_fail.mp3");
            }

            if (npcsRemaining > 0) {
                spawnTimer += dt;
                if (spawnTimer >= spawnInterval) {
                    spawnTimer = 0f;
                    float doorX = DOOR_X[rng.nextInt(DOOR_X.length)];
                    spawnNPC(doorX, ctx.config.height - DOOR_Y + 50f);
                    npcsRemaining--;
                }
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
        for (float doorX : DOOR_X) {
            if (Math.abs(pTransform.x - doorX) < 30f && pTransform.y > ctx.config.height - DOOR_Y - 30f) {
                nearDoor = true;
                break;
            }
        }
        if (nearDoor) {
            won = true;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_clear.mp3");
        }
 
        // NPC logic — use explicit list, no component sniffing
        for (Entity npc : npcs) {
            TransformComponent t = npc.getComponent(TransformComponent.class);
            VelocityComponent v = npc.getComponent(VelocityComponent.class);
            if (t == null || v == null) continue;
 
            // Randomly nudge direction
            if (rng.nextFloat() < 0.02f) {
                float angle = rng.nextFloat() * MathUtils.PI2;
                float npcSpeed = 40f + rng.nextFloat() * 40f;
                v.vx = MathUtils.cos(angle) * npcSpeed;
                v.vy = MathUtils.sin(angle) * npcSpeed;
            }

            keepInsideBounds(npc, false);
        }

        keepInsideBounds(player, true);
    }

    @Override
    public void render() {
        float W = ctx.config.width;
        float H = ctx.config.height;

        // --- 1. Platform floor ---
        ctx.renderer.drawRect(0, 0, W, H, new Color(0.78f, 0.78f, 0.78f, 1f));

        // Floor tile grid
        Color gridColor = new Color(0.72f, 0.72f, 0.72f, 1f);
        for (float y = 80f; y < H - DOOR_Y; y += 80f)
            ctx.renderer.drawLine(0, y, W, y, gridColor);
        for (float x = 80f; x < W; x += 80f)
            ctx.renderer.drawLine(x, 0, x, H - DOOR_Y, gridColor);

        // --- 2. NPCs drawn BEFORE train so train covers them ---
        Color shadow   = new Color(0.65f, 0.65f, 0.65f, 1f);
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

        // --- 3. Train drawn ON TOP to mask NPCs still inside ---
        // Train body
        ctx.renderer.drawRect(0, H - DOOR_Y, W, DOOR_Y, new Color(0.91f, 0.91f, 0.92f, 1f));

        // NS red stripe
        ctx.renderer.drawRect(0, H - 14f, W, 14f, new Color(0.83f, 0.18f, 0.18f, 1f));

        // Train bottom shadow
        ctx.renderer.drawRect(0, H - DOOR_Y, W, 7f, new Color(0.69f, 0.69f, 0.71f, 1f));

        // Carriage dividers
        Color divider = new Color(0.80f, 0.80f, 0.80f, 1f);
        ctx.renderer.drawRect(W * 0.25f - 1f, H - DOOR_Y, 2f, DOOR_Y, divider);
        ctx.renderer.drawRect(W * 0.50f - 1f, H - DOOR_Y, 2f, DOOR_Y, divider);
        ctx.renderer.drawRect(W * 0.75f - 1f, H - DOOR_Y, 2f, DOOR_Y, divider);

        // Door openings — cut holes in the train so NPCs show through
        Color platformFloor = new Color(0.78f, 0.78f, 0.78f, 1f);
        Color doorFrame     = new Color(0.67f, 0.67f, 0.67f, 1f);
        Color doorSill      = new Color(0.96f, 0.77f, 0.09f, 1f);
        for (float doorX : DOOR_X) {
            ctx.renderer.drawRect(doorX - 30f, H - DOOR_Y, 60f, DOOR_Y, platformFloor);
            ctx.renderer.drawRect(doorX - 32f, H - DOOR_Y, 4f,  DOOR_Y, doorFrame);
            ctx.renderer.drawRect(doorX + 28f, H - DOOR_Y, 4f,  DOOR_Y, doorFrame);
            ctx.renderer.drawRect(doorX - 32f, H - DOOR_Y, 64f, 7f,     doorSill);
        }

        // Yellow tactile strip
        ctx.renderer.drawRect(0, H - DOOR_Y - 10f, W, 10f, new Color(0.96f, 0.77f, 0.09f, 0.85f));

        // Tactile bumps
        Color bump = new Color(0.88f, 0.69f, 0.08f, 0.7f);
        for (float x = 30f; x < W; x += 25f)
            ctx.renderer.drawCircle(x, H - DOOR_Y - 5f, 3f, bump);

        // Waiting zone markers
        Color marker = new Color(0.83f, 0.18f, 0.18f, 0.5f);
        for (float doorX : DOOR_X) {
            ctx.renderer.drawRect(doorX - 35f, H - DOOR_Y - 24f, 70f, 4f,  marker);
            ctx.renderer.drawRect(doorX - 35f, H - DOOR_Y - 24f, 4f,  20f, marker);
            ctx.renderer.drawRect(doorX + 31f, H - DOOR_Y - 24f, 4f,  20f, marker);
        }

        // --- 4. Player always on top ---
        TransformComponent pt = player.getComponent(TransformComponent.class);
        ctx.renderer.drawCircle(pt.x + 2f, pt.y - 2f, 18f, shadow);
        ctx.renderer.drawCircle(pt.x, pt.y, 16f, new Color(0.10f, 0.54f, 0.43f, 1f));
        ctx.renderer.drawCircle(pt.x, pt.y, 10f, new Color(0.13f, 0.67f, 0.53f, 1f));

        // HUD
        float timeRemaining = Math.max(0, timeLimit - gameTimer);
        font.setColor(Color.WHITE);
        ctx.renderer.drawText(font, "LVL " + level, 20, H - 20f);
        font.setColor(new Color(0.96f, 0.77f, 0.09f, 1f));
        ctx.renderer.drawText(font, String.format("%.1fs", timeRemaining), W - 80f, H - 20f);
        font.setColor(new Color(0.85f, 0.44f, 0.44f, 1f));
        ctx.renderer.drawText(font, "NPC: " + npcs.size(), W / 2f - 30f, H - 20f);

        if (!gameStarted) {
            font.setColor(new Color(0.96f, 0.77f, 0.09f, 1f));
            ctx.renderer.drawText(font, "Let passengers alight first!", 140, H / 2f);
        } else if (won) {
            font.setColor(new Color(0.13f, 0.67f, 0.53f, 1f));
            ctx.renderer.drawText(font, "CLEARED!", W / 2f - 60f, H / 2f);
            if (level == 10) ctx.renderer.drawText(font, "YOU BEAT THE GAME!", W / 2f - 120f, H / 2f - 40f);
        } else if (lost) {
            font.setColor(new Color(0.83f, 0.18f, 0.18f, 1f));
            ctx.renderer.drawText(font, "TIME'S UP!", W / 2f - 60f, H / 2f);
        }
        font.setColor(Color.WHITE);
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
            maxY = isPlayer ? ctx.config.height - c.radius : ctx.config.height - DOOR_Y - c.radius;
        } else {
            minX = c.halfWidth;
            maxX = ctx.config.width - c.halfWidth;
            minY = c.halfHeight;
            maxY = isPlayer ? ctx.config.height - c.halfHeight : ctx.config.height - DOOR_Y - c.halfHeight;
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
        if (collisionSub != null) collisionSub.cancel();
        if (font != null) font.dispose();
        for (Entity npc : npcs) ctx.entityManager.destroy(npc);
        npcs.clear();
    }
}
