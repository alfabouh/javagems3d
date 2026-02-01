package javagems3d.system.resources.assets.models.mesh.structures.solid;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class MeshGroup extends MeshStructure3D<RenderMesh> {
    public static final String POSTFIX = "_group";
    private MeshBuffer linkedMeshBuffer;

    public MeshGroup(@Nullable List<MeshNode3D<RenderMesh>> meshNodes) {
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    @SafeVarargs
    public MeshGroup(MeshNode3D<RenderMesh>... t) {
        this(Arrays.asList(t));
    }

    public MeshGroup(RenderMesh renderMesh) {
        this(new MeshNode3D<>(renderMesh, new Material()));
    }

    public MeshGroup() {
        this((List<MeshNode3D<RenderMesh>>) null);
    }

    @SuppressWarnings("all")
    public MeshGroup setLinkedMeshBuffer(MeshBuffer linkedMeshBuffer) {
        this.linkedMeshBuffer = linkedMeshBuffer;
        return this;
    }

    @Override
    public boolean canBeUsedInIndirectRendering() {
        return this.getLinkedMeshBuffer() != null;
    }

    public void clearLinkedMeshBufferNodesData() {
        if (this.canBeUsedInIndirectRendering()) {
            this.getLinkedMeshBuffer().clearNodesData();
        }
    }

    @Override
    public void clear() {
        super.clear();
        this.clearLinkedMeshBufferNodesData();
    }

    public MeshBuffer getLinkedMeshBuffer() {
        return this.linkedMeshBuffer;
    }
}