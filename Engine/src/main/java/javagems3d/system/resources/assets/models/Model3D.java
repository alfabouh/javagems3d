package javagems3d.system.resources.assets.models;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Model3D extends Model<Pose3D, MeshStructure3D<? extends IMesh>> {
    public Model3D(@NotNull Pose3D pose, @Nullable MeshStructure3D<? extends IMesh> meshStructure) {
        super(pose, meshStructure);
    }

    public Model3D(@NotNull Model<Pose3D, MeshStructure3D<? extends IMesh>> model) {
        super(model);
    }

    public Model3D(@NotNull Model<Pose3D, MeshStructure3D<? extends IMesh>> model, @NotNull Pose3D pose) {
        super(model, pose);
    }

    @Nullable
    public MeshBuffer getMeshBufferForIndirectRendering() {
        if (this.getMeshStructure() instanceof MeshBuffer) {
            return (MeshBuffer) this.getMeshStructure();
        }
        return ((MeshGroup) this.getMeshStructure()).getLinkedMeshBuffer();
    }

    @SuppressWarnings("all")
    public <R extends MeshStructure3D<? extends IMesh>> R getMeshStructureCast() {
        try {
            return (R) this.getMeshStructure();
        } catch (ClassCastException e) {
            throw new JGemsRuntimeException("Unable to cast!\n" + e.getMessage());
        }
    }
}
