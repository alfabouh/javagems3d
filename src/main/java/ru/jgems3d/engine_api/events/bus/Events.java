package ru.jgems3d.engine_api.events.bus;

import ru.jgems3d.engine.physics.world.PhysicsWorld;

public abstract class Events {
    public static final class PhysWorldTickEvent extends Cancellable implements IEvent {
        public final PhysicsWorld physicsWorld;
        public final Stage stage;

        public PhysWorldTickEvent(Stage stage, PhysicsWorld physicsWorld) {
            this.physicsWorld = physicsWorld;
            this.stage = stage;
        }
    }

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
}
