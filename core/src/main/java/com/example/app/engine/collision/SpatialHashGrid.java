package com.example.app.engine.collision;

import com.example.app.engine.entity.Entity;
import com.example.app.engine.movement.TransformComponent;
import com.example.app.engine.util.PairKey;

import java.util.*;

/**
 * Simple uniform-grid broadphase (non-contextual).
 */
public final class SpatialHashGrid {

    private final float cellSize;
    private final Map<Long, List<Entity>> buckets = new HashMap<>();

    public SpatialHashGrid(float cellSize) {
        this.cellSize = cellSize;
    }

    public void clear() { buckets.clear(); }

    public void insert(Entity e, TransformComponent t, ColliderComponent c) {
        float minX, minY, maxX, maxY;

        float px = t.x + c.offsetX;
        float py = t.y + c.offsetY;

        if (c.type == ColliderComponent.ShapeType.CIRCLE) {
            minX = px - c.radius; maxX = px + c.radius;
            minY = py - c.radius; maxY = py + c.radius;
        } else { // AABB
            minX = px - c.halfWidth;  maxX = px + c.halfWidth;
            minY = py - c.halfHeight; maxY = py + c.halfHeight;
        }

        int x0 = worldToCell(minX), x1 = worldToCell(maxX);
        int y0 = worldToCell(minY), y1 = worldToCell(maxY);

        for (int cx = x0; cx <= x1; cx++) {
            for (int cy = y0; cy <= y1; cy++) {
                long key = pack(cx, cy);
                List<Entity> list = buckets.get(key);
                if (list == null) {
                    list = new ArrayList<>();
                    buckets.put(key, list);
                }
                list.add(e);
            }
        }
    }

    public Set<PairKey> computeCandidatePairs() {
        Set<PairKey> pairs = new HashSet<>();
        for (List<Entity> list : buckets.values()) {
            for (int i = 0; i < list.size(); i++) {
                for (int j = i + 1; j < list.size(); j++) {
                    pairs.add(new PairKey(list.get(i).getId(), list.get(j).getId()));
                }
            }
        }
        return pairs;
    }

    private int worldToCell(float v) {
        return (int)Math.floor(v / cellSize);
    }

    private long pack(int x, int y) {
        return (((long)x) << 32) ^ (y & 0xffffffffL);
    }
}