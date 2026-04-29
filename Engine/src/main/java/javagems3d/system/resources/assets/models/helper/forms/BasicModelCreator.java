package javagems3d.system.resources.assets.models.helper.forms;

import javagems3d.system.resources.assets.models.Model;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import javagems3d.system.resources.assets.models.pose.IPose;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.service.args.ArbitraryArguments;
import org.jetbrains.annotations.Nullable;

public interface BasicModelCreator<T extends Model<?, ?>> {
    T generateModel(@Nullable ArbitraryArguments arguments);
    RenderMesh generateMesh(@Nullable ArbitraryArguments arguments);
}
