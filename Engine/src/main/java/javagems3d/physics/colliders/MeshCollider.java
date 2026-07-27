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
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;
import logger.Log;

public class MeshCollider implements IColliderConstructor {
    private final MeshStructure3D<?> meshStructure;
    private final boolean isBodyDynamic;

    public MeshCollider(MeshStructure3D<?> meshStructure, boolean isBodyDynamic) {
        this.meshStructure = meshStructure;
        this.isBodyDynamic = isBodyDynamic;
    }

    public static IColliderConstructor getDynamic(MeshStructure3D<?> meshStructure) {
        return new MeshCollider(meshStructure, true);
    }

    public static IColliderConstructor getStatic(MeshStructure3D<?> meshStructure) {
        return new MeshCollider(meshStructure, false);
    }

    public static IColliderConstructor get(MeshStructure3D<?> meshStructure, boolean isBodyDynamic) {
        return new MeshCollider(meshStructure, isBodyDynamic);
    }

    @Override
    public CollisionShape execute() {
        MeshCollisionData meshCollisionData = this.meshStructure.getMeshCollisionData();
        if (meshCollisionData == null) {
            Log.get().error("Couldn't get mesh collision collections! " + this.meshStructure);
            return new BoxCollisionShape(1.0f);
        }
        CollisionShape collisionShape;
        if (this.isBodyDynamic) {
            collisionShape = meshCollisionData.getDynamicCollision();
        } else {
            collisionShape = meshCollisionData.getStaticCollision();
            collisionShape.setMargin(this.margin());
        }
        return collisionShape;
    }

    protected float margin() {
        return 0.005f;
    }
}
