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

package javagems3d.physics.entities.properties.collision;

public interface IHasCollisionFilter {
    int getCollisionGroup();

    void setCollisionGroup(CollisionType... collisionTypes);

    int getCollisionFilter();

    void setCollisionFilter(CollisionType... collisionTypes);
}
