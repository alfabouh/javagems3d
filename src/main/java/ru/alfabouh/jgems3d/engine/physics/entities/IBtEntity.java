package ru.alfabouh.jgems3d.engine.physics.entities;

import ru.alfabouh.jgems3d.engine.physics.entities.properties.collision.ICollisionFilter;
import ru.alfabouh.jgems3d.engine.physics.entities.properties.state.IEntityState;
import ru.alfabouh.jgems3d.engine.physics.world.triggers.ICollideTrigger;

public interface IBtEntity extends IEntityState, ICollideTrigger, ICollisionFilter {
}
