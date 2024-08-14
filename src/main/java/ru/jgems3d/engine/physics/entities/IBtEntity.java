package ru.jgems3d.engine.physics.entities;

import ru.jgems3d.engine.physics.entities.properties.collision.IHasCollisionFilter;
import ru.jgems3d.engine.physics.entities.properties.state.IHasEntityState;
import ru.jgems3d.engine.physics.world.triggers.IHasCollisionTrigger;

public interface IBtEntity extends IHasEntityState, IHasCollisionTrigger, IHasCollisionFilter {
}
