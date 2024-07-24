package ru.alfabouh.jgems3d.engine.physics.world.triggers;

public interface ICollideTrigger {
    ITriggerAction onColliding();
    default boolean isValid() {
        return this.onColliding() != null;
    }
}
