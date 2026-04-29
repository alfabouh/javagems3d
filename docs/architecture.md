# Architecture

JavaGems3D is structured as a modular engine designed for game runtime execution and editor-based content development. The project is divided into three main modules:

* Launcher
* Engine
* Workbench

## Main Project Modules

### Launcher

The `launcher` module is responsible for application startup and bootstrapping.

It initializes the runtime environment, parses launch arguments, loads the required application context, and starts either the game runtime or the editor depending on the selected launch mode.

This is the main entry point of every JavaGems3D application.

### Engine

The `engine` module is the core of the entire project.

It contains rendering, physics, sound, internal runtime systems, and the API layer used by external game projects.

The engine can work independently without the editor.

### Workbench

The `workbench` module is the game editor.

It is used for map editing, project resource management, testing, and development workflows.

Workbench depends on the engine and reuses many of its systems directly. The editor cannot work without the engine, while the engine can run without the editor.

## Engine Internal Structure

The engine itself is divided into five major components:

* Rendering
* Physics
* Audio
* API
* Core Systems

## Rendering System

JavaGems3D uses OpenGL as its rendering backend.

Main renderer class:

```java
public class JGemsOpenGLRenderer
```

### Rendering Features

### Graphics API

* OpenGL 4.3

### Lighting

* Deferred lighting (fully implemented)
* Forward rendering (works together with deferred pipeline)
* Phong lighting model
* Directional light
* Point lights
* Spot lights are currently not implemented

### Shadows

* EVSM high-quality shadows for sunlight
* VSM shadows for local light sources

### Rendering Techniques

* Indirect rendering
* Direct rendering
* Hybrid usage of both approaches
* Skeletal animation
* Weighted OIT transparency
* Alpha discard transparency
* 2D skyboxes
* 3D skyboxes

### Post Processing

* SSAO
* HDR
* Bloom
* FXAA

### Asset Support

* Models: GLTF 2.0 only
* Images: PNG, BMP, JPG, JPEG, TFA
* Sounds: OGG only
* Shaders: fragment, vertex, geometry, compute

Currently, only GLTF2 is supported for 3D assets. This was chosen to keep the asset pipeline predictable and focused.

### UI System

The engine uses two UI systems:

* Custom immediate-mode UI system
* Dear ImGui integration for tools and editor workflows

### Current Limitations

Some rendering systems are still planned or under improvement:

* Light baking is not implemented yet
* Particle systems are not implemented yet
* Some FPS and memory optimization issues still exist in complex scenes

## Physics System

Physics is based on the Bullet Physics library.

### Features

* Runs in a dedicated separate thread
* Synchronization between physics and rendering is implemented
* Custom character kinematic controller

A custom character controller is used because the default Bullet implementation was not suitable for the required gameplay behavior.

## Audio System

Audio is based on OpenAL.

It uses a relatively standard implementation for 3D positional sound, playback management, and listener synchronization.

## API System

The API layer allows external projects to directly extend and control engine behavior.

It provides:

* custom application entry points
* event registration
* resource initialization
* UI overrides
* input bindings
* editor resource configuration

The API system is covered in detail in a separate chapter.

## Workbench Editor

The Workbench editor is also covered in a separate chapter.

It includes:

* map editing
* resource management
* testing tools
* runtime preview
* project setup utilities

## Design Philosophy

JavaGems3D is designed primarily for:

* small and medium-sized projects
* experimental game development
* custom engine architecture research
* tool-assisted development workflows

The goal is not to compete with large commercial engines, but to provide a controllable and extendable engine architecture with direct access to low-level systems.

This makes the project especially useful for learning engine architecture, rendering pipelines, and runtime/editor integration.
