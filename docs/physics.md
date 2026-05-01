# Physics System

JavaGems3D currently uses Libbulletjme as the main physics backend.

Official overview: https://stephengold.github.io/Libbulletjme/lbj-en/English/overview.html

The physics layer runs in a separate dedicated thread and is synchronized with the rendering pipeline.

The goal of the physics system is not just to expose raw Bullet functionality, but to provide a higher-level API that is easier to use inside real gameplay code.

Because of this, direct interaction with raw Bullet objects is generally not recommended unless there is a very specific reason.

---

# Physics Thread

Physics simulation runs independently from rendering.

By default:

```text
TPS = 40
```

This means physics updates happen 40 times per second.

The physics service is started through:

```java
JGemsPhysics
```

Internally it uses:

```java
Executors.newSingleThreadExecutor(...)
```

which creates a dedicated physics thread.

---

# Render Synchronization

Rendering and physics are synchronized manually.

The main synchronization happens inside:

```java
JGemsScene
```

Rendering receives:

```java
new FrameTicking(elapsedTime, frameDeltaTime)
```

This object contains interpolation values between physics ticks.

It is used for:

* transform interpolation
* smooth movement between physics updates
* animation timing
* render-side prediction helpers

This prevents visible jitter when render FPS is much higher than physics TPS.

Physics state is stepped independently, while rendering smoothly interpolates between states.

---

# Physics Startup

The main physics entry point is:

```java
DynamicsSystem
```

Responsibilities:

* native Bullet library loading
* physics world initialization
* collision configuration
* gravity setup
* collision trigger handling
* stepping simulation
* object registration

Native libraries are automatically extracted into:

```java
JGems3D.getEngineFilesFolder()
```

and loaded from there during engine startup.

This avoids requiring manual native installation.

---

# Physics Space

The engine creates a Bullet:

```java
PhysicsSpace
```

with configured:

* world bounds
* broadphase type
* solver type
* collision configuration
* gravity

Default gravity:

```java
(0, -10, 0)
```

This is the low-level simulation space where all rigid bodies exist.

---

# Physics Simulation Loop

Actual simulation happens inside:

```java
PhysicsProcessor
```

This is responsible for:

* stepping physics world
* updating physics objects
* collision checks
* trigger execution
* synchronization with render thread

Simplified flow:

```text
World Update
→ Bullet Step
→ Collision Test
→ Trigger Events
```

This repeats every physics tick.

---

# Physics World

The main gameplay entry point is:

```java
PhysicsWorld
```

This is the object developers should work with.

It manages:

* world objects
* spawning
* destruction
* ticking
* world events
* physics entities

You should create and manage physics objects through `PhysicsWorld`, not directly through raw Bullet APIs.

---

# Utility Functions

Helper methods are available in:

```java
DynamicsUtils
```

Examples:

* vector conversion between JOML and JME
* transform creation
* rigid body movement
* rigid body rotation
* rigid body scaling
* interpolation helpers
* collision mesh generation

Example:

```java
DynamicsUtils.translateRigidBody(...)
DynamicsUtils.rotateRigidBody(...)
DynamicsUtils.createTransform(...)
```

These wrappers simplify common operations and avoid repetitive conversion code.

---

# Recommended Physics Wrappers

The recommended way to create physics objects is through engine wrappers.

## Static Bodies

```java
JGemsStaticBody
```

Used for:

* walls
* floors
* environment geometry
* non-moving world objects

Example:

```java
JGemsStaticBody worldModeledBrush = (JGemsStaticBody) new JGemsStaticBody(MeshCollider.getStatic(ground2), world, new Vector3f(0.0f), "grass").setCanBeDeleted(false);
JGemsHelper.world().addWorldItem(worldModeledBrush, new EntityRenderData(JGemsResourceManager.globalRenderDataAssets.ground, ground2));
worldModeledBrush.setPosition(new Vector3f(0, -5, 0));
```

---

## Dynamic Bodies

```java
JGemsDynamicBody
```

Used for:

* movable objects
* physical props
* dynamic entities
* simulation-driven gameplay objects

Example:

```java
jGemsBody = new JGemsDynamicBody(MeshCollider.getDynamic(meshStructure3D), physicsWorld, new Vector3f(0.0f), template.getObjectNameId()).setCanBeDeleted(false);
JGemsHelper.world().addWorldItem(jGemsBody, entityRenderData);
jGemsBody.setPosition(template.getPosition() == null ? new Vector3f(0.0f) : template.getPosition());
jGemsBody.setRotation(template.getRotation() == null ? new Vector3f(0.0f) : template.getRotation());
jGemsBody.setScaling(template.getScaling() == null ? new Vector3f(1.0f) : template.getScaling());
```

These wrappers already contain:

* body creation
* transform handling
* physics parameter control
* world integration
* engine-side synchronization

This is the preferred approach.

---

# Raw Bullet Access

If direct Bullet-level control is required, there is also:

```java
BulletBody
```

This allows working directly with:

```java
PhysicsRigidBody
```

while still staying integrated with the engine world system.

This should only be used for advanced custom cases.

For normal gameplay development, wrappers are strongly preferred.

---

# Collision Shapes

Collision shapes are generated during model loading.

The default system uses:

```java
MeshCollisionData.DefaultFabric
```

Rules:

## Static Objects

Usually use:

```java
MeshCollisionShape
```

based on the real mesh.

This provides accurate environment collision.

---

## Dynamic Objects

Usually use:

```java
HullCollisionShape
```

for smaller objects.

For large objects the engine may fallback to simplified:

```java
BoxCollisionShape
```

for better performance.

This avoids expensive runtime collision cost.

---

# Custom Collision Factories

Developers can create custom collision factories using:

```java
MeshCollisionData.Fabric
```

This allows full control over:

* static collision shape generation
* dynamic collision shape generation
* animated object collision shapes

This is useful for special gameplay requirements or optimization.

---

# Collision Triggers

Collision trigger support is built into the physics system.

Objects implementing collision trigger interfaces can automatically react to contact events.

Flow:

```text
Collision Detected
→ Trigger Created
→ EventBus Event
→ Custom Action Executed
```

This allows gameplay logic like:

* pickups
* doors
* scripted events
* area triggers
* quest interactions

without manual low-level collision handling.

---

# Custom Kinematic Controller

The default Bullet kinematic controller was not good enough for gameplay usage, so JavaGems3D uses a fully custom implementation.

Main base class:

```java
JGemsKinematicItem
```

Player implementation:

```java
JGemsKinematicPlayer
```

This controller handles:

* movement
* jump logic
* ground detection
* body velocity
* collision response
* player state
* controlled character behavior

It is still being improved and is far from perfect, but in practice it works significantly better than the default Bullet controller.

This is the standard way to implement FPS-style or third-person player movement inside the engine.