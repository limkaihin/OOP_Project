package com.example.app.demo.scenes;

import com.example.app.engine.EngineContext;
import com.example.app.engine.collision.ColliderComponent;
import com.example.app.engine.entity.Entity;
import com.example.app.engine.io.InputAction;
import com.example.app.engine.io.InputState;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.movement.VelocityComponent;
import com.example.app.engine.render.EngineColor;

public class PlayerController {

    private final EngineContext ctx;
    private Entity player;

    private final float W, H;
    private final float doorX, doorWidth, doorY;

    private boolean won = false;

    private static final float PLAYER_SPEED        = 150f;
    private static final float DIAGONAL_NORMALISER = 0.707f;
    private static final float WIN_PROXIMITY        = 30f;

    private static final EngineColor COL_SHADOW    = new EngineColor(0.60f, 0.59f, 0.57f, 1f);
    private static final EngineColor COL_PLAYER    = new EngineColor(0.10f, 0.54f, 0.43f, 1f);
    private static final EngineColor COL_PLAYER_HI = new EngineColor(0.13f, 0.67f, 0.53f, 1f);

    public PlayerController(EngineContext ctx, float W, float H,
                            float doorX, float doorWidth, float doorY) {
        this.ctx = ctx;
        this.W = W;
        this.H = H;
        this.doorX = doorX;
        this.doorWidth = doorWidth;
        this.doorY = doorY;
    }

    public void spawn() {
        player = ctx.getPlayerFactory().create(320f, 80f, "player");
        won = false;
    }

    public void update() {
        if (player == null) return;

        InputState in = ctx.getIoManager().getInputHandler().getState();
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        pv.vx = 0;
        pv.vy = 0;

        if (in.isPressed(InputAction.MOVE_LEFT))  pv.vx -= PLAYER_SPEED;
        if (in.isPressed(InputAction.MOVE_RIGHT)) pv.vx += PLAYER_SPEED;
        if (in.isPressed(InputAction.MOVE_UP))    pv.vy += PLAYER_SPEED;
        if (in.isPressed(InputAction.MOVE_DOWN))  pv.vy -= PLAYER_SPEED;

        // Normalise diagonal so speed is consistent in all directions
        if (pv.vx != 0 && pv.vy != 0) {
            pv.vx *= DIAGONAL_NORMALISER;
            pv.vy *= DIAGONAL_NORMALISER;
        }

        keepInsideBounds();
        checkWinCondition();
    }

    private void checkWinCondition() {
        if (player == null) return;
        TransformComponent t = player.getComponent(TransformComponent.class);
        boolean nearDoor = Math.abs(t.x - doorX) < WIN_PROXIMITY
                && t.y > H - doorY - WIN_PROXIMITY;
        if (nearDoor) won = true;
    }

    private void keepInsideBounds() {
        if (player == null) return;
        TransformComponent t = player.getComponent(TransformComponent.class);
        ColliderComponent c = player.getComponent(ColliderComponent.class);
        VelocityComponent v = player.getComponent(VelocityComponent.class);
        if (t == null || c == null) return;

        float r = (c.type == ColliderComponent.ColShapeType.CIRCLE)
                ? c.radius : Math.max(c.halfWidth, c.halfHeight);

        // Let the player pass through the door gap to board
        float ceiling = H - doorY - r;
        boolean inGap = Math.abs(t.x - doorX) < doorWidth - r;
        if (t.y > ceiling && !inGap) {
            t.y = ceiling;
            if (v != null) v.vy = 0;
        }

        float oldX = t.x, oldY = t.y;
        t.x = Math.max(r, Math.min(W - r, t.x));
        t.y = Math.max(r, Math.min(H - r, t.y));

        if (v != null) {
            if (t.x != oldX) v.vx = -v.vx;
            if (t.y != oldY) v.vy = -v.vy;
        }
    }

    public void draw() {
        if (player == null) return;
        TransformComponent pt = player.getComponent(TransformComponent.class);
        ctx.getRenderer().drawCircle(pt.x + 2f, pt.y - 2f, 18f, COL_SHADOW);
        ctx.getRenderer().drawCircle(pt.x, pt.y, 16f, COL_PLAYER);
        ctx.getRenderer().drawCircle(pt.x, pt.y, 10f, COL_PLAYER_HI);
    }

    /** Called by TrainScene when a collision is resolved — pushes the player away from the NPC. */
    public void applyCollisionPush(float dx, float dy, float dist, int level) {
        if (player == null) return;
        TransformComponent pt = player.getComponent(TransformComponent.class);
        VelocityComponent pv = player.getComponent(VelocityComponent.class);
        float push = 10f + ((level - 1) * 2.22f);
        pt.x += (dx / dist) * push;
        pt.y += (dy / dist) * push;
        if (pv != null) {
            pv.vx += (dx / dist) * push * 10f;
            pv.vy += (dy / dist) * push * 10f;
        }
    }

    public void destroy() {
        if (player != null) {
            ctx.getEntityManager().destroy(player);
            player = null;
        }
    }

    public Entity getPlayer()    { return player; }
    public boolean hasPlayer()   { return player != null; }
    public boolean isWon()       { return won; }
}
