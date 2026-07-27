/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.physics.colliders;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import org.joml.Vector3f;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;

public class OrientedBoxCollider implements IColliderConstructor {
    private final Vector3f size;

    public OrientedBoxCollider(Vector3f size) {
        this.size = size;
    }

    @Override
    public CollisionShape execute() {
        Vector3f vector3f = new Vector3f(this.size);
        return new BoxCollisionShape(DynamicsUtils.convertV3F_JME(vector3f).mult(0.5f));
    }
}
