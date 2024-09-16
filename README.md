![Made in Java](https://img.shields.io/badge/Made_in-Java-yellow)
[![](https://jitpack.io/v/alfabouh/javagems3d.svg)](https://jitpack.io/#alfabouh/javagems3d)
[![GitHub release](https://img.shields.io/badge/GitHub%20Packages-Download-brightgreen)](https://github.com/alfabouh/javagems3d/packages/2241468)
![OpenGL 4.3](https://img.shields.io/badge/Render-OpenGL_4.3-blue)
![Bullet Physics](https://img.shields.io/badge/Physics-Bullet-blue)
![Windows Support](https://img.shields.io/badge/Platform-Windows-brightgreen)

![ICOGIT](https://github.com/user-attachments/assets/f4d93bc9-279f-41bf-bcd1-d31f8eb056f9)
## JavaGems3D

**JavaGames3D** is a three-dimensional game engine written in java. **v0.2a** is the latest released version

The engine uses **OpenGL 4.3** for graphics, and the **Bullet Physics([Libbulletjme](https://github.com/stephengold/Libbulletjme))** as the physics engine.

***The project is at the initial stage of development, many features have not yet been implemented, and those that have been implemented have not been properly implemented. Use the best practices at your own risk!***

![screen_1725096928350](https://github.com/user-attachments/assets/48d10d31-2aa7-4e16-93a7-27d3720ec7f8)

### Projects, based on JavaGems3D:
[![ItchIO](https://img.shields.io/badge/JGems_Projects-ItchIO-brightgreen)](https://alfabouh.itch.io/)

## Engine Features(v0.2a)

| Feature | Implementation |
|----------------------------|----------------------|
| 3D Rendering | ✅ OpenGL 4.3 |
| Physics support | ✅ Bullet Physics |
| Deferred Rendering | ✅ Implemented |
| Shadows | ✅ Cascaded VSM |
| Lighting | ✅ Point lights, Directional light |
| Post-Processing | ✅ HDR, Bloom |
| Sound System | ✅ OpenAL |
| Dear ImGui | ✅ Implemented |
| OS Supporting | ⚠️ Windows |
| Docs | ⚠️ 0.01% |
| Map System | ⚠️ Implemented |
| Map Editor | ⚠️ Implemented |
| Particle System | ⚠️ Implemented |
| Engine API | ⚠️ Implemented |
| UI System | ⚠️ Immediate UI System |
| OIT | ⚠️ Weighted OIT |
| AI for Entities | ⚠️ Requires reworking |
| NavMesh System | ⚠️ Requires reworking |
| Multi-Threading | ⚠️ Render and physics work in parallel |
| Global illumination | ❌ Not developed |
| Ray-Tracing | ❌ Not developed |
| PBR | ❌ Not developed |
| Network Multiplayer | ❌ Not developed |
| Skeletal Animation | ❌ Not developed |
| LOD | ❌ Not developed |

- ✅ The feature is developed and works stably
- ⚠️ The feature has been developed, but requires reworking/revision
- ❌ The feature has not been developed, but it is planned in the future

### Goal
The main goal of the project is to create a lightweight java game engine for the development of simple three-dimensional games

## Get Started

### 1. Add Dependency

To include JavaGems3D in your project, add the following dependency to your `pom.xml`:

```xml
<dependency>
  <groupId>com.github.alfabouh</groupId>
  <artifactId>javagems3d</artifactId>
  <version>0.2</version>
</dependency>
```
### 2. Initializing the engine

Then you need to initialize the engine itself in the main method of your program:

```Java
public class Main {
    public static void main(String[] args) {
        JavaGemsLauncher.launch(args);
    }
}
```
The engine also involves the use of the **ToolBox** map editing program. You can initialize this program in a similar way instead of running the standard engine.
```Java
public class Main {
    public static void main(String[] args) {
        ToolBoxLauncher.launch(args);
    }
}
```
- Also, ToolBox can be started by writing the codeword ***toolbox*** in the startup parameters

### 3. Working with the API
*The API of the engine does not have too many useful functions at the moment, but there are enough of them to implement the basic features*
* Before you start working, it should be noted that the starter classes that represent your program must be located in the ***jgems_api*** directory
  
![image](https://github.com/user-attachments/assets/61c5c650-85b8-4c0b-8f6f-a6e0da36316b)

* Create a class that will be the base class of your application. It must have the ***@JGemsGameEntry*** annotation and must implement the ***JGemsGameApplicationn*** interface.

  Example:
```Java
@JGemsGameEntry(gameTitle = "Test", gameVersion = "0.1a", devStage = JGemsGameEntry.DevStage.PRE_ALPHA)
public class TestGame implements JGemsGameApplication {
    @JGemsGameInstance
    private static TestGame game;

    public TestGame() {
    }

    @Override
    public void loadResources(IAppResourceLoader appResourceLoader) {
        appResourceLoader.addAssetsLoader(new ModelLoader());
    }

    @Override
    public void subscribeEvents(IAppEventSubscriber appEventSubscriber) {
        appEventSubscriber.addClassWithEvents(TestEvents.class);
    }

    @Override
    public void preInitEvent(EngineSystem engineSystem) {
        JGems3D.DEBUG_MODE = true;
    }

    @Override
    public void postInitEvent(EngineSystem engineSystem) {
    }

    @Override
    public @NotNull AppManager createAppManager() {
        return new TestManager(null);
    }
}
```

- Using the ***@JGemsGameInstance*** annotation, you can mark an entity to which the value of an instance of the main ***@JGemsGameEntry*** class will be assigned using API mechanisms

```Java
    @JGemsGameInstance
    private static TestGame game;
```

- Create a class that will populate the object database for the *ToolBox* Map editor. The class must have the ***@JGemsTBoxEntry*** annotation and must implement the interface ***JGemsTBoxApplication***

  Example:
```Java
@JGemsTBoxEntry
public class TestTBoxApp implements JGemsTBoxApplication {
    public static ObjectCategory PHYSICS_OBJECT = new ObjectCategory("Entities");
    public static ObjectCategory PROP_OBJECT = new ObjectCategory("Props");
    public static ObjectCategory MARKER_OBJECT = new ObjectCategory("Markers");
    public static ObjectCategory LIQUID_OBJECT = new ObjectCategory("Liquids");

    @Override
    public void initEntitiesObjectData(TBoxResourceManager tBoxResourceManager, ITBoxEntitiesObjectData tBoxEntitiesObjectData) {
        Attribute<Vector3f> transformPosXYZ = new Attribute<>(AttributeTarget.POSITION_XYZ, AttributeID.POSITION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformRotXYZ = new Attribute<>(AttributeTarget.ROTATION_XYZ, AttributeID.ROTATION_XYZ, new Vector3f(0.0f));
        Attribute<Vector3f> transformScaleXYZ = new Attribute<>(AttributeTarget.SCALING_XYZ, AttributeID.SCALING_XYZ, new Vector3f(1.0f));
        tBoxEntitiesObjectData.add("cube", new TObjectData(new ModeledObjectData(new AttributesContainer(transformPosXYZ, transformRotXYZ, transformScaleXYZ), tBoxResourceManager.getShaderAssets().world_object, tBoxResourceManager.createModel(new JGemsPath("/assets/jgems/models/cube/cube.obj")), TestTBoxApp.PHYSICS_OBJECT)));
    }

    @Override
    public void initEntitiesUserData(JGemsResourceManager jGemsResourceManager, TBoxEntitiesUserData tBoxEntitiesUserData) {
        tBoxEntitiesUserData.add("cube", new TUserData(new TDefaultRenderContainer(new RenderEntity(), EntityObject.class, new JGemsPath("/assets/jgems/models/cube/cube.obj"), new JGemsPath(JGems3D.Paths.SHADERS, "world/world_gbuffer"), new MeshRenderAttributes())));
    }
}
```

- Most of the useful functions are placed in the ***JGemsHelper*** utility class

  Example:
```Java
        JGemsHelper.GAME.unPauseGameAndUnLockUnPausing();
        JGemsHelper.GAME.unLockController();
        JGemsHelper.CAMERA.setCurrentCamera(null);
        JGemsHelper.WINDOW.setWindowFocus(false);
```

### Examples
- *Below are links to repositories where you can see examples of working programs, based on JavaGems3D*
- [JavaGems3DExample](https://github.com/alfabouh/JavaGems3DExample)
- [JavaGems3DHorror](https://github.com/alfabouh/JavaGems3DHorror)

## Some screenshots

![2024-08-31_14-20-03](https://github.com/user-attachments/assets/3a821656-dd65-4280-886b-ae8641b787e8)
![screen_1725103122362](https://github.com/user-attachments/assets/54e869af-21a2-45eb-872a-8c405294181d)

