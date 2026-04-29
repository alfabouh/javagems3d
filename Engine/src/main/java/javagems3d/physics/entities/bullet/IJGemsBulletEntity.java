package javagems3d.physics.entities.bullet;

import javagems3d.physics.entities.properties.collision.IHasCollisionFilter;
import javagems3d.physics.entities.properties.state.IHasEntityState;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;

public interface IJGemsBulletEntity extends IHasEntityState, IHasCollisionTrigger, IHasCollisionFilter {
}
