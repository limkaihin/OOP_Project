package com.example.app.demo.scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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
import com.example.app.engine.render.RenderableComponent;
import com.example.app.engine.render.SpriteComponent;
import com.example.app.engine.scene.AbstractBaseScene;
import com.example.app.engine.scene.Scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class TrainScene extends AbstractBaseScene {
    private final EngineContext ctx;
    private final int level;
    private final int initialNpcCount;
    private final float timeLimit;
    private Entity player;
    private final Random rng = new Random();
    private SpriteBatch batch;
    private BitmapFont font;
    private float timer = 0f;
    private boolean won = false;
    private boolean lost = false;
    private boolean gameStarted = false;
    private float postGameTimer = 0f;

    // Textures map
    private Map<String, Texture> textures = new HashMap<>();

    public TrainScene(EngineContext ctx, int level) {
        this.ctx = ctx;
        this.level = level;
        this.initialNpcCount = Math.round(30f + ((level - 1) * (20f / 9f)));
        this.timeLimit = 20f - ((level - 1) * (10f / 9f));
    }

    @Override
    public void onLoad() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        // Load textures
        textures.put("mrt_bg", new Texture(Gdx.files.internal("mrt_train_side.png")));
        textures.put("platform_floor", new Texture(Gdx.files.internal("waiting_zone_floor_marker.png")));
        textures.put("door", new Texture(Gdx.files.internal("train_door.png")));
        textures.put("player", new Texture(Gdx.files.internal("player_down.png")));

        ctx.ioManager.playSound("alert.mp3"); // Play sound (no dedicated voice clip provided, using what is there)
        ctx.ioManager.log("TrainScene", "Loaded (NPCs=" + initialNpcCount + ")");
        setupWorld();
    }

    private void setupWorld() {
        // Player (Outside the train, bottom center)
        player = ctx.entityManager.create();
        player.addComponent(TransformComponent.class, new TransformComponent(120, 80));
        player.addComponent(VelocityComponent.class, new VelocityComponent(0, 0));
        player.addComponent(ColliderComponent.class, ColliderComponent.aabb(16, 16));
        player.addComponent(SpriteComponent.class, new SpriteComponent("player", 32, 32));

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
        Entity npc = ctx.entityManager.create();
        npc.addComponent(TransformComponent.class, new TransformComponent(x, y));

        // Give them velocity towards the door (x=320, y=trainYBase)
        float targetX = 320f + (rng.nextFloat() - 0.5f) * 40f;
        float targetY = 160f;

        float dx = targetX - x;
        float dy = targetY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        float speed = 40f + rng.nextFloat() * 40f;
        npc.addComponent(VelocityComponent.class, new VelocityComponent((dx / dist) * speed, (dy / dist) * speed));

        npc.addComponent(ColliderComponent.class, ColliderComponent.circle(12));

        // Load a random passenger sprite
        String spriteName = "passenger_npc_" + String.format("%02d", rng.nextInt(24) + 1) + ".png";
        if (!textures.containsKey(spriteName)) {
            try {
                textures.put(spriteName, new Texture(Gdx.files.internal(spriteName)));
            } catch (Exception e) {
                // Ignore if missing, use first
            }
        }
        npc.addComponent(SpriteComponent.class, new SpriteComponent(spriteName, 24, 24));
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
                    ctx.sceneManager.switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 0.6f));
                } else {
                    ctx.sceneManager.switchTo(new TransitionScene(ctx, new LevelSelectScene(ctx), 0.6f));
                }
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
        if (!won && !lost) {
            if (in.isPressed(InputAction.MOVE_LEFT))
                pv.vx -= speed;
            if (in.isPressed(InputAction.MOVE_RIGHT))
                pv.vx += speed;
            if (in.isPressed(InputAction.MOVE_UP))
                pv.vy += speed;
            if (in.isPressed(InputAction.MOVE_DOWN))
                pv.vy -= speed;
        }

        // Check win condition: player is inside train
        TransformComponent pTransform = player.getComponent(TransformComponent.class);
        if (pTransform.y > 280f && Math.abs(pTransform.x - 320) < 60) {
            won = true;
            ctx.ioManager.getOutputHandler().stopSound();
            ctx.ioManager.playSound("level_clear.mp3"); // play clear sound
        }

        // Logic for NPCs
        for (Entity e : ctx.entityManager.getAll()) {
            if (e != player && e.getComponent(SpriteComponent.class) != null) {
                TransformComponent t = e.getComponent(TransformComponent.class);
                VelocityComponent v = e.getComponent(VelocityComponent.class);
                if (t != null && v != null) {
                    // If outside train, walk downwards offscreen
                    if (t.y < 200f) {
                        v.vx = 0;
                        v.vy = -60f;
                    }
                    if (t.y < -50f) {
                        ctx.entityManager.destroy(e);
                    }
                }

                if (e != player)
                    keepInsideBounds(e, false);
            }
        }

        keepInsideBounds(player, true);
    }

    @Override
    public void render() {
        ctx.renderer.end(); // temporarily end ShapeRenderer before starting SpriteBatch

        // Draw background
        batch.begin();
        Texture bg = textures.get("mrt_bg");
        if (bg != null)
            batch.draw(bg, 0, 240, 640, 240);

        Texture floor = textures.get("platform_floor");
        if (floor != null)
            batch.draw(floor, 0, 0, 640, 240);

        // Draw entities
        for (Entity e : ctx.entityManager.getAll()) {
            TransformComponent t = e.getComponent(TransformComponent.class);
            SpriteComponent s = e.getComponent(SpriteComponent.class);
            if (t != null && s != null) {
                Texture tex = textures.get(s.texturePath);
                if (tex != null) {
                    batch.draw(tex, t.x - s.width / 2, t.y - s.height / 2, s.width, s.height);
                }
            }
        }

        // HUD
        font.draw(batch, "Level: " + level, 10, 470);
        float timeRemaining = Math.max(0, timeLimit - timer);
        font.draw(batch, "Time Left: " + String.format("%.1f", timeRemaining) + "s", 10, 440);

        if (!gameStarted) {
            font.draw(batch, "Please let the alighting passenger\nalight before boarding, thank you.", 100, 300);
        } else if (won) {
            font.setColor(Color.GREEN);
            font.draw(batch, "SUCCESS! Level " + level + " Cleared!", 200, 240);
            if (level == 10) {
                font.draw(batch, "YOU BEAT THE GAME!", 220, 200);
            }
            font.setColor(Color.WHITE);
        } else if (lost) {
            font.setColor(Color.RED);
            font.draw(batch, "TIME'S UP! The door closed.\nTry again!", 200, 240);
            font.setColor(Color.WHITE);
        }

        batch.end();
        ctx.renderer.begin(); // restart ShapeRenderer for GameMaster loop
    }

    private void onCollision(CollisionEvent event) {
        Entity a = event.getPair().getA();
        Entity b = event.getPair().getB();

        // Bounce Player off NPCs
        if (isPlayerAndNPC(a, b)) {
            VelocityComponent pv = player.getComponent(VelocityComponent.class);
            Entity npc = a == player ? b : a;
            VelocityComponent nv = npc.getComponent(VelocityComponent.class);

            TransformComponent pt = player.getComponent(TransformComponent.class);
            TransformComponent nt = npc.getComponent(TransformComponent.class);

            float dx = pt.x - nt.x;
            float dy = pt.y - nt.y;
            float dist = (float) Math.sqrt(dx * dx + dy * dy);
            if (dist == 0)
                dist = 0.001f;

            // Harder knockback per level. Base 10f at level 1, up to 30f at level 10
            float pushForce = 10f + ((level - 1) * 2.22f);

            // Push player back
            pt.x += (dx / dist) * pushForce;
            pt.y += (dy / dist) * pushForce;

            // Push npc back
            nt.x -= (dx / dist) * pushForce;
            nt.y -= (dy / dist) * pushForce;
            ctx.ioManager.playSound("hit.wav"); // Play sound (no dedicated voice clip provided, using what is there)

            if (nv != null) {
                nv.vx = -nv.vx * 1.5f;
                nv.vy = -nv.vy * 1.5f;
            }

            // Give player slight opposing velocity bounce
            if (pv != null) {
                pv.vx += (dx / dist) * pushForce * 10f;
                pv.vy += (dy / dist) * pushForce * 10f;
            }
        }
    }

    private boolean isPlayerAndNPC(Entity a, Entity b) {
        boolean hasPlayer = a == player || b == player;
        boolean hasNPC = (a != player && a.getComponent(SpriteComponent.class) != null) ||
                (b != player && b.getComponent(SpriteComponent.class) != null);
        return hasPlayer && hasNPC;
    }

    private void keepInsideBounds(Entity e, boolean bPlayer) {
        TransformComponent t = e.getComponent(TransformComponent.class);
        ColliderComponent c = e.getComponent(ColliderComponent.class);
        VelocityComponent v = e.getComponent(VelocityComponent.class);

        if (t == null || c == null)
            return;

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

        // Restrict player from walking through walls
        if (bPlayer && t.y > 220f) {
            if (t.x < 320 - 40 || t.x > 320 + 40) {
                t.y = 220f;
            }
        }

        float oldX = t.x;
        float oldY = t.y;

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
        if (batch != null)
            batch.dispose();
        if (font != null)
            font.dispose();
        for (Texture t : textures.values()) {
            t.dispose();
        }
    }
}
