/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.system.resources.assets.models.mesh.structures;

import javagems3d.system.resources.assets.materials.Material;
import javagems3d.system.resources.assets.models.mesh.DataMesh;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MeshBuffer extends MeshStructure<MeshBuffer.MeshBufferNode> {
    public static final String POSTFIX = "_buffer";
    private final List<PassData> passData;

    public MeshBuffer() {
        this.passData = new ArrayList<>();
    }

    public MeshBuffer(List<MeshBufferNode> nodes) {
        super(nodes);
        this.passData = new ArrayList<>();
    }

    public MeshBuffer(MeshBufferNode... t) {
        super(t);
        this.passData = new ArrayList<>();
    }

    @Override
    public void clear() {
        super.clear();
        this.getPassData().clear();
    }

    @Override
    public boolean canBeUsedInIndirectRendering() {
        return true;
    }

    public List<PassData> getPassData() {
        return this.passData;
    }

    @Override
    public List<MeshBufferNode> getMeshNodes() {
        return super.getMeshNodes();
    }

    public static final class MeshBufferNode extends MeshStructure.Node<DataMesh> {
        private final Material material;

        public MeshBufferNode(@NotNull DataMesh meshData, Material material) {
            super(meshData);
            this.material = material;
        }

        public void clearMesh() {
            this.getMesh().clearMesh();
        }

        public Material getMaterial() {
            return this.material;
        }
    }

    public static final class PassData {
        private final int sizeInBytes;
        private int materialId;
        private final int offset;
        private final int vertices;
        private final int firstIndexOffset;

        public PassData(int firstIndexOffset, int sizeInBytes, int materialId, int offset, int vertices) {
            this.firstIndexOffset = firstIndexOffset;
            this.sizeInBytes = sizeInBytes;
            this.materialId = materialId;
            this.offset = offset;
            this.vertices = vertices;
        }

        public int getFirstIndexOffset() {
            return this.firstIndexOffset;
        }

        public void setMaterialId(int materialId) {
            this.materialId = materialId;
        }

        public int getSizeInBytes() {
            return this.sizeInBytes;
        }

        public int getMaterialId() {
            return this.materialId;
        }

        public int getOffset() {
            return this.offset;
        }

        public int getVertices() {
            return this.vertices;
        }
    }
}
