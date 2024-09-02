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

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Vector3f;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;

public class OBBCollider implements IColliderConstructor {
    private final Vector3f size;

    public OBBCollider(Vector3f size) {
        this.size = size;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        Vector3f vector3f = new Vector3f(this.size);
        return new BoxCollisionShape(DynamicsUtils.convertV3F_JME(vector3f).mult(0.5f));
    }
}
