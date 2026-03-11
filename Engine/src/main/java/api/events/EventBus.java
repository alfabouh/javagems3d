package api.events;

import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.renderer.OpenGLRenderer;
import javagems3d.graphics.rendering.scene.renderer.nodes.base.IRenderNode;
import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.system.resources.managing.ResourceManager;

import java.util.Set;

public abstract class EventBus {
    public enum ObjectState {
        SPAWN,
        DESTROY
    }

    public enum State {
        START,
        END
    }

    public enum Run {
        PRE,
        POST
    }

    public interface IEvent {
        default boolean canBeCancelled() {
            return this instanceof Cancellable;
        }

        @SuppressWarnings("all")
        default boolean isCancelled() {
            return this.canBeCancelled() && ((Cancellable) this).isCancelled();
        }

        class EmptyEvent implements IEvent {
        }
    }

    public static abstract class Cancellable {
        private boolean isCancelled;

        public Cancellable() {
            this.isCancelled = false;
        }

        public boolean isCancelled() {
            return this.isCancelled;
        }

        public void setCancelled(boolean cancelled) {
            this.isCancelled = cancelled;
        }
    }


    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public record MapLoading(Run run, PhysicsWorld physicsWorld, SceneWorld sceneWorld) implements IEvent {
    }

    public record SceneWorldState(State state, SceneWorld sceneWorld) implements IEvent {
    }

    public record PhysicsWorldState(State state, PhysicsWorld physicsWorld) implements IEvent {
    }

    public static final class SceneWorldUpdate extends Cancellable implements IEvent {
        public final SceneWorld sceneWorld;
        public final Run run;

        public SceneWorldUpdate(Run run, SceneWorld sceneWorld) {
            this.run = run;
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class PhysicsWorldUpdate extends Cancellable implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final Run run;

        public PhysicsWorldUpdate(Run run, PhysicsWorld physicsWorld) {
            this.run = run;
            this.physicsWorld = physicsWorld;
        }
    }

    public record WorldObjectState(Run run, ObjectState state, IWorldObject worldObject) implements IEvent {
    }

    public static final class WorldObjectUpdate extends Cancellable implements IEvent {
        public final IWorldTicked worldTicked;
        public final Run run;

        public WorldObjectUpdate(Run run, IWorldTicked worldTicked) {
            this.run = run;
            this.worldTicked = worldTicked;
        }
    }

    public static final class OpenGLRendererState extends Cancellable implements IEvent {
        public final OpenGLRenderer openGLRenderer;
        public final State state;

        public OpenGLRendererState(State state, OpenGLRenderer openGLRenderer) {
            this.state = state;
            this.openGLRenderer = openGLRenderer;
        }
    }

    public static final class OpenGLRendererProcess extends Cancellable implements IEvent {
        public final OpenGLRenderer openGLRenderer;
        public final Run run;
        public final Set<SceneObject> toRender;

        public OpenGLRendererProcess(Run run, Set<SceneObject> toRender, OpenGLRenderer openGLRenderer) {
            this.toRender = toRender;
            this.run = run;
            this.openGLRenderer = openGLRenderer;
        }
    }

    public static final class OpenGLNodeRenderProcess extends Cancellable implements IEvent {
        public final OpenGLRenderer openGLRenderer;
        public final Run run;
        public final IRenderNode renderNode;

        public OpenGLNodeRenderProcess(Run run, IRenderNode renderNode, OpenGLRenderer openGLRenderer) {
            this.renderNode = renderNode;
            this.run = run;
            this.openGLRenderer = openGLRenderer;
        }
    }

    public static class CollisionTriggered extends Cancellable implements IEvent {
        public final IHasCollisionTrigger object;
        public final ITriggerAction triggerAction;

        public CollisionTriggered(IHasCollisionTrigger object, ITriggerAction triggerAction) {
            this.object = object;
            this.triggerAction = triggerAction;
        }
    }

    public record ReloadResourcesEvent(ResourceManager resourceManager) implements IEvent {
    }
}