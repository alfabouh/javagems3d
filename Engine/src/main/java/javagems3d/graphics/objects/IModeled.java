package javagems3d.graphics.objects;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;

import javagems3d.system.resources.assets.models.Model3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.jetbrains.annotations.Nullable;

public interface IModeled extends IAnimated {
    Model3D getModel();
    void updateAnimation();

    default @Nullable CullingAABB pickAABBDataFromMesh() {
        if (!this.hasModel()) {
            return null;
        }
        Pose3D pose3D = this.getModel().getPose();
        if (this.isAnimated()) {
            return this.getModel().getMeshStructure().getMeshAABBDataForAnimation(this.getAnimationData().getCurrentAnimation()).getNormalizedAABB(pose3D);
        }
        MeshBoundingBoxData meshBoundingBoxData = this.getModel().getMeshStructure().getMeshAABBData();
        if (meshBoundingBoxData == null) {
            return null;
        }
        return meshBoundingBoxData.getNormalizedAABB(pose3D);
    }

    default boolean hasModel() {
        return this.getModel() != null && this.getModel().isValid();
    }
}
