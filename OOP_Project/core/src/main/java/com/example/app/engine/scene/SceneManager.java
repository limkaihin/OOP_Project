package com.example.app.engine.scene;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {

    private final List<Scene> scenes = new ArrayList<>();

    public List<Scene> getScenes() {
        return scenes;
    }

    public Scene current() {
        if (scenes.isEmpty()) return null;
        return scenes.get(scenes.size() - 1);
    }

    public void switchTo(Scene scene) {
        pop();
        push(scene);
    }

    public void push(Scene scene) {
        if (scene == null) return;
        scenes.add(scene);
        scene.onLoad();
        scene.onEnter();
    }

    public void pop() {
        Scene cur = current();
        if (cur == null) return;
        cur.onExit();
        cur.onUnload();
        scenes.remove(scenes.size() - 1);
    }

    public int size() {
        return scenes.size();
    }
}
