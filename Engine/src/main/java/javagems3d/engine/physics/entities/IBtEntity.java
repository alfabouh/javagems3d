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

package javagems3d.engine.physics.entities;

import javagems3d.engine.physics.entities.properties.collision.IHasCollisionFilter;
import javagems3d.engine.physics.entities.properties.state.IHasEntityState;
import javagems3d.engine.physics.world.triggers.IHasCollisionTrigger;

public interface IBtEntity extends IHasEntityState, IHasCollisionTrigger, IHasCollisionFilter {
}
