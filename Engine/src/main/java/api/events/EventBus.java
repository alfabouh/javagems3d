package api.events;

import javagems3d.graphics.world.SceneWorld;
import javagems3d.physics.world.PhysicsWorld;
import javagems3d.physics.world.basic.IWorldObject;

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

    public static final class MapLoading implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final SceneWorld sceneWorld;
        public final Run run;

        public MapLoading(Run run, PhysicsWorld physicsWorld, SceneWorld sceneWorld) {
            this.physicsWorld = physicsWorld;
            this.sceneWorld = sceneWorld;
            this.run = run;
        }
    }

    public static final class SceneWorldState implements IEvent {
        public final SceneWorld sceneWorld;
        public final State state;

        public SceneWorldState(State state, SceneWorld sceneWorld) {
            this.state = state;
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class PhysicsWorldState implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final State state;

        public PhysicsWorldState(State state, PhysicsWorld physicsWorld) {
            this.state = state;
            this.physicsWorld = physicsWorld;
        }
    }

    public static final class SceneWorldUpdate extends Cancellable implements IEvent {
        public final SceneWorld sceneWorld;

        public SceneWorldUpdate(SceneWorld sceneWorld) {
            this.sceneWorld = sceneWorld;
        }
    }

    public static final class PhysicsWorldUpdate extends Cancellable implements IEvent {
        public final PhysicsWorld physicsWorld;

        public PhysicsWorldUpdate(PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
        }
    }

    public static final class WorldObjectState implements IEvent {
        public final IWorldObject worldObject;
        public final Run run;
        public final ObjectState state;

        public WorldObjectState(Run run, ObjectState state, IWorldObject worldObject) {
            this.run = run;
            this.state = state;
            this.worldObject = worldObject;
        }
    }
}