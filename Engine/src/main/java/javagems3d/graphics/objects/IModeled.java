package javagems3d.graphics.objects;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import org.jetbrains.annotations.Nullable;

public interface IModeled extends IAnimated {
    Model3D getModel();
    void updateAnimation();

    default @Nullable CullingAABB pickAABBDataFromMesh() {
        if (!this.hasModel()) {
            return null;
        }
        if (!this.getModel().getMeshStructure().hasMeshUserData(MeshStructure3D.MESH_AABB_UD)) {
            return null;
        }
        return this.getModel().getMeshStructure().getMeshUserData(MeshStructure3D.MESH_AABB_UD, MeshAABBData.class).getCullingAABB(this.getModel().getPose());
    }

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
}
