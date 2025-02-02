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
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshUserData {
    private final float[] allPositions;
    private final CompoundMesh compoundMesh;

    public MeshCollisionData(MeshStructure3D<?> meshStructure) {
        this.compoundMesh = DynamicsUtils.getCompoundMesh(meshStructure);
        this.allPositions = this.pickAllPositions(meshStructure);
    }

    private float[] pickAllPositions(MeshStructure3D<?> meshStructure) {
        List<Float> floats = new ArrayList<>();
        for (MeshNode3D<?> meshNode3D : meshStructure.getAllNodes()) {
            floats.addAll(meshNode3D.getMeshData().getVertexPositions());
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
