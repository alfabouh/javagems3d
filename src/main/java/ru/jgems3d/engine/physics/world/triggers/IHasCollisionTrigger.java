package ru.jgems3d.engine.physics.world.triggers;

public interface IHasCollisionTrigger {
    ITriggerAction onColliding();
    default boolean isValid() {
        return this.onColliding() != null;
    }
}
