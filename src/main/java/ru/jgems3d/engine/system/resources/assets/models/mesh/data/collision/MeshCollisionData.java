package ru.jgems3d.engine.system.resources.assets.models.mesh.data.collision;

import com.jme3.bullet.collision.shapes.infos.CompoundMesh;
import ru.jgems3d.engine.physics.world.thread.dynamics.DynamicsUtils;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.resources.assets.models.mesh.IMeshDataContainer;
import ru.jgems3d.engine.system.resources.assets.models.mesh.MeshDataGroup;
import ru.jgems3d.engine.system.resources.assets.models.mesh.ModelNode;

import java.util.ArrayList;
import java.util.List;

public class MeshCollisionData implements IMeshDataContainer {
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
        return JGemsHelper.convertFloatsArray(floats);
    }

    public CompoundMesh getCompoundMesh() {
        return this.compoundMesh;
    }

    public float[] getAllPositions() {
        return this.allPositions;
    }
}
