package ru.jgems3d.engine.physics.entities;

import ru.jgems3d.engine.physics.entities.properties.collision.ICollisionFilter;
import ru.jgems3d.engine.physics.entities.properties.state.IEntityState;
import ru.jgems3d.engine.physics.world.triggers.ICollideTrigger;

public interface IBtEntity extends IEntityState, ICollideTrigger, ICollisionFilter {
}
