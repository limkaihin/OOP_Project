# OOP_Project --- Abstract Engine Demo (INF1009 Part 1)

This project is a libGDX desktop application demonstrating a
non-contextual Abstract Engine built using Object-Oriented Programming
principles and a modular system architecture.

The engine provides reusable, simulation-agnostic systems such as: -
Scene Management - Entity Management - Movement Management - Collision
Management (with spatial partitioning support) - Input / Output
Management - Event Bus System - Render Queue (engine outputs render
commands; application renders them)

The included demo simulation is intentionally simple (basic shapes and
interactions).\
Its purpose is to showcase how the engine can be reused for other
simulations without rewriting core systems.

------------------------------------------------------------------------

## How to Run

### Desktop (LWJGL3)

From the project root directory:

**Windows**

    .\gradlew.bat lwjgl3:run

**macOS / Linux**

    ./gradlew lwjgl3:run

Note: The Gradle task will remain in the EXECUTING state until you close
the game window. This is normal behaviour.

------------------------------------------------------------------------

## Controls

  Action                     Key
  -------------------------- --------------
  Move Player                W A S D
  Increase / Reduce Volume   Up / Down
  Play or Stop Music         Right / Left
  Spawn Obstacle             Space
  Start / Step 1 frame       Enter
  Pause / Resume             P
  Quit Application           Esc

Key bindings are defined in the application / demo layer, not inside the
engine.

------------------------------------------------------------------------

## Engine Features Overview

### Scene Management

-   Application starts in a MenuScene
-   Transitions into a SandboxScene
-   Uses a TransitionScene to demonstrate controlled scene switching
-   Scene lifecycle (load, update, dispose) is managed centrally by the
    engine

### Entity Management

-   Entities are lightweight identifiers managed by a central
    EntityManager
-   Components store data independently of logic
-   Entity lifecycle (creation, update, destruction) is consistently
    controlled by the engine

### Movement Management

-   Movement is processed through MovementManager
-   Position updates are handled through components (e.g., transform +
    velocity)
-   Logic is separated from rendering and collision systems

### Collision Management

-   Collision detection is handled by a dedicated collision system
-   Broad-phase optimization supported via spatial partitioning
    (SpatialHashGrid)
-   Collision responses are triggered without embedding
    simulation-specific behaviour in the engine

### Input / Output Management

-   User input is abstracted through an input handling system
-   Demo layer maps keys to engine-recognised actions
-   Output and logging are handled via the output manager

### Event Bus

-   Systems communicate through an event-driven mechanism
-   Reduces tight coupling between managers
-   Enables extensibility without modifying core systems

### Render Queue

-   The engine does not render directly
-   It outputs RenderCommand objects into a RenderQueue
-   The application layer reads and renders these commands using libGDX
-   Ensures separation between engine logic and graphical implementation

------------------------------------------------------------------------

## Project Structure

### Application / Lifecycle Layer

    core/src/main/java/com/example/app/

Key files: - GameMaster.java - Extends ApplicationAdapter - Owns
create(), render(), dispose() - Owns rendering resources (SpriteBatch,
ShapeRenderer, BitmapFont) - Creates and updates the engine -
Main.java - Entry point for launcher compatibility - Extends GameMaster

------------------------------------------------------------------------

### Abstract Engine (Non-Contextual)

    core/src/main/java/com/example/app/engine/

Contains: - EngineImpl and EngineContext - Core managers (scene, entity,
movement, collision, input, output) - ECS components - Event system -
Spatial hash grid implementation - Render output abstractions
(RenderCommand, RenderQueue)

No simulation-specific logic exists in this layer.

------------------------------------------------------------------------

### Demo / Simulation Layer

    core/src/main/java/com/example/app/demo/scenes/

Contains: - MenuScene --- configuration and entry point -
TransitionScene --- controlled scene switching - SandboxScene ---
demonstrates entity spawning, movement, and collision interaction

The demo can be replaced without modifying the engine layer.

------------------------------------------------------------------------

## UML Diagram

The UML diagram provides an architecture-level overview of the engine
design, showing:

-   Entity--Component--System structure\
-   Manager responsibilities\
-   Scene hierarchy\
-   Interface usage\
-   Low coupling and high cohesion design

UML files are located in:

    docs/uml/

install plantuml plugin in vscode to view the diagram. 

------------------------------------------------------------------------

## Design Intent

-   The engine is reusable and extensible\
-   Core systems are modular and decoupled\
-   New simulations can be built by replacing only the demo layer\
-   The application layer adapts libGDX lifecycle and rendering to
    engine output\
-   Architecture prioritizes scalability, maintainability, and
    separation of concerns

------------------------------------------------------------------------

## Notes

-   Rendering and visuals are intentionally minimal\
-   Physics and collision behaviour are simplified for clarity\
-   Emphasis is placed on clean architecture, subsystem isolation, and
    extensibility\
-   The project focuses on demonstrating architectural design rather
    than graphical complexity
