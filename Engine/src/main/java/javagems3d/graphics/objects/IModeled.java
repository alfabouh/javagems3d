package javagems3d.graphics.objects;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.mesh.udata.IMeshUserData;
import javagems3d.system.resources.assets.models.mesh.udata.MeshAABBData;
import org.jetbrains.annotations.Nullable;

public interface IModeled extends IAnimated {
    Model<Format3D> getModel();
    void updateAnimation();

    default @Nullable CullingAABB pickAABBDataFromMesh() {
        if (!this.hasModel()) {
            return null;
        }
        if (!this.getModel().getMeshStructure().hasMeshUserData(MeshStructure.MESH_AABB_UD)) {
            return null;
        }
        return this.getModel().getMeshStructure().getMeshUserData(MeshStructure.MESH_AABB_UD, MeshAABBData.class).getCullingAABB(this.getModel().getFormat());
    }

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
}
