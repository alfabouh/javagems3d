/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package ru.jgems3d.engine_api.events.bus;

import org.joml.Vector2i;
import ru.jgems3d.engine.graphics.opengl.environment.light.Light;
import ru.jgems3d.engine.graphics.opengl.rendering.items.objects.AbstractSceneEntity;
import ru.jgems3d.engine.graphics.opengl.rendering.programs.fbo.FBOTexture2DProgram;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.JGemsOpenGLRenderer;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.render_base.SceneRenderBase;
import ru.jgems3d.engine.graphics.opengl.rendering.scene.tick.FrameTicking;
import ru.jgems3d.engine.graphics.opengl.world.SceneWorld;
import ru.jgems3d.engine.physics.world.PhysicsWorld;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsSystem;
import ru.jgems3d.engine.physics.world.triggers.IHasCollisionTrigger;
import ru.jgems3d.engine.physics.world.triggers.ITriggerAction;
import ru.jgems3d.engine.system.map.loaders.IMapLoader;

/**
 * This class contains many Events that will allow you to execute any code in certain places of the engine.
 * <br><br>
 * Some events can be <b>cancelled</b> if they are inherited from <b>Events.Cancellable</b>.
 * <br><i>Cancelling the event means that the standard logic of the engine will not be executed</i>
 * Example of code execution in an event:
 * <pre>
 *     {@code
 *          @SubscribeEvent
 *          public static void onWorldTick(Events.PhysWorldTickPre event) {
 *              if (event.canBeCancelled()) {
 *                  event.setCancelled(true);
 *              }
 *          }
 *     }
 * </pre>
 */
public abstract class Events {
    @SuppressWarnings("all")
    public interface IEvent {
        default boolean canBeCancelled() {
            return this instanceof Cancellable;
        }

        default boolean isCancelled() {
            return this.canBeCancelled() && ((Cancellable) this).isCancelled();
        }
    }

    public static abstract class Cancellable {
        private boolean isCancelled;

        public Cancellable() {
            this.isCancelled = false;
        }

        public void setCancelled(boolean cancelled) {
            this.isCancelled = cancelled;
        }

        public boolean isCancelled() {
            return this.isCancelled;
        }
    }

    public enum Stage {
        PRE,
        POST;
    }




    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    //section PhysWorld
    public static final class PhysWorldTickPre extends Cancellable implements IEvent {
        public final PhysicsWorld physicsWorld;

        public PhysWorldTickPre(PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
        }
    }

    public static final class PhysWorldTickPost implements IEvent {
        public final PhysicsWorld physicsWorld;

        public PhysWorldTickPost(PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
        }
    }

    public static final class PhysWorldStart implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final Stage stage;

        public PhysWorldStart(Stage stage, PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
            this.stage = stage;
        }
    }

    public static final class PhysWorldEnd implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final Stage stage;

        public PhysWorldEnd(Stage stage, PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
            this.stage = stage;
        }
    }


    //section WorldItem
    public static final class WorldItemUpdatePre extends Cancellable implements IEvent {
        public final IWorldTicked worldTicked;

        public WorldItemUpdatePre(IWorldTicked worldTicked) {
            this.worldTicked = worldTicked;
        }
    }

    public static final class WorldItemUpdatePost implements IEvent {
        public final IWorldTicked worldTicked;

        public WorldItemUpdatePost(IWorldTicked worldTicked) {
            this.worldTicked = worldTicked;
        }
    }

    public static final class ItemSpawnedInPhysicsWorld implements IEvent {
        public final IWorldObject worldItem;

        public ItemSpawnedInPhysicsWorld(IWorldObject worldItem) {
            this.worldItem = worldItem;
        }
    }

    public static final class ItemDestroyedInPhysicsWorld implements IEvent {
        public final IWorldObject worldItem;

        public ItemDestroyedInPhysicsWorld(IWorldObject worldItem) {
            this.worldItem = worldItem;
        }
    }

    // section Bullet
    public static final class BulletUpdate implements IEvent {
        public final DynamicsSystem dynamicsSystem;

        public BulletUpdate(DynamicsSystem dynamicsSystem) {
            this.dynamicsSystem = dynamicsSystem;
        }
    }


    // section Collision
    public static final class CollisionTriggered extends Cancellable implements IEvent {
        public final IHasCollisionTrigger hasCollisionTrigger;
        public final ITriggerAction triggerAction;

        public CollisionTriggered(IHasCollisionTrigger hasCollisionTrigger, ITriggerAction triggerAction) {
            this.hasCollisionTrigger = hasCollisionTrigger;
            this.triggerAction = triggerAction;
        }
    }

    // section Map
    public static final class MapLoad implements IEvent {
        public final Stage stage;
        public final IMapLoader mapLoader;

        public MapLoad(Stage stage, IMapLoader mapLoader) {
            this.stage = stage;
            this.mapLoader = mapLoader;
        }
    }

    public static final class MapDestroy implements IEvent {
        public final Stage stage;
        public final IMapLoader mapLoader;

        public MapDestroy(Stage stage, IMapLoader mapLoader) {
            this.stage = stage;
            this.mapLoader = mapLoader;
        }
    }

    // section Resources

    public static final class ReloadResourcesEvent implements IEvent {

        public ReloadResourcesEvent() {

        }
    }

    // section RenderPostProcessing
    public static final class RenderPostProcessing extends Cancellable implements IEvent {
        public final JGemsOpenGLRenderer jGemsOpenGLRenderer;
        public final FrameTicking ticking;
        public final int sceneBufferTextureID;
        public final Vector2i windowSize;

        public RenderPostProcessing(FrameTicking ticking, Vector2i windowSize, int sceneBufferTextureID, JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
            this.ticking = ticking;
            this.sceneBufferTextureID = sceneBufferTextureID;
            this.windowSize = windowSize;
        }
    }

    // section Render

    public static final class RenderWorldStart implements IEvent {
        public final Stage stage;
        public final SceneWorld sceneWorld;

        public RenderWorldStart(Stage stage, SceneWorld sceneWorld) {
            this.stage = stage;
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class RenderWorldEnd implements IEvent {
        public final Stage stage;
        public final SceneWorld sceneWorld;

        public RenderWorldEnd(Stage stage, SceneWorld sceneWorld) {
            this.stage = stage;
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class RenderWorldTickPre extends Cancellable implements IEvent {
        public final SceneWorld sceneWorld;

        public RenderWorldTickPre(SceneWorld sceneWorld) {
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class RenderWorldTickPost implements IEvent {
        public final SceneWorld sceneWorld;

        public RenderWorldTickPost(SceneWorld sceneWorld) {
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class LightAdded implements IEvent {
        public final Light light;

        public LightAdded(Light light) {
            this.light = light;
        }
    }

    public static final class ItemSpawnInRenderWorld implements IEvent {
        public final AbstractSceneEntity abstractSceneEntity;

        public ItemSpawnInRenderWorld(AbstractSceneEntity abstractSceneEntity) {
            this.abstractSceneEntity = abstractSceneEntity;
        }
    }

    public static final class ItemDestroyInRenderWorld implements IEvent {
        public final AbstractSceneEntity abstractSceneEntity;

        public ItemDestroyInRenderWorld(AbstractSceneEntity abstractSceneEntity) {
            this.abstractSceneEntity = abstractSceneEntity;
        }
    }

    public static final class RenderScenePre extends Cancellable implements IEvent {
        public final JGemsOpenGLRenderer jGemsOpenGLRenderer;
        public final FrameTicking ticking;
        public final Vector2i windowSize;

        public RenderScenePre(FrameTicking ticking, Vector2i windowSize, JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
            this.ticking = ticking;
            this.windowSize = windowSize;
        }
    }

    public static final class RenderScenePost implements IEvent {
        public final JGemsOpenGLRenderer jGemsOpenGLRenderer;
        public final FrameTicking ticking;
        public final Vector2i windowSize;

        public RenderScenePost(FrameTicking ticking, Vector2i windowSize, JGemsOpenGLRenderer jGemsOpenGLRenderer) {
            this.jGemsOpenGLRenderer = jGemsOpenGLRenderer;
            this.ticking = ticking;
            this.windowSize = windowSize;
        }
    }

    public static final class RenderBaseStartRender implements IEvent {
        public final SceneRenderBase base;

        public RenderBaseStartRender(SceneRenderBase base) {
            this.base = base;
        }
    }

    public static final class RenderBaseEndRender implements IEvent {
        public final SceneRenderBase base;

        public RenderBaseEndRender(SceneRenderBase base) {
            this.base = base;
        }
    }

    public static final class RenderBaseRender extends Cancellable implements IEvent {
        public final SceneRenderBase base;
        public final FrameTicking ticking;

        public RenderBaseRender(FrameTicking ticking, SceneRenderBase base) {
            this.base = base;
            this.ticking = ticking;
        }
    }
}