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

package javagems3d.system.resources.assets.models.mesh.data.collision;

import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.models.mesh.IMeshUserData;
import javagems3d.system.resources.assets.models.mesh.MeshDataGroup;
import javagems3d.system.resources.assets.models.mesh.ModelNode;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshUserData {
    private final float[] allPositions;
    private final CompoundMesh compoundMesh;

    public MeshCollisionData(MeshDataGroup meshDataGroup) {
        this.compoundMesh = DynamicsUtils.getCompoundMesh(meshDataGroup);
        this.allPositions = this.pickAllPositions(meshDataGroup);
    }

    private float[] pickAllPositions(MeshDataGroup meshDataGroup) {
        List<Float> floats = new ArrayList<>();
        for (ModelNode modelNode : meshDataGroup.getModelNodeList()) {
            floats.addAll(modelNode.getMesh().getAttributePositions());
        }
        return JGemsHelper.UTILS.convertFloatsArray(floats);
    }

    public CompoundMesh getCompoundMesh() {
        return this.compoundMesh;
    }

    public float[] getAllPositions() {
        return this.allPositions;
    }
}
