# OOP_project — Abstract Engine Demo (INF1009 Part 1)

This project is a **libGDX desktop application** demonstrating a **non-contextual Abstract Engine** built using Object-Oriented Programming principles.

The engine provides reusable, simulation-agnostic systems such as:
- Scene Management
- Entity Management
- Movement Management
- Collision Management
- Input / Output Management
- Render Queue (engine outputs render commands; application renders them)

The included demo simulation is intentionally simple (basic shapes and interactions).  
Its purpose is to showcase how the engine can be reused for other simulations without rewriting core systems.

---

## How to Run

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

> Note: The Gradle task will remain in the `EXECUTING` state until you close the game window. This is normal behaviour.

---

## Controls

| Action | Key |
|------|----|
| Move Player | `W A S D` or Arrow Keys |
| Spawn Obstacle | `Space` |
| Start / Confirm (Menu) | `Enter` |
| Pause / Resume | `P` |
| Quit Application | `Esc` |

Key bindings are defined in the **application / demo layer**, not inside the engine.

---

## Engine Features Overview

### Scene Management
- Application starts in a **Menu Scene**
- Transitions into a **Sandbox Scene**
- Demonstrates scene loading, unloading, and transitions

### Entity Management
- Entities are created and destroyed via a central manager
- Entity lifecycle is consistently managed by the engine

### Movement Management
- Movement is processed through the movement system
- `MovementManager` communicates with `EntityManager` to update entity positions

### Collision Management
- Collision detection and resolution handled by the collision system
- Demo reacts to collisions without embedding game-specific logic in the engine

### Input / Output Management
- User input is mapped to abstract input actions
- Output is logged via the output manager

### Render Queue
- The engine does not render directly
- It outputs `RenderCommand`s into a `RenderQueue`
- The application layer renders these commands

---

## Project Structure

### Application / Lifecycle Layer
```
core/src/main/java/com/example/app/
```
Key files:
- **GameMaster.java**
  - Extends `ApplicationAdapter`
  - Owns `create()`, `render()`, `dispose()`
  - Owns rendering resources (`SpriteBatch`, `ShapeRenderer`, `BitmapFont`)
  - Creates and updates the engine
- **Main.java**
  - Entry point for launcher compatibility
  - Extends `GameMaster`

---

### Abstract Engine (Non-Contextual)
```
core/src/main/java/com/example/app/engine/
```
Contains:
- `EngineImpl` and `EngineContext`
- Core managers (scene, entity, movement, collision, input, output)
- Entities and components
- Render output abstractions

No simulation-specific logic exists in this layer.

---

### Demo / Simulation Layer
```
core/src/main/java/com/example/app/demo/scenes/
```
Contains:
- `MenuScene` — configuration and entry point
- `TransitionScene` — scene transitions
- `SandboxScene` — demonstrates entity spawning, movement, and collisions

---

## UML Diagram

The UML diagram provides an **architecture-level overview** of the engine design, showing how the main components and managers are structured and connected.

UML files are located in:
```
docs/uml/
```

Open and export the diagram using **diagrams.net (draw.io)**.

---

## Design Intent

- The engine is reusable and extensible
- The demo exists only to showcase engine capabilities
- New simulations can be built by replacing the demo layer without modifying engine code
- The application layer adapts libGDX lifecycle and rendering to engine output

---

## Notes

- Rendering and visuals are intentionally minimal
- Physics and collision behaviour are simplified for clarity
- Emphasis is placed on clean architecture, separation of concerns, and extensibility
