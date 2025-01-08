package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeshGroup extends MeshStructure<MeshGroup.MeshGroupNode> {
    public static final String POSTFIX = "_group";
    private MeshBuffer linkedMeshBuffer;

    public MeshGroup() {
        super();
        this.linkedMeshBuffer = null;
    }

    public MeshGroup(List<MeshGroupNode> nodes) {
        super(nodes);
        this.linkedMeshBuffer = null;
    }

    public MeshGroup(MeshGroupNode... t) {
        super(t);
        this.linkedMeshBuffer = null;
    }

    public boolean clearMeshBuffer() {
        if (this.canBeUsedInIndirectRendering()) {
            this.getLinkedMeshBuffer().clear();
            return true;
        }
        return false;
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

    public MeshBuffer getLinkedMeshBuffer() {
        return this.linkedMeshBuffer;
    }

    @Override
    public List<MeshGroupNode> getMeshNodes() {
        return super.getMeshNodes();
    }

    public static class MeshGroupNode extends MeshStructure.Node <RenderMesh> {
        private Material material;

        public MeshGroupNode(@NotNull RenderMesh renderMesh) {
            this(renderMesh, null);
        }

        public MeshGroupNode(@NotNull RenderMesh renderMesh, @Nullable Material material) {
            super(renderMesh);
            this.material = material == null ? Material.createDefault() : material;
        }

        public MeshGroupNode setMaterial(Material material) {
            this.material = material;
            return this;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}