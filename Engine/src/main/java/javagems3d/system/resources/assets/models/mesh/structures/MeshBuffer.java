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

import javagems3d.system.resources.assets.models.mesh.IndirectRenderMesh;
import javagems3d.system.resources.assets.models.mesh.data.IMeshUserData;

import java.util.ArrayList;
import java.util.List;

public class MeshBuffer extends MeshStructure {
    private final List<MeshNode> meshes;
    private final List<DrawData> drawData;

    public MeshBuffer() {
        this(null);
    }

    public MeshBuffer(List<MeshNode> meshes) {
        this.drawData = new ArrayList<>();
        this.meshes = new ArrayList<>();
        if (meshes != null) {
            this.getMeshes().addAll(meshes);
        }
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getUnSafeMeshUserData(String key) {
        return this.getMeshUserData(key, null);
    }

    @SuppressWarnings("all")
    public <T extends IMeshUserData> T getMeshUserData(String key, Class<T> tClass) {
        if (this.getMeshUserData(key) == null) {
            return null;
        }
        if (tClass == null || this.getMeshUserData(key).getClass().isAssignableFrom(tClass)) {
            return (T) this.getMeshUserData(key);
        }
        return null;
    }

    public void putMeshNode(MeshNode meshNode) {
        this.getMeshes().add(meshNode);
    }

    public List<DrawData> getDrawData() {
        return this.drawData;
    }

    public List<MeshNode> getMeshes() {
        return this.meshes;
    }

    @Override
    public void clean() {
    }

    @Override
    public MeshRenderTarget getMeshTargetType() {
        return MeshRenderTarget.INDIRECT;
    }

    public static final class MeshNode {
        private final IndirectRenderMesh meshData;
        private final int materialId;

        public MeshNode(IndirectRenderMesh meshData, int materialId) {
            this.meshData = meshData;
            this.materialId = materialId;
        }

        public IndirectRenderMesh getMesh() {
            return this.meshData;
        }

        public int getMaterialId() {
            return this.materialId;
        }
    }

    public static final class DrawData {
        private final int sizeInBytes;
        private final int materialId;
        private final int offset;
        private final int vertices;

        public DrawData(int sizeInBytes, int materialId, int offset, int vertices) {
            this.sizeInBytes = sizeInBytes;
            this.materialId = materialId;
            this.offset = offset;
            this.vertices = vertices;
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
