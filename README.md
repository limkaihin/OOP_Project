# OOP_Project --- INF1009 Part 2

This project is a libGDX desktop application demonstrating a
game that is built upon an Abstract Engine. The engine is built 
using Object-Oriented Programming principles and a modular system architecture.

The engine provides reusable, game-agnostic systems such as: -
Scene Management - Entity Management - Movement Management - Collision
Management (with spatial partitioning support) - Input / Output
Management - Event Bus System - Render Queue (engine outputs render
commands; application renders them) - Progress Tracker

The game intends to  model the experience of boarding a train in a crowded environment
in Singapore's context. The goal in mind is to engage kids and interest adults, and to
teach them the importance of letting passengers alight first.

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
  Increase / Reduce Volume   Left Click in Settings Menu
  Play or Stop Music         Left Click in Settings Menu
  Start Game                 Enter
  Settings Menu / Pause      Esc
  Quit Application           Left Click

Key bindings are defined in the demo layer, not inside the
engine.

------------------------------------------------------------------------

## Engine Features Overview

### Scene Management

-   Application starts in a MenuScene
-   Transitions into a LevelSelectScene
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
    game -specific behaviour in the engine

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

Contains: - EngineContext - Core managers (scene, entity,
movement, collision, input, output) - ECS components - Event system -
Spatial hash grid implementation - Render output abstractions
(RenderCommand, RenderQueue)

No game-specific logic exists in this layer.

------------------------------------------------------------------------

### Demo / Game Layer

    core/src/main/java/com/example/app/demo/scenes/

Contains: - MenuScene --- configuration and entry point -
TransitionScene --- controlled scene switching - TrainScene ---
game logic, entity spawning, movement, and collision interaction

The demo can be replaced without modifying the engine layer.

------------------------------------------------------------------------

## Design Intent

-   The engine is reusable and extensible\
-   Core systems are modular and decoupled\
-   New games can be built by replacing only the demo layer\
-   The application layer adapts libGDX lifecycle and rendering to
    engine output\
-   Architecture prioritizes scalability, maintainability, and
    separation of concerns

------------------------------------------------------------------------
