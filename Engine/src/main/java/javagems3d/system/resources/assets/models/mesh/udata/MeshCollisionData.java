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

package javagems3d.system.resources.assets.models.mesh.udata;

import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import javagems3d.JGemsHelper;
import javagems3d.physics.world.thread.dynamics.DynamicsUtils;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshUserData {
    private final float[] allPositions;
    private final CompoundMesh compoundMesh;

    public MeshCollisionData(MeshStructure<?> meshStructure) {
        this.compoundMesh = DynamicsUtils.getCompoundMesh(meshStructure);
        this.allPositions = this.pickAllPositions(meshStructure);
    }

    private float[] pickAllPositions(MeshStructure<?> meshStructure) {
        List<Float> floats = new ArrayList<>();
        for (MeshStructure.Node<?> meshNode : meshStructure.getMeshNodes()) {
            floats.addAll(meshNode.getMesh().getVertexPositions());
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
