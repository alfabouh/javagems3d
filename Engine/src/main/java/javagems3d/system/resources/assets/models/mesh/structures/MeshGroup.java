package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.material.Material;
import javagems3d.system.resources.assets.models.mesh.DirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.DefaultAttributePointers;

import java.util.ArrayList;
import java.util.List;

public class MeshGroup extends MeshStructure {
    private final List<MeshNode> meshMeshNodeList;

    public MeshGroup() {
        this.meshMeshNodeList = new ArrayList<>();
    }

    public MeshGroup(MeshNode meshNode) {
        this();
        this.putMeshNode(meshNode);
    }

    public MeshGroup(DirectRenderMesh directRenderMesh) {
        this(new MeshNode(directRenderMesh));
    }

    public void createRenderAABB() {
        this.createRenderAABB(DefaultAttributePointers.ATTR_POSITIONS.getIndex());
    }

    public void createRenderAABB(int positionAttributeIndex) {
        //JGemsHelper.UTILS.createMeshRenderAABBData(this, positionAttributeIndex);
    }

    public void putMeshNode(MeshNode meshNode) {
        this.meshMeshNodeList.add(meshNode);
    }

    public void clean() {
        this.getModelNodeList().forEach(MeshNode::cleanMesh);
        this.getModelNodeList().clear();
    }

    @Override
    public MeshRenderTarget getMeshTargetType() {
        return MeshRenderTarget.DIRECT;
    }

    public List<MeshNode> getModelNodeList() {
        return this.meshMeshNodeList;
    }

    public static class MeshNode {
        private final DirectRenderMesh directRenderMesh;
        private final Material material;

        public MeshNode(DirectRenderMesh directRenderMesh, Material material) {
            this.directRenderMesh = directRenderMesh;
            this.material = material;
        }

        public MeshNode(DirectRenderMesh directRenderMesh) {
            this.directRenderMesh = directRenderMesh;
            this.material = null;
        }

        public void cleanMesh() {
            this.getMesh().cleanMesh();
        }

        public DirectRenderMesh getMesh() {
            return this.directRenderMesh;
        }

        public Material getMaterial() {
            return this.material;
        }
    }
}