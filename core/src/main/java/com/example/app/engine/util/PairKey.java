package com.example.app.engine.util;

/**
 * Hashable pair key for deduping candidate collision pairs.
 */
public final class PairKey {
    public final int a;
    public final int b;

    public PairKey(int id1, int id2) {
        if (id1 < id2) { a = id1; b = id2; }
        else { a = id2; b = id1; }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PairKey)) return false;
        PairKey p = (PairKey)o;
        return a == p.a && b == p.b;
    }

    @Override
    public int hashCode() {
        return 31 * a + b;
    }
}