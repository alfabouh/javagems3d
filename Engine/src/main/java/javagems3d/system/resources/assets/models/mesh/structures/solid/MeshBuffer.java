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

package javagems3d.system.resources.assets.models.mesh.structures.solid;

import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeshBuffer extends MeshStructure3D<DataMesh> {
    public static final String POSTFIX = "_buffer";
    private final List<PassData> passData;
    private final List<PassData> passDataTransparent;

    public MeshBuffer(@Nullable List<MeshNode3D<DataMesh>> meshNodes) {
        this.passData = new ArrayList<>();
        this.passDataTransparent = new ArrayList<>();
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    @SafeVarargs
    public MeshBuffer(MeshNode3D<DataMesh>... t) {
        this(Arrays.asList(t));
    }

    public MeshBuffer() {
        this((List<MeshNode3D<DataMesh>>) null);
    }

    @Override
    public void clear() {
        super.clear();
        this.getSolidPassData().clear();
        this.getTransparentPassData().clear();
    }

    @Override
    public boolean hasTransparency() {
        return !this.getTransparentPassData().isEmpty();
    }

    @Override
    public boolean canBeUsedInIndirectRendering() {
        return true;
    }

    public List<PassData> getTransparentPassData() {
        return this.passDataTransparent;
    }

    public List<PassData> getSolidPassData() {
        return this.passData;
    }

    public List<PassData> getAllPassData() {
        int capacity = this.getSolidPassData().size() + this.getTransparentPassData().size();
        List<PassData> passData1 = new ArrayList<>(capacity);
        passData1.addAll(this.getSolidPassData());
        passData1.addAll(this.getTransparentPassData());
        return passData1;
    }

    public static final class PassData {
        private final int sizeInBytes;
        private int materialId;
        private final int offset;
        private final int vertices;
        private final int firstIndexOffset;

        public PassData(int firstIndexOffset, int sizeInBytes, int materialId, int offset, int vertexIndexes) {
            this.firstIndexOffset = firstIndexOffset;
            this.sizeInBytes = sizeInBytes;
            this.materialId = materialId;
            this.offset = offset;
            this.vertices = vertexIndexes;
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

        public int numVertexIndexes() {
            return this.vertices;
        }
    }
}
