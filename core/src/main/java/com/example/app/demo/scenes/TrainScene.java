package com.example.app.demo.scenes;

import com.example.app.demo.game.NPCController;
import com.example.app.demo.game.PlayerController;
import com.example.app.demo.game.TrainLayout;
import com.example.app.demo.game.TrainHud;
import com.example.app.demo.game.TrainRenderer;
import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.CollisionEvent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.util.EventBus;

public final class TrainScene extends AbstractBaseScene {

    private final EngineContext ctx;

    // Delegates — each owns one responsibility
    private final PlayerController playerController;
    private final NPCController npcController;
    private final TrainRenderer trainRenderer;
    private final TrainHud trainHud;

    private EventBus<CollisionEvent>.Subscription collisionSub;

    // Level configuration
    private final int level;
    private final int initialNpcCount;
    private final float timeLimit;
    private int npcsRemaining;

    // Frame state
    private float spawnTimer = 0f;
    private float introTimer = 0f;
    private float gameTimer = 0f;
    private float postGameTimer = 0f;
    private float doorOpenAmount = 0f;
    private boolean gameStarted = false;
    private boolean won = false;
    private boolean lost = false;
    private int lives = MAX_LIVES;

    // Constants
    private static final int MAX_LIVES = 3;
    private static final float SPAWN_INTERVAL = 0.4f;
    private static final float DOOR_OPEN_SPEED = 1.5f;

    public TrainScene(EngineContext ctx, int level) {
        this.ctx = ctx;
        this.level = level;
        this.initialNpcCount = Math.round(10f + ((level - 1) * 2f));
        this.timeLimit = 15f - ((level + 1) * (10f / 9f));

        float W = ctx.getConfig().width;
        float H = ctx.getConfig().height;

        this.playerController = new PlayerController(ctx, W, H, TrainLayout.DOOR_X, TrainLayout.DOOR_WIDTH,
                TrainLayout.DOOR_Y);
        this.npcController = new NPCController(ctx, W, H, TrainLayout.DOOR_Y, level);
        this.trainRenderer = new TrainRenderer(ctx.getRenderer());
        this.trainHud = new TrainHud();
    }

    // ---- Lifecycle ----

    @Override
    public void onLoad() {
        trainHud.load();
        npcsRemaining = initialNpcCount;
        playerController.spawn();
        collisionSub = ctx.getCollisionEvents().subscribe(this::onCollision);
        ctx.getIoManager().playSound("alert.mp3");
        ctx.getIoManager().log("TrainScene", "Loaded (NPCs=" + initialNpcCount + ")");
    }

    @Override
    public void onUnload() {
        if (collisionSub != null)
            collisionSub.cancel();
        trainHud.dispose();
        playerController.destroy();
        npcController.destroyAll();
    }

    // ---- Update ----

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
        if (introTimer > 2f)
            gameStarted = true;
    }

    private void updatePostGame(float dt) {
        doorOpenAmount = Math.max(0f, doorOpenAmount - dt * DOOR_OPEN_SPEED);
        postGameTimer += dt;
        if (postGameTimer > 2.5f) {
            if (won) {
                ctx.getProgress().unlockNextLevel(level);
                if (level >= 5) {
                    ctx.getSceneManager().switchTo(new GameClearScene(ctx));
                    return;
                }
            }
            ctx.getSceneManager().switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 1.5f));
        }
    }

    private void updateGame(float dt) {
        if (timeLimit - gameTimer < 5f)
            doorOpenAmount = Math.max(0.2f, doorOpenAmount - dt * 0.3f);

        gameTimer += dt;
        if (gameTimer > timeLimit) {
            triggerLoss();
            return;
        }

        updateSpawner(dt);
        playerController.update();
        if (playerController.isWon())
            triggerWin();
        npcController.updateAll(playerController.getPlayer());
    }

    private void updateSpawner(float dt) {
        if (npcsRemaining <= 0)
            return;
        spawnTimer += dt;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0f;
            float H = ctx.getConfig().height;
            npcController.spawnOne(TrainLayout.DOOR_X, H - TrainLayout.DOOR_Y + 50f);
            npcsRemaining--;
        }
    }

    private void triggerWin() {
        if (won)
            return;
        won = true;
        playerController.destroy();
        npcController.destroyAll();
        ctx.getIoManager().getOutputHandler().stopSound();
        ctx.getIoManager().playSound("level_clear.mp3");
        playerController.destroy();
    }

    private void triggerLoss() {
        if (lost)
            return;
        lost = true;
        playerController.destroy();
        npcController.destroyAll();
        ctx.getIoManager().getOutputHandler().stopSound();
        ctx.getIoManager().playSound("level_fail.mp3");
    }

    // ---- Render ----

    @Override
    public void render() {
        float W = ctx.getConfig().width;
        float H = ctx.getConfig().height;
        trainRenderer.render(W, H, doorOpenAmount, lost);
        npcController.drawAll();
        playerController.draw();
    }

    @Override
    public void renderHud() {
        float timeRemaining = Math.max(0, timeLimit - gameTimer);
        float W = ctx.getConfig().width;
        float H = ctx.getConfig().height;
        trainHud.render(ctx.getRenderer(), level, timeRemaining, lives, won, lost, gameStarted, W, H);
    }

    // ---- Collision ----

    private void onCollision(CollisionEvent event) {
        Entity a = event.getPair().getA();
        Entity b = event.getPair().getB();
        Entity player = playerController.getPlayer();
        if (player == null)
            return;

        Entity npc;
        if (a == player && npcController.getAll().contains(b))
            npc = b;
        else if (b == player && npcController.getAll().contains(a))
            npc = a;
        else
            return;

        resolveCollision(player, npc);
        lives--;
        ctx.getIoManager().playSound("hit.wav");
        if (lives <= 0)
            triggerLoss();
    }

    private void resolveCollision(Entity player, Entity npc) {
        TransformComponent pt = player.getComponent(TransformComponent.class);
        TransformComponent nt = npc.getComponent(TransformComponent.class);
        float dx = pt.x - nt.x;
        float dy = pt.y - nt.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist == 0)
            dist = 0.001f;

        playerController.applyCollisionPush(dx, dy, dist, level);
        npcController.applyCollisionPush(npc, dx, dy, dist, level);
    }
}