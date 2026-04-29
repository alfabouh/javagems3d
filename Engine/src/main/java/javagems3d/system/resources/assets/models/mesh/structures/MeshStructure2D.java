package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode2D;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class MeshStructure2D extends MeshStructure<RenderMesh, MeshNode2D> {
    public MeshStructure2D() {
    }

    public int @NotNull [] getLayersToInit() {
        return new int[] {0};
    }

    public void putNodes(List<MeshNode2D> list) {
        for (MeshNode2D m : list) {
            this.putNode(0, m);
        }
    }

    public void putSolidNode(MeshNode2D meshNode3D) {
        this.putNode(0, meshNode3D);
    }

    public List<MeshNode2D> getNodes() {
        return this.getNodes(0);
    }
}
