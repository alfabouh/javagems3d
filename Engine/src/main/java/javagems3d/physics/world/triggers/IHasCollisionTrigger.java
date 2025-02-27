package javagems3d.physics.world.triggers;

public interface IHasCollisionTrigger {
    ITriggerAction onColliding();

    default boolean isValid() {
        return this.onColliding() != null;
    }
}
