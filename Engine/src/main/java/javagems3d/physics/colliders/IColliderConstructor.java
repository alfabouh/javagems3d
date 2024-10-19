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

package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.CollisionShape;

@FunctionalInterface
public interface IColliderConstructor {
    CollisionShape createCollisionShape();

    static IColliderConstructor get(CollisionShape collisionShape) {
        return () -> collisionShape;
    }
}