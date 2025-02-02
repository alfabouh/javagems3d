package javagems3d.system.resources.assets.models.mesh.structures.flat;

import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshGui extends MeshStructure2D {
    public MeshGui(@Nullable List<MeshNode2D> meshNodes) {
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    public MeshGui(RenderMesh renderMesh) {
        this(new MeshNode2D(renderMesh));
    }

    public MeshGui(MeshNode2D... t) {
        this(Arrays.asList(t));
    }

    public MeshGui() {
        this((List<MeshNode2D>) null);
    }
}
