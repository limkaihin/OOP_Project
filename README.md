# OOP_project — Abstract Engine Demo (INF1009 Part 1)

This project is a **libGDX desktop application** that demonstrates a **non-contextual Abstract Engine** built using Object-Oriented Programming principles.

The engine provides reusable core systems such as:
- Scene Management  
- Entity Management  
- Movement Management  
- Collision Management  
- Input / Output Management  

The included demo simulation is **intentionally simple** (basic shapes and interactions).  
Its sole purpose is to showcase how the engine can be reused for other simulations **without modifying the engine code**.

---

## 1. How to Run

### Desktop (LWJGL3)

From the project root directory:

**Windows**
```bash
.\gradlew.bat lwjgl3:run
```

**macOS / Linux**
```bash
./gradlew lwjgl3:run
```

This uses the standard libGDX Gradle task for running the desktop module.

> Note: The Gradle task will remain in the `EXECUTING` state until the game window is closed. This is normal behavior.

---

## 2. Required Assets

Place the following file in the `assets/` folder (same directory as `libgdx.png`):

- `hit.wav` — sound played when the NPC collides with the player.

If `hit.wav` is missing:
- The demo will still run normally
- Collision sound effects will simply be disabled

---

## 3. Controls

| Action | Key |
|------|----|
| Move Player | `W A S D` or Arrow Keys |
| Spawn Obstacle | `Space` |
| Start / Confirm (Menu) | `Enter` |
| Pause / Resume | `P` |
| Quit Application | `Esc` |

Key bindings are defined in the **demo layer**, not inside the engine.

---

## 4. What to Look For (Engine Features)

### Scene Management
- Application starts in a **Menu Scene**
- Transitions into a **Sandbox Scene**
- Demonstrates scene loading, unloading, and transitions

### Entity Management
- Entities are created and destroyed through a central manager
- Entity lifecycle is handled consistently by the engine

### Movement Management
- Player movement is driven by input
- Other entities (NPCs / obstacles) may move independently

### Collision Management
- Collision detection and resolution handled by the engine
- Collision results are published as events
- Demo reacts to collisions without tightly coupling to engine internals

### Input / Output Management
- Key presses are mapped to abstract input actions
- Output is logged to the console or visualized in the demo

---

## 5. Project Structure (Important Files)

### Abstract Engine (Non-Contextual)
```
core/src/main/java/com/example/app/engine/
```
Includes:
- `EngineImpl`
- `EngineContext`
- Scene, Entity, Collision, Movement, and IO managers
- Shared utilities and abstractions

This package contains **no simulation-specific logic**.

---

### Demo / Simulation Layer (Contextual)
```
core/src/main/java/com/example/app/demo/scenes/
```

Key files:
- `MenuScene.java` — Menu and configuration entry point
- `TransitionScene.java` — Simple scene transition logic
- `SandboxScene.java` — Demonstrates entity creation, movement, and collisions

All demo-specific behavior lives here.

---

### libGDX Adapter / Entry Point
```
core/src/main/java/com/example/app/Main.java
```

Responsibilities:
- Initializes the engine
- Binds input actions
- Renders engine output and on-screen instructions

---

## 6. Design Intent

- The **engine** is reusable and extensible
- The **demo** is replaceable and disposable
- You can delete the entire `demo` package and reuse the engine unchanged
- New simulations can be built by:
  - Creating new scenes
  - Defining new entities
  - Subscribing to engine events
  - Binding inputs externally

---

## 7. Notes

- Rendering and visuals are intentionally minimal
- Focus is on engine design, abstraction, and extensibility
- Physics and collision logic are simplified for clarity
- The project prioritizes clean separation of concerns over visual complexity

---

## 8. Summary

This project demonstrates:
- Strong application of Object-Oriented Programming concepts
- Clean separation between abstract engine and simulation logic
- Reusable, scalable, and extensible engine architecture
- A working demo that validates engine functionality without polluting core systems
