package ru.jgems3d.engine.physics.world.triggers.zones.base;

import ru.jgems3d.engine.physics.entities.properties.collision.IHasCollisionFilter;
import ru.jgems3d.engine.physics.world.triggers.IHasCollisionTrigger;
import ru.jgems3d.engine.physics.world.triggers.Zone;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;

public interface ITriggerZone extends IWorldTicked, IWorldObject, IHasCollisionTrigger, IHasCollisionFilter {
    Zone getZone();
}
