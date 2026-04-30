# API

## Overview

The API is the main integration layer for working with the JGems engine.

It is responsible for:

- application bootstrap
- game lifecycle control
- asset and resource loading
- event registration
- scripting integration
- UI initialization
- editor (`WBench`) integration
- access to engine subsystems (rendering, physics, world, audio, input)

The API layer is intentionally separated from the engine core.  
The engine (`JGems3D`) does not depend on the game application — the application depends on the engine.

---

## API Integration

To use the engine, an external application layer must be connected to runtime.

There are two main ways to do this.

---

### 1. JAR deployment (production)

The application can be packaged as a separate `.jar` and placed inside the `api/` directory near the engine core JAR.

This is the recommended approach for production builds.

Typical structure:

```text
engine/
├── core.jar
├── api/
│   └── game.jar
```

---

### 2. Runtime classpath injection

You can also define the application entry point manually using launch arguments.

Launch argument:

`api_app_classpath="classpath"`

Example:

```bash
java -cp game.jar;engine-core.jar;api.jar ^
-Dapi_app_classpath="com.game.AppDefault" ^
-jar engine-core.jar
```

Requirements:

- the class must be available in runtime classpath
- the class must be accessible via `-cp`
- the class must be annotated with `@JGemsAppEntry`
- the class must extend `JGemsApplication`

This approach is useful for development and testing.

---

## Application Entry Point

The application entry point is defined using:

```java
@JGemsAppEntry(id = "DefaultGame")
public class AppDefault extends JGemsApplication {
```

### Rules

- only one entry point is allowed per project
- the class must extend `JGemsApplication`

`JGemsApplication` contains the main initialization interfaces used by the engine.

---

## Application Instance Injection

Optional singleton injection:

```java
@JGemsAppInstance
public static AppDefault appDefault;
```

If this annotation is present, the engine automatically injects the current runtime instance of the application.

Useful for global access.

---

## Debug Mode

```java
JGems3D.DEBUG_MODE = true;
```

Enables global debug mode.

This affects:

- rendering diagnostics
- debug lines
- collision visualization
- lighting debug
- editor diagnostics

---

# Application Lifecycle

The engine calls several initialization methods during startup.

---

## Scripts

```java
@Override
public void initScripts(IAppScriptContextRegistry appScriptRegistry) {
    appScriptRegistry.addGlobalGameContextScript(
        "A_default_app.scripts.test"
    );
}
```

Used for script registration.

Supported contexts:

- global context → entire game
- local context → current map only

This allows script wrappers and JS integrations.

> Full scripting documentation will be added later.

---

## Events

```java
@Override
public void initEvents(IAppEventSubscriber appEventSubscriber) {
    appEventSubscriber.addClassWithEvents(TestEvents.class);
}
```

Registers event hook classes.

Used for:

- gameplay events
- engine callbacks
- interaction hooks
- runtime listeners

> Full event system documentation will be added later.

---

## Resources

```java
@Override
public void initResources(IAppResources appResources) {
    appResources.putGlobalAssetsInitializer(new ModelInitializer());
}
```

Registers resource loaders.

This is where meshes, textures, models and other assets are loaded.

---

# Asset Initialization

Every resource loader must implement:

```java
IAssetsInitializer
```

---

## Example

```java
public class ModelInitializer implements IAssetsInitializer {

    @Override
    public void load(SystemResources systemResources) {
        // load models here
    }

    @Override
    public LaunchMode loadMode() {
        return LaunchMode.REGULAR;
    }

    @Override
    public LoadPriority loadPriority() {
        return LoadPriority.LOW;
    }
}
```

### Notes

- inheritance from `IAssetsInitializer` is required
- supports models, textures, animations, materials
- loading order is controlled by `LoadPriority`
- loading stage is controlled by `LaunchMode`

> Detailed asset system documentation will be added later.

---

# Input Binding

```java
@Override
public BindingManager getBindingManager() {
    return new DefaultBindings();
}
```

Responsible for mapping:

- keyboard input
- mouse input
- controller input

to gameplay actions.

This is the main input binding layer.

---

# Main Menu UI

```java
@Override
public PanelUI getMainMenuPanel() {
    if (JavaToJsAPI.uiContainer.getMainMenuPanel() == null) {
        return new TestMainMenuPanel(null);
    }

    return JavaToJsAPI.uiContainer.getMainMenuPanel().second();
}
```

Defines the main menu panel of the game.

### Important

This method must always return a valid `PanelUI`.

Fallback UI should always exist.

---

# Window Properties

```java
@Override
public Window.WindowProperties getWindowProperties() {
    return new Window.WindowProperties(
        "DefaultGame",
        new JGemsPath(Window.DEFAULT_ICON)
    );
}
```

Defines:

- window title
- application icon
- startup window settings

---

# Editor Integration

```java
@Override
public void setupEditorResources(IAPIWBenchDataManager manager) {
}
```

Used only for the map editor (`WBench`).

Typical usage:

- registering props
- terrain objects
- environment assets
- default editor entities

This does not affect gameplay runtime directly.

---

# Global Engine Configuration

The engine provides a global configuration layer:

```java
JGemsConfig
```

---

## Debug Configuration

```java
JGemsConfig.DEBUG
```

Used for runtime diagnostics.

Examples:

- `WIREFRAME_RENDERING`
- `SHOW_CASCADES`
- `FULL_BRIGHT`
- `FOG`
- `SHOW_DEBUG_LINES`

Useful for development and debugging.

---

## System Configuration

```java
JGemsConfig.SYSTEM
```

Defines engine-wide technical limits.

Examples:

- default resolution
- FOV
- render distance
- shadow system limits
- point light limits
- indirect rendering limits
- animation limits
- HDR / SSAO / Bloom / FXAA toggles
- UI scaling
- camera speed and sensitivity

Important examples:

```java
JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH
JGemsConfig.SYSTEM.MAX_POINT_LIGHTS
JGemsConfig.SYSTEM.USE_SHADOWS
JGemsConfig.SYSTEM.CAM_SPEED
```

This is the main place for low-level engine tuning.

---

# JGemsHelper

`JGemsHelper` is the main helper facade for accessing engine subsystems.

Instead of accessing internal engine classes directly, API code should use:

```java
JGemsHelper
```

This provides safe centralized access.

---

## Main Subsystems

```java
JGemsHelper.render();
JGemsHelper.world();
JGemsHelper.camera();
JGemsHelper.controller();
JGemsHelper.map();
JGemsHelper.ui();
JGemsHelper.state();
JGemsHelper.resources();
JGemsHelper.files();
JGemsHelper.math();
```

Available systems:

- rendering
- world
- map
- physics
- audio
- UI
- camera
- controller
- resources
- localisation
- files
- math utilities

---

# Camera System

```java
JGemsHelper.camera().setCurrentCamera(camera);
JGemsHelper.camera().enableFreeCamera(controller, pos, rot);
JGemsHelper.camera().enableAttachedCamera(entity);
```

Features:

- free camera mode
- attached entity camera
- runtime camera switching

Useful for gameplay, cutscenes and editor tools.

---

# Controller System

```java
JGemsHelper.controller().lockController();
JGemsHelper.controller().attachControllerTo(controller, target);
JGemsHelper.controller().detachController();
JGemsHelper.controller().setCursorInCenter();
```

Features:

- active controller access
- controller locking
- player/controller attachment
- cursor management
- binding access

---

# Map System

```java
JGemsHelper.map().loadMap(processor);
JGemsHelper.map().exitMap();
JGemsHelper.map().getCurrentGameMap();
```

Features:

- load maps
- unload maps
- get current player
- validate current map state
- access map-relative paths

---

# World System

```java
JGemsHelper.world().addWorldItem(item, renderData);
JGemsHelper.world().addProp(prop);
JGemsHelper.world().addLight(light);
JGemsHelper.world().addLiquid(liquid, liquidData);
```

Used for runtime world manipulation.

Supports:

- entities
- props
- liquids
- lights
- skybox
- fog
- environment access

This is one of the most important gameplay APIs.

---

# UI System

```java
JGemsHelper.ui().openMainMenu();
JGemsHelper.ui().openPanel(panel);
JGemsHelper.ui().closePanel();
```

Controls runtime UI panels.

Useful for:

- pause menu
- inventory
- settings
- custom in-game interfaces

---

# State Control

```java
JGemsHelper.state().pauseGame(true);
JGemsHelper.state().resumeGame();
JGemsHelper.state().pauseGameAndLockResume(true);
```

Controls:

- game pause
- sound pause
- resume locking

Useful for:

- menus
- cutscenes
- loading transitions
- editor mode switching

---

# Resource Access

```java
JGemsHelper.resources().getGlobalGameResources();
JGemsHelper.resources().reloadResources();
```

Used for:

- accessing local/global resources
- animation texture buffers
- runtime resource reload

Helpful during development and hot reload workflows.

---

# Summary

The JGems API is built around strict modular separation:

- application layer
- engine core
- runtime subsystems
- editor subsystem
- helper facade (`JGemsHelper`)

This architecture allows:

- modular game development
- isolated editor workflow
- runtime extensibility
- scalable rendering systems
- high-performance engine-side control

The API should be considered the primary public interface for all gameplay and editor-side development.
