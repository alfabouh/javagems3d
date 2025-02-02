package javagems3d.system.resources.assets.models;

import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshBuffer;
import javagems3d.system.resources.assets.models.mesh.structures.solid.MeshGroup;
import javagems3d.system.resources.assets.models.pose.Pose2D;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Model2D extends Model<Pose2D, MeshStructure2D> {
    public Model2D(@NotNull Pose2D pose, @NotNull MeshStructure2D meshStructure) {
        super(pose, meshStructure);
    }

    public Model2D(@NotNull Model<Pose2D, MeshStructure2D> model) {
        super(model);
    }

    public Model2D(@NotNull Model<Pose2D, MeshStructure2D> model, @NotNull Pose2D pose) {
        super(model, pose);
    }
}
