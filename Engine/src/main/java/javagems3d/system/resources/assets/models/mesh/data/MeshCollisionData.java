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

package javagems3d.system.resources.assets.models.mesh.data;

import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.models.mesh.MeshGroup;
import javagems3d.system.resources.assets.models.mesh.attributes.pointer.DefaultPointers;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshUserData {
    private final float[] allPositions;
    private final CompoundMesh compoundMesh;

    public MeshCollisionData(MeshGroup meshGroup) {
        this(meshGroup, DefaultPointers.POSITIONS.getIndex());
    }

    public MeshCollisionData(MeshGroup meshGroup, int positionsAttributeIndex) {
        this.compoundMesh = DynamicsUtils.getCompoundMesh(meshGroup);
        this.allPositions = this.pickAllPositions(meshGroup, positionsAttributeIndex);
    }

    private float[] pickAllPositions(MeshGroup meshGroup, int positionsAttributeIndex) {
        List<Float> floats = new ArrayList<>();
        for (MeshGroup.Node meshNode : meshGroup.getModelNodeList()) {
            floats.addAll(meshNode.getMesh().tryGetValuesFromAttributeByIndex(positionsAttributeIndex));
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
