/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.physics.world.triggers.zones.base;

import javagems3d.physics.entities.properties.collision.IHasCollisionFilter;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.basic.IWorldTicked;
import javagems3d.physics.world.triggers.IHasCollisionTrigger;
import javagems3d.physics.world.triggers.Zone;

public interface ITriggerZone extends IWorldTicked, IWorldObject, IHasCollisionTrigger, IHasCollisionFilter {
    Zone getZone();
}
