package javagems3d.physics.world.triggers;

public interface IHasCollisionTrigger {
    ITriggerAction collisionTriggerFunc();

    default boolean isValid() {
        return this.collisionTriggerFunc() != null;
    }
}
