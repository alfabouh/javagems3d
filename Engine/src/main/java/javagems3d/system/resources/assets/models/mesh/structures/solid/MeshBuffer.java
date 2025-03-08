package javagems3d.system.resources.assets.models.mesh.structures.solid;

import javagems3d.system.resources.assets.models.mesh.DataMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure3D;
import javagems3d.system.resources.assets.models.mesh.structures.nodes.MeshNode3D;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MeshBuffer extends MeshStructure3D<DataMesh> {
    public static final String POSTFIX = "_buffer";
    private final Map<Integer, List<PassData>> meshPassData;

    public MeshBuffer(@Nullable List<MeshNode3D<DataMesh>> meshNodes) {
        this.meshPassData = new HashMap<>();
        this.initPassDataLayers();
        if (meshNodes != null) {
            this.putNodes(meshNodes);
        }
    }

    protected void initPassDataLayers() {
        for (int i : this.getLayersToInit()) {
            this.meshPassData.put(i, new ArrayList<>());
        }
        if (this.meshPassData.isEmpty()) {
            this.meshPassData.put(0, new ArrayList<>());
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
        this.meshPassData.clear();
    }

    @Override
    public boolean hasTransparency() {
        return !this.getTransparentPassData().isEmpty();
    }

    @Override
    public boolean canBeUsedInIndirectRendering() {
        return true;
    }

    public void putSolidPassData(PassData passData) {
        this.getSolidPassData().add(passData);
    }

    public void putBlendedTransparentPassData(PassData passData) {
        this.getTransparentPassData().add(passData);
    }

    public List<PassData> getSolidPassData() {
        return this.getPassData(MeshStructure3D.SOLID_LAYER);
    }

    public List<PassData> getTransparentPassData() {
        return this.getPassData(MeshStructure3D.TRANSPARENCY_LAYER);
    }

    public List<PassData> getPassData(int layer) {
        return this.meshPassData.get(layer);
    }

    public Map<Integer, List<PassData>> getMeshPassDataMap() {
        return this.meshPassData;
    }

    public List<PassData> getAllPassData() {
        int capacity = 0;
        for (List<PassData> layer : this.meshPassData.values()) {
            capacity += layer.size();
        }
        List<PassData> list = new ArrayList<>(capacity);
        for (List<PassData> layer : this.meshPassData.values()) {
            list.addAll(layer);
        }
        return list;
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
