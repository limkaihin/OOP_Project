package com.example.app.demo.scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.example.app.demo.render.LibGdxFont;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputState;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.EngineColor;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.util.EventBus;

public final class TrainScene extends AbstractBaseScene {
    
    private final EngineContext ctx;
    private LibGdxFont font;
    private LibGdxFont bigFont;
    private final GlyphLayout layout = new GlyphLayout();
    private final Random rng = new Random();

    private final float W, H;

    // Entities
    private Entity player;
    private final List<Entity> npcs = new ArrayList<>();
    private EventBus<CollisionEvent>.Subscription collisionSub;

    // Level Info
    private final int level;
    private final int initialNpcCount;
    private int npcsRemaining;
    private final float timeLimit;
    private float spawnTimer = 0f;
    private int lives = MAX_LIVES;
    private float doorOpenAmount = 0f;
    private float introTimer = 0f;
    private float gameTimer = 0f;
    private float postGameTimer = 0f;
    private boolean won = false;
    private boolean lost = false;
    private boolean gameStarted = false;

    // Constants
    private static final float DOOR_X = 320f;
    private static final float DOOR_Y = 105f; // Height of train band
    private static final float DOOR_WIDTH = 50f; // Half-width of door gap
    private static final float DOOR_OPEN_SPEED = 1.5f;
    private static final float SPAWN_INTERVAL = 0.4f;
    private static final int MAX_LIVES = 3;

    // Train colours
    private static final EngineColor COL_TRAIN_BODY = new EngineColor(0.95f, 0.94f, 0.92f, 1f);
    private static final EngineColor COL_TRAIN_PANEL = new EngineColor(0.12f, 0.12f, 0.14f, 1f);
    private static final EngineColor COL_TRAIN_STRIPE = new EngineColor(0.83f, 0.15f, 0.15f, 1f);
    private static final EngineColor COL_TRAIN_SHADOW = new EngineColor(0.50f, 0.50f, 0.52f, 1f);
    private static final EngineColor COL_WIN_BORDER = new EngineColor(0.15f, 0.15f, 0.18f, 1f);
    private static final EngineColor COL_WIN_GLASS = new EngineColor(0.35f, 0.55f, 0.65f, 1f);
    private static final EngineColor COL_WIN_SHINE = new EngineColor(0.60f, 0.78f, 0.88f, 0.7f);
    private static final EngineColor COL_DOOR_BG = new EngineColor(0.73f, 0.73f, 0.75f, 1f);
    private static final EngineColor COL_DOOR_PANEL = new EngineColor(0.82f, 0.82f, 0.84f, 1f);
    private static final EngineColor COL_DOOR_LINE = new EngineColor(0.75f, 0.75f, 0.77f, 1f);
    private static final EngineColor COL_DOOR_FRAME = new EngineColor(0.53f, 0.53f, 0.56f, 1f);
    private static final EngineColor COL_DOOR_SILL = new EngineColor(0.96f, 0.77f, 0.09f, 1f);
    private static final EngineColor COL_TACTILE = new EngineColor(0.94f, 0.75f, 0.06f, 1f);
    private static final EngineColor COL_BUMP = new EngineColor(0.83f, 0.65f, 0.04f, 1f);
    private static final EngineColor COL_MARKER = new EngineColor(0.83f, 0.18f, 0.18f, 0.7f);

    // Platform colours
    private static final EngineColor COL_PLATFORM = new EngineColor(0.83f, 0.81f, 0.78f, 1f);
    private static final EngineColor COL_GRID = new EngineColor(0.75f, 0.73f, 0.70f, 1f);
    private static final EngineColor COL_SHADOW = new EngineColor(0.60f, 0.59f, 0.57f, 1f);

    // NPC colours
    private static final EngineColor COL_NPC_RED = new EngineColor(0.77f, 0.36f, 0.36f, 1f);
    private static final EngineColor COL_NPC_RED_HI = new EngineColor(0.85f, 0.44f, 0.44f, 1f);
    private static final EngineColor COL_NPC_ORG = new EngineColor(0.72f, 0.44f, 0.25f, 1f);
    private static final EngineColor COL_NPC_ORG_HI = new EngineColor(0.80f, 0.50f, 0.31f, 1f);

    // Player colours
    private static final EngineColor COL_PLAYER = new EngineColor(0.10f, 0.54f, 0.43f, 1f);
    private static final EngineColor COL_PLAYER_HI = new EngineColor(0.13f, 0.67f, 0.53f, 1f);

    // HUD colours
    private static final EngineColor COL_HUD_BG = new EngineColor(0.10f, 0.10f, 0.16f, 0.88f);
    private static final EngineColor COL_HEART_ON = new EngineColor(0.83f, 0.18f, 0.18f, 1f);
    private static final EngineColor COL_HEART_OFF = new EngineColor(0.35f, 0.35f, 0.40f, 1f);
    private static final EngineColor COL_LOSTTEXT = new EngineColor(0.95f, 0.95f, 0.95f, 1f);
    private static final EngineColor COL_ENDGAMEOVERLAY = new EngineColor(0.15f, 0.15f, 0.15f, 0.65f);

    // Constructor
    public TrainScene(EngineContext ctx, int level) {
        this.ctx = ctx;
        this.level = level;
        this.initialNpcCount = Math.round(10f + ((level - 1) * 2f));
        this.timeLimit = 15f - ((level + 1) * (10f / 9f));
        this.W = ctx.getConfig().width;
        this.H = ctx.getConfig().height;
    }

    // Lifecycle
    @Override
    public void onLoad() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(com.badlogic.gdx.Gdx.files.internal("Oswald-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter smallParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        smallParams.size = 18;
        font = new LibGdxFont(generator.generateFont(smallParams));

        FreeTypeFontGenerator.FreeTypeFontParameter bigParams = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bigParams.size = 72;
        bigFont = new LibGdxFont(generator.generateFont(bigParams));

        generator.dispose();
        font.setColor(EngineColor.WHITE);

        ctx.getIoManager().playSound("alert.mp3");
        ctx.getIoManager().log("TrainScene", "Loaded (NPCs=" + initialNpcCount + ")");
        setupWorld();
    }

    private void setupWorld() {
        npcsRemaining = initialNpcCount;
        player = ctx.getPlayerFactory().create(320f, 80f, "player");
        collisionSub = ctx.getCollisionEvents().subscribe(this::onCollision);
    }

    @Override
    public void onUnload() {
        if (collisionSub != null)
            collisionSub.cancel();
        if (font != null)
            font.dispose();
        if (bigFont != null)
            bigFont.dispose();
        for (Entity npc : npcs)
            ctx.getEntityManager().destroy(npc);
        npcs.clear();
    }

    // Update
    @Override
    public void update(float dt) {
        if (!gameStarted) {
            updateIntro(dt);
            return;
        }
        if (won || lost) {
            updatePostGame(dt);
            return;
        }
        updateGame(dt);
    }

    private void updateIntro(float dt) {
        introTimer += dt;
        doorOpenAmount = Math.min(1f, introTimer / 2f);
        if (introTimer > 2f) {
            gameStarted = true;
        }
    }

    private void updatePostGame(float dt) {
        doorOpenAmount = Math.max(0f, doorOpenAmount - dt * DOOR_OPEN_SPEED);
        postGameTimer += dt;
        if (postGameTimer > 2.5f) {
            if (won) {
                GameProgress.maxUnlockedLevel = Math.max(GameProgress.maxUnlockedLevel, level + 1);
                // Show game-clear screen
                if (level >= 5) {
                    ctx.getSceneManager().switchTo(new GameClearScene(ctx));
                    return;
                }
            }
            ctx.getSceneManager().switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 1.5f));
        }
    }

    private void updateGame(float dt) {
        // Door warning when time is low
        if (timeLimit - gameTimer < 5f) {
            doorOpenAmount = Math.max(0.2f, doorOpenAmount - dt * 0.3f);
        }

        gameTimer += dt;
        if (gameTimer > timeLimit) {
            triggerLoss();
            return;
        }

        updateSpawner(dt);
        updatePlayer();
        updateNPCs();
    }

    private void updateSpawner(float dt) {
        if (npcsRemaining <= 0)
            return;
        spawnTimer += dt;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0f;
            spawnNPC(DOOR_X, H - DOOR_Y + 50f);
            npcsRemaining--;
        }
    }

    private void updatePlayer() {
        if (player == null)
            return;

        InputState in = ctx.getIoManager().getInputHandler().getState();
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        pv.vx = 0;
        pv.vy = 0;

        float speed = 150f;
        if (in.isPressed(InputAction.MOVE_LEFT))
            pv.vx -= speed;
        if (in.isPressed(InputAction.MOVE_RIGHT))
            pv.vx += speed;
        if (in.isPressed(InputAction.MOVE_UP))
            pv.vy += speed;
        if (in.isPressed(InputAction.MOVE_DOWN))
            pv.vy -= speed;

        keepInsideBounds(player, true);
        checkWinCondition();
    }

    private void checkWinCondition() {
        if (player == null)
            return;
        TransformComponent t = player.getComponent(TransformComponent.class);
        boolean nearDoor = Math.abs(t.x - DOOR_X) < 30f
                && t.y > H - DOOR_Y - 30f;
        if (nearDoor)
            triggerWin();
    }

    private void triggerWin() {
        if (won) return;
        won = true;
        ctx.getIoManager().getOutputHandler().stopSound();
        ctx.getIoManager().playSound("level_clear.mp3");
        ctx.getEntityManager().destroy(player);
        player = null;
    }

    private void triggerLoss() {
        if (lost) return;
        lost = true;
        ctx.getIoManager().getOutputHandler().stopSound();
        ctx.getIoManager().playSound("level_fail.mp3");
    }

    private void updateNPCs() {
        for (int i = 0; i < npcs.size(); i++) {
            Entity npc = npcs.get(i);
            TransformComponent t = npc.getComponent(TransformComponent.class);
            VelocityComponent v = npc.getComponent(VelocityComponent.class);
            if (t == null || v == null)
                continue;

            if (i % 3 == 0) {
                updatePushyNPC(t, v);
            } else {
                updateWanderNPC(v);
            }

            keepInsideBounds(npc, false);
            if (v.vy > 0)
                v.vy = -v.vy; // never move upward
        }
    }

    private void updatePushyNPC(TransformComponent t, VelocityComponent v) {
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

    private void updateWanderNPC(VelocityComponent v) {
        if (rng.nextFloat() < 0.02f) {
            float angle = rng.nextFloat() * MathUtils.PI2;
            float speed = 40f + rng.nextFloat() * 40f;
            v.vx = MathUtils.cos(angle) * speed;
            v.vy = MathUtils.sin(angle) * speed;
        }
    }

    private void spawnNPC(float x, float y) {
        Entity npc = ctx.getEnemyFactory().create(x, y, "NPC");
        float angle = MathUtils.PI + (rng.nextFloat() - 0.5f) * MathUtils.PI;
        float speed = 60f + rng.nextFloat() * 40f;
        VelocityComponent v = npc.getComponent(VelocityComponent.class);
        v.vx = MathUtils.cos(angle) * speed;
        v.vy = MathUtils.sin(angle) * speed;
        npcs.add(npc);
    }

    // Render
    @Override
    public void render() {
        drawPlatform(W, H);
        drawNPCs();
        if (player != null)
            drawPlayer();
        drawTrain(W, H);
        drawEndGameOverlay(W, H);
    }

    private void drawPlatform(float W, float H) {
        ctx.getRenderer().drawRect(0, 0, W, H, COL_PLATFORM);
        for (float y = 80f; y < H - DOOR_Y; y += 80f)
            ctx.getRenderer().drawLine(0, y, W, y, COL_GRID);
        for (float x = 80f; x < W; x += 80f)
            ctx.getRenderer().drawLine(x, 0, x, H - DOOR_Y, COL_GRID);
    }

    private void drawNPCs() {
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

    private void drawPlayer() {
        TransformComponent pt = player.getComponent(TransformComponent.class);
        ctx.getRenderer().drawCircle(pt.x + 2f, pt.y - 2f, 18f, COL_SHADOW);
        ctx.getRenderer().drawCircle(pt.x, pt.y, 16f, COL_PLAYER);
        ctx.getRenderer().drawCircle(pt.x, pt.y, 10f, COL_PLAYER_HI);
    }

    private void drawTrain(float W, float H) {
        float trainY = H - DOOR_Y;

        // Main body
        ctx.getRenderer().drawRect(0, trainY, W, DOOR_Y, COL_TRAIN_BODY);
        ctx.getRenderer().drawRect(0, trainY, W, 28f, COL_TRAIN_PANEL);
        ctx.getRenderer().drawRect(0, trainY + 28f, W, 12f, COL_TRAIN_STRIPE);
        ctx.getRenderer().drawRect(0, trainY, W, 4f, COL_TRAIN_SHADOW);

        drawWindows(H);
        drawDoor(trainY);
        drawTactileStrip(W, H);
        drawWaitingMarkers(H);
    }

    private void drawWindows(float H) {
        float[] windowX = { 55f, 155f, 395f, 495f };
        for (float x : windowX) {
            ctx.getRenderer().drawRect(x, H - DOOR_Y + 40f, 80f, 48f, COL_WIN_BORDER);
            ctx.getRenderer().drawRect(x + 3f, H - DOOR_Y + 43f, 74f, 42f, COL_WIN_GLASS);
            ctx.getRenderer().drawRect(x + 3f, H - DOOR_Y + 75f, 22f, 8f, COL_WIN_SHINE);
        }
    }

    private void drawDoor(float trainY) {
        // Background
        ctx.getRenderer().drawRect(DOOR_X - DOOR_WIDTH - 4f, trainY, DOOR_WIDTH * 2f + 8f, DOOR_Y, COL_DOOR_BG);

        // Animated panels
        float panelWidth = DOOR_WIDTH * (1f - doorOpenAmount);
        ctx.getRenderer().drawRect(DOOR_X - DOOR_WIDTH, trainY, panelWidth, DOOR_Y - 8f, COL_DOOR_PANEL);
        ctx.getRenderer().drawRect(DOOR_X - DOOR_WIDTH + panelWidth * 0.6f, trainY, 2f, DOOR_Y - 8f, COL_DOOR_LINE);
        ctx.getRenderer().drawRect(DOOR_X + DOOR_WIDTH - panelWidth, trainY, panelWidth, DOOR_Y - 8f, COL_DOOR_PANEL);
        ctx.getRenderer().drawRect(DOOR_X + DOOR_WIDTH - panelWidth * 0.4f, trainY, 2f, DOOR_Y - 8f, COL_DOOR_LINE);

        // Frames and sill
        ctx.getRenderer().drawRect(DOOR_X - DOOR_WIDTH - 6f, trainY, 6f, DOOR_Y, COL_DOOR_FRAME);
        ctx.getRenderer().drawRect(DOOR_X + DOOR_WIDTH, trainY, 6f, DOOR_Y, COL_DOOR_FRAME);
        ctx.getRenderer().drawRect(DOOR_X - DOOR_WIDTH - 6f, trainY, DOOR_WIDTH * 2f + 12f, 9f, COL_DOOR_SILL);
    }

    private void drawTactileStrip(float W, float H) {
        ctx.getRenderer().drawRect(0, H - DOOR_Y - 10f, W, 10f, COL_TACTILE);
        for (float x = 20f; x < W; x += 25f)
            ctx.getRenderer().drawCircle(x, H - DOOR_Y - 5f, 3.5f, COL_BUMP);
    }

    private void drawWaitingMarkers(float H) {
        float mx = DOOR_X - DOOR_WIDTH - 6f;
        float my = H - DOOR_Y - 26f;
        float mw = DOOR_WIDTH * 2f + 12f;
        ctx.getRenderer().drawRect(mx, my, mw, 5f, COL_MARKER);
        ctx.getRenderer().drawRect(mx, my, 5f, 22f, COL_MARKER);
        ctx.getRenderer().drawRect(DOOR_X + DOOR_WIDTH + 1f, my, 5f, 22f, COL_MARKER);
    }

    private void drawEndGameOverlay(float W, float H) {
        if (lost) {
            ctx.getRenderer().drawRect(0, 0, W, H, COL_ENDGAMEOVERLAY);
        }
    }

    // HUD
    @Override
    public void renderHud() {
        float timeRemaining = Math.max(0, timeLimit - gameTimer);

        drawHudBar(W);
        drawLives(W);
        drawHudText(W, timeRemaining);
        drawMidScreenMessage(W, H);

        font.setColor(EngineColor.WHITE);
    }

    private void drawHudBar(float W) {
        ctx.getRenderer().drawRect(0, 0, W, 42f, COL_HUD_BG);
    }

    private void drawLives(float W) {
        for (int i = 0; i < MAX_LIVES; i++) {
            ctx.getRenderer().drawCircle(20f + i * 22f, 19f, 9f, i < lives ? COL_HEART_ON : COL_HEART_OFF);
        }
    }

    private void drawHudText(float W, float timeRemaining) {
        font.setColor(EngineColor.WHITE);
        layout.setText(font.bitmapFont, "LVL " + level);
        ctx.getRenderer().drawText(font, "LVL " + level, W / 2f - layout.width / 2f, 28f);

        String timerStr = String.format("%.1fs", timeRemaining);
        layout.setText(font.bitmapFont, timerStr);
        font.setColor(COL_DOOR_SILL);
        ctx.getRenderer().drawText(font, timerStr, W - layout.width - 10f, 28f);
    }

    private void drawMidScreenMessage(float W, float H) {
        float midY = H / 2f + 36f;

        if (!gameStarted) {
            layout.setText(font.bitmapFont, "Avoid passengers, board the train!");
            font.setColor(EngineColor.BLACK);
            ctx.getRenderer().drawText(font, "Avoid passengers, board the train!",
                W / 2f - layout.width / 2f, H / 2f + layout.height / 2f);
        } else if (won) {
            layout.setText(bigFont.bitmapFont, "BOARDED!");
            bigFont.setColor(COL_PLAYER_HI);
            ctx.getRenderer().drawText(bigFont, "BOARDED!",
                W / 2f - layout.width / 2f, midY + layout.height / 2f);
        } else if (lost) {
            layout.setText(bigFont.bitmapFont, "YOU LOST!");
            bigFont.setColor(COL_LOSTTEXT);
            ctx.getRenderer().drawText(bigFont, "YOU LOST!",
                W / 2f - layout.width / 2f, midY + layout.height / 2f);
        }
        font.setColor(EngineColor.WHITE);
    }

    // Collision
    private void onCollision(CollisionEvent event) {
        Entity a = event.getPair().getA();
        Entity b = event.getPair().getB();
        if (!isPlayerAndNPC(a, b))
            return;

        Entity npc = (a == player) ? b : a;
        resolveCollision(player, npc);

        lives--;
        ctx.getIoManager().playSound("hit.wav");
        
        if (lives <= 0)
            triggerLoss();
    }

    private void resolveCollision(Entity p, Entity npc) {
        VelocityComponent pv = p.getComponent(VelocityComponent.class);
        VelocityComponent nv = npc.getComponent(VelocityComponent.class);
        TransformComponent pt = p.getComponent(TransformComponent.class);
        TransformComponent nt = npc.getComponent(TransformComponent.class);

        float dx = pt.x - nt.x;
        float dy = pt.y - nt.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0)
            dist = 0.001f;

        float push = 10f + ((level - 1) * 2.22f);
        pt.x += (dx / dist) * push;
        pt.y += (dy / dist) * push;
        nt.x -= (dx / dist) * push;
        nt.y -= (dy / dist) * push;

        if (nv != null) {
            nv.vx = -nv.vx * 1.5f;
            nv.vy = -nv.vy * 1.5f;
        }
        if (pv != null) {
            pv.vx += (dx / dist) * push * 10f;
            pv.vy += (dy / dist) * push * 10f;
        }
    }

    private boolean isPlayerAndNPC(Entity a, Entity b) {
        if (player == null)
            return false;
        if (a == player)
            return npcs.contains(b);
        if (b == player)
            return npcs.contains(a);
        return false;
    }

    // Physics
    private void keepInsideBounds(Entity e, boolean isPlayer) {
        TransformComponent t = e.getComponent(TransformComponent.class);
        ColliderComponent c = e.getComponent(ColliderComponent.class);
        VelocityComponent v = e.getComponent(VelocityComponent.class);
        if (t == null || c == null)
            return;

        float r = (c.type == ColliderComponent.ColShapeType.CIRCLE) ? c.radius : Math.max(c.halfWidth, c.halfHeight);
        float minX = r;
        float maxX = W - r;
        float minY = r;
        float maxY = H - r;

        if (!isPlayer) {
            maxY = H - DOOR_Y - r;
        } else {
            float ceiling = H - DOOR_Y - r;
            boolean inGap = Math.abs(t.x - DOOR_X) < DOOR_WIDTH - r;
            if (t.y > ceiling && !inGap) {
                t.y = ceiling;
                if (v != null)
                    v.vy = 0;
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
}