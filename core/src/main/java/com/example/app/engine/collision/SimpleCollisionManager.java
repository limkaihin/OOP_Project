package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.util.PairKey;

import java.util.*;

public final class SimpleCollisionManager implements CollisionManager {

    private final List<CollisionListener> listeners = new ArrayList<>();
    private CollisionResolver resolver; // optional

    // Broadphase grid (cellSize tuned for demo)
    private final SpatialHashGrid grid = new SpatialHashGrid(64f);

    public void addListener(CollisionListener listener) {
        if (listener != null) listeners.add(listener);
    }

    public void setResolver(CollisionResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public List<CollisionEvent> update(float dt, Iterable<Entity> entities) {
        List<Entity> collidables = new ArrayList<>();
        Map<Integer, Entity> byId = new HashMap<>();

        grid.clear();

        for (Entity e : entities) {
            if (e == null) continue;
            ColliderComponent c = e.getComponent(ColliderComponent.class);
            TransformComponent t = e.getComponent(TransformComponent.class);
            if (c == null || t == null) continue;

            collidables.add(e);
            byId.put(e.getId(), e);
            grid.insert(e, t, c);
        }

        Set<PairKey> candidates = grid.computeCandidatePairs();
        List<CollisionEvent> events = new ArrayList<>();

        for (PairKey pk : candidates) {
            Entity a = byId.get(pk.a);
            Entity b = byId.get(pk.b);
            if (a == null || b == null) continue;

            ColliderComponent ca = a.getComponent(ColliderComponent.class);
            ColliderComponent cb = b.getComponent(ColliderComponent.class);
            if (ca == null || cb == null) continue;

            if (!passesFilter(ca, cb)) continue;

            TransformComponent ta = a.getComponent(TransformComponent.class);
            TransformComponent tb = b.getComponent(TransformComponent.class);
            if (ta == null || tb == null) continue;

            CollisionManifold manifold = intersect(ta, ca, tb, cb);
            if (manifold == null) continue;

            CollisionEvent evt = new CollisionEvent(new CollisionPair(a, b), manifold);
            events.add(evt);

            for (CollisionListener l : listeners) l.onCollision(evt);

            if (resolver != null && !ca.isTrigger && !cb.isTrigger) {
                resolver.resolve(a, b, manifold);
            }
        }

        return events;
    }

    private boolean passesFilter(ColliderComponent a, ColliderComponent b) {
        boolean ab = (a.mask & b.layer) != 0;
        boolean ba = (b.mask & a.layer) != 0;
        return ab && ba;
    }

    private CollisionManifold intersect(TransformComponent ta, ColliderComponent ca, TransformComponent tb, ColliderComponent cb) {
        float ax = ta.x + ca.offsetX;
        float ay = ta.y + ca.offsetY;
        float bx = tb.x + cb.offsetX;
        float by = tb.y + cb.offsetY;

        if (ca.type == ColliderComponent.ShapeType.CIRCLE && cb.type == ColliderComponent.ShapeType.CIRCLE) {
            return circleCircle(ax, ay, ca.radius, bx, by, cb.radius);
        }

        if (ca.type == ColliderComponent.ShapeType.AABB && cb.type == ColliderComponent.ShapeType.AABB) {
            return aabbAabb(ax, ay, ca.halfWidth, ca.halfHeight, bx, by, cb.halfWidth, cb.halfHeight);
        }

        if (ca.type == ColliderComponent.ShapeType.CIRCLE && cb.type == ColliderComponent.ShapeType.AABB) {
            return circleAabb(ax, ay, ca.radius, bx, by, cb.halfWidth, cb.halfHeight);
        }

        if (ca.type == ColliderComponent.ShapeType.AABB && cb.type == ColliderComponent.ShapeType.CIRCLE) {
            CollisionManifold m = circleAabb(bx, by, cb.radius, ax, ay, ca.halfWidth, ca.halfHeight);
            if (m == null) return null;
            return new CollisionManifold(-m.normalX, -m.normalY, m.penetration);
        }

        return null;
    }

    private CollisionManifold circleCircle(float ax, float ay, float ar, float bx, float by, float br) {
        float dx = bx - ax, dy = by - ay;
        float r = ar + br;
        float dist2 = dx*dx + dy*dy;
        if (dist2 > r*r) return null;

        float dist = (float)Math.sqrt(Math.max(dist2, 1e-8f));
        float nx = dx / dist, ny = dy / dist;
        return new CollisionManifold(nx, ny, r - dist);
    }

    private CollisionManifold aabbAabb(float ax, float ay, float ahw, float ahh, float bx, float by, float bhw, float bhh) {
        float dx = bx - ax;
        float px = (ahw + bhw) - Math.abs(dx);
        if (px <= 0) return null;

        float dy = by - ay;
        float py = (ahh + bhh) - Math.abs(dy);
        if (py <= 0) return null;

        if (px < py) {
            float nx = (dx < 0) ? -1f : 1f;
            return new CollisionManifold(nx, 0f, px);
        } else {
            float ny = (dy < 0) ? -1f : 1f;
            return new CollisionManifold(0f, ny, py);
        }
    }

    private CollisionManifold circleAabb(float cx, float cy, float cr, float bx, float by, float bhw, float bhh) {
        float closestX = clamp(cx, bx - bhw, bx + bhw);
        float closestY = clamp(cy, by - bhh, by + bhh);

        float dx = closestX - cx;
        float dy = closestY - cy;
        float dist2 = dx*dx + dy*dy;
        if (dist2 > cr*cr) return null;

        float dist = (float)Math.sqrt(Math.max(dist2, 1e-8f));
        float nx = (dist > 0f) ? (dx / dist) : 0f;
        float ny = (dist > 0f) ? (dy / dist) : 1f;

        return new CollisionManifold(nx, ny, cr - dist);
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}