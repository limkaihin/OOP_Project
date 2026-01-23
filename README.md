# OOP_project — Abstract Engine Demo (INF1009 Part 1)

This project is a libGDX desktop application that demonstrates a **non-contextual abstract engine** with:
Scene management, Entity management, Movement, Collision, and Input/Output management.

The demo is intentionally simple (shapes + basic interactions) so the engine can be reused for other simulations without rewriting core systems.

## How to run

### Desktop (LWJGL3)
From the project root:

- Windows:
  - `.\gradlew.bat lwjgl3:run`
- macOS/Linux:
  - `./gradlew lwjgl3:run`

This is the standard libGDX way to run the desktop module via Gradle. [web:98]

## Required assets

Place this file in the assets folder (same folder as `libgdx.png`):

- `assets/hit.wav` — sound played when the NPC collides with the player.

If `hit.wav` is missing, the demo will still run, but collision sound is disabled.

## Controls

- Move player: `WASD` or Arrow keys
- Spawn an obstacle: `Space`
- Start / Confirm (menu): `Enter`
- Pause/resume: `P`
- Quit: `Esc`

## What to look for (engine features)

- **Scene management**: Starts in a Menu scene, transitions into a Sandbox scene.
- **Entity management**: Entities are created centrally and updated each frame.
- **Movement management**: Player movement is controlled by input; NPC/obstacles can also move.
- **Collision management**: Collision events occur between entities and trigger interactions (sound/log/bounce).
- **Input/Output management**: Key presses are mapped to actions; output is logged to the console.

## Project structure (important files)

### Engine (non-contextual)
- `core/src/main/java/com/example/app/engine/`
  - `EngineImpl`, `EngineContext`, managers, components, utilities.

### Demo / Simulation layer (contextual)
- `core/src/main/java/com/example/app/demo/scenes/`
  - `MenuScene.java` — choose spawn count / start demo.
  - `TransitionScene.java` — simple transition between scenes.
  - `SandboxScene.java` — spawns entities, handles interactions, and submits render commands.

### libGDX adapter
- `core/src/main/java/com/example/app/Main.java`
  - Creates the engine and draws the engine’s render queue + UI instructions.

## Notes

- The Gradle task `lwjgl3:run` will show “EXECUTING” until you close the game window (that is normal).
- The abstract engine is designed to keep simulation-specific logic out of the engine package so it can be reused.
