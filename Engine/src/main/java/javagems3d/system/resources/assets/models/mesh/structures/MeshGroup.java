package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.RenderMesh;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MeshGroup extends MeshStructure<MeshGroup.MeshGroupNode> {
    public MeshGroup() {
        super();
    }

    public MeshGroup(List<MeshGroupNode> nodes) {
        super(nodes);
    }

    public MeshGroup(MeshGroupNode... t) {
        super(t);
    }

    @Override
    public MeshDataType getMeshDataType() {
        return MeshDataType.GROUP;
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