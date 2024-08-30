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

package ru.jgems3d.engine.physics.world.triggers.zones.base;

import ru.jgems3d.engine.physics.entities.properties.collision.IHasCollisionFilter;
import ru.jgems3d.engine.physics.world.basic.IWorldObject;
import ru.jgems3d.engine.physics.world.basic.IWorldTicked;
import ru.jgems3d.engine.physics.world.triggers.IHasCollisionTrigger;
import ru.jgems3d.engine.physics.world.triggers.Zone;

public interface ITriggerZone extends IWorldTicked, IWorldObject, IHasCollisionTrigger, IHasCollisionFilter {
    Zone getZone();
}
