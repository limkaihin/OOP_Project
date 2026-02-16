OOP_Project — Abstract Engine Demo (INF1009)

This project is a libGDX-based desktop application demonstrating a reusable Abstract Game Engine built using Object-Oriented Programming principles and an Entity–Component–System (ECS) architecture.

The engine is simulation-agnostic and modular. The included demo exists purely to showcase engine capabilities.

Core Architectural Features
1. Entity–Component–System (ECS)

The engine follows an ECS design:

Entity — lightweight identifier

Component — pure data (e.g., Transform, Velocity, Collider, Renderable)

Manager/System — processes entities containing required components

This design ensures:

High reusability

Easy feature extension

Minimal code duplication

Clear separation between data and behaviour

2. Subsystem Isolation

The engine is divided into independent subsystems:

Scene Management

Entity Management

Movement Management

Collision Management

Input Management

Output Management

Render Queue System

Event Bus System

Each subsystem operates independently, reducing coupling and improving maintainability.

3. Scene Modularity

Scenes are fully modular and managed through a SceneManager.

Implemented scenes include:

MenuScene

SandboxScene

TransitionScene

Each scene:

Owns its entities

Defines its logic

Can be loaded/unloaded independently

This allows new game modes or simulations to be added without modifying the engine core.

4. Decoupled Managers

Managers communicate via:

Interfaces

EngineContext

EventBus

This prevents tight coupling between systems.

Example:

CollisionManager detects collisions

Emits events via EventBus

Other systems respond independently

This makes the architecture scalable and extensible.

5. Spatial Hash Grid (Collision Optimization)

Collision detection uses a SpatialHashGrid for broad-phase optimization.

Benefits:

Avoids O(n²) brute-force collision checks

Improves scalability with increasing entity count

Makes the engine capable of handling larger simulations efficiently

6. Render Queue Architecture

The engine does not render directly.

Instead:

Systems generate RenderCommands

Commands are pushed into a RenderQueue

The application layer renders them using libGDX

This cleanly separates:

Engine logic

Rendering implementation

Project Structure
Root Modules
core/      → Engine + Demo
lwjgl3/    → Desktop launcher
assets/    → Audio and visual resources

Core Engine Layer
core/src/main/java/com/example/app/engine/


Contains:

EngineImpl

EngineContext

Managers (MovementManager, CollisionManager, etc.)

ECS components

EventBus

SpatialHashGrid

RenderCommand / RenderQueue

This layer contains no simulation-specific logic.

Demo Layer
core/src/main/java/com/example/app/demo/


Contains:

Demo scenes

Game-specific input mapping

Example entity spawning logic

The demo can be replaced without modifying the engine.

Application Layer
core/src/main/java/com/example/app/


Key classes:

GameMaster

Extends ApplicationAdapter

Handles lifecycle: create(), render(), dispose()

Owns rendering resources (SpriteBatch, ShapeRenderer, etc.)

Main

Entry point compatibility

This layer adapts libGDX lifecycle to the abstract engine.

How to Run
Desktop (LWJGL3)

From the root directory:

Windows
.\gradlew.bat lwjgl3:run

macOS / Linux
./gradlew lwjgl3:run


Note: Gradle will remain in EXECUTING state until the window is closed. This is expected.

Controls
Action	Key
Move Player	W A S D
Spawn Obstacle	Space
Increase Volume	Up
Decrease Volume	Down
Play Music	Right
Stop Music	Left
Start / Confirm	Enter
Pause / Resume	P
Quit	Esc

Key bindings are defined in the demo layer.

UML Overview

The UML diagram illustrates:

ECS relationships

Manager dependencies

Scene hierarchy

Interface usage

Low coupling and high cohesion design

UML files are located in:

docs/uml/

Design Goals

Reusable engine architecture

Clear separation of concerns

Extensible scene system

Scalable collision system

Decoupled subsystems

Platform abstraction (desktop launcher separated from core)

Educational Focus

This project emphasizes:

Object-Oriented Design Principles

SOLID principles

ECS architecture

Modular system design

Low coupling, high cohesion

Scalable engine structure

The demo is intentionally simple — the focus is architectural clarity rather than visual complexity.