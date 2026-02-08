package com.example.app.engine.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Engine-level render queue (non-libGDX).
 * Demo/Main consumes it to draw using libGDX.
 */
public final class RenderQueue {
    private final List<RenderCommand> commands = new ArrayList<>();

    public void clear() { commands.clear(); }
    public void submit(RenderCommand cmd) { if (cmd != null) commands.add(cmd); }
    public List<RenderCommand> view() { return Collections.unmodifiableList(commands); }
}