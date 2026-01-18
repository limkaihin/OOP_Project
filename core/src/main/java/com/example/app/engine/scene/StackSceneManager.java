package com.example.app.engine.scene;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Stack-based scene manager:
 * - push(): overlays a new scene (previous remains below)
 * - pop(): removes current scene and returns to previous
 * - switchTo(): replaces current scene
 *
 * This is generic and works for menus, pause overlays, gameplay, etc.
 */
public final class StackSceneManager implements SceneManager {

    private final Deque<Scene> stack = new ArrayDeque<>();

    @Override
    public void push(Scene scene) {
        if (scene == null) throw new IllegalArgumentException("scene cannot be null");

        Scene cur = current();
        if (cur != null) cur.onExit();

        scene.onLoad();
        stack.push(scene);
        scene.onEnter();
    }

    @Override
    public void pop() {
        Scene cur = current();
        if (cur == null) return;

        cur.onExit();
        cur.onUnload();
        stack.pop();

        Scene next = current();
        if (next != null) next.onEnter();
    }

    @Override
    public void switchTo(Scene scene) {
        if (scene == null) throw new IllegalArgumentException("scene cannot be null");

        Scene cur = current();
        if (cur != null) {
            cur.onExit();
            cur.onUnload();
            stack.pop();
        }

        scene.onLoad();
        stack.push(scene);
        scene.onEnter();
    }

    @Override
    public Scene current() {
        return stack.peek();
    }

    @Override
    public int size() {
        return stack.size();
    }
}
