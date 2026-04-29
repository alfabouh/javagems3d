package javagems3d.physics.world.triggers.zones.base;

import javagems3d.physics.entities.properties.collision.IHasCollisionFilter;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.Zone;

public interface ITriggerZone extends IWorldTicked, IWorldObject, IHasCollisionTrigger, IHasCollisionFilter {
    Zone getZone();
}
