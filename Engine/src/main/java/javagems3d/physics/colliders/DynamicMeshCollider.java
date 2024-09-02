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
import com.jme3.bullet.collision.shapes.HullCollisionShape;
import javagems3d.physics.world.thread.dynamics.DynamicsSystem;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.system.resources.assets.models.mesh.data.collision.MeshCollisionData;
import javagems3d.system.service.exceptions.JGemsNullException;

public class DynamicMeshCollider implements IColliderConstructor {
    private final MeshDataGroup meshDataGroup;

    public DynamicMeshCollider(MeshDataGroup meshDataGroup) {
        this.meshDataGroup = meshDataGroup;
    }

    @Override
    public CollisionShape createGeom(DynamicsSystem dynamicsSystem) {
        MeshCollisionData meshCollisionData = this.meshDataGroup.getMeshUserData(MeshCollisionData.class);
        if (meshCollisionData == null) {
            throw new JGemsNullException("Couldn't get mesh collision collections! " + this.meshDataGroup);
        }
        HullCollisionShape hullCollisionShape = new HullCollisionShape(meshCollisionData.getAllPositions());
        hullCollisionShape.setMargin(0.01f);
        return hullCollisionShape;
    }
}