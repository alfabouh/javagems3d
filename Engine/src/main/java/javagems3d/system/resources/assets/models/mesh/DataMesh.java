package javagems3d.system.resources.assets.models.mesh;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMesh implements IMesh {
    private int positionsIdx;

    private final Map<Integer, VertexBuffer<Float>> bufferMap;
    private VertexBuffer<Integer> indexes;

    public DataMesh() {
        this.positionsIdx = IMesh.DEFAULT_POS_IDX;
        this.bufferMap = new HashMap<>();
    }

    public void putIndexes(List<Integer> indexes) {
        IntBuffer buffer = MemoryUtil.memAllocInt(indexes.size());
        for (int i : indexes) {
            buffer.put(i);
        }
        buffer.flip();
        this.indexes = new VertexBuffer<>(null, indexes);
    }

    @SuppressWarnings("all")
    public <T> DataMesh putBufferInMeshData(RenderAttributePointer attributePointer, List<T> array) {
        this.getBufferMap().put(attributePointer.getIndex(), new VertexBuffer(attributePointer, array));
        return this;
    }

    public VertexBuffer<Integer> getIndexesBuffer() {
        return this.indexes;
    }

    @SuppressWarnings("all")
    public <T> VertexBuffer<T> getBufferById(int id) {
        return (VertexBuffer<T>) this.getBufferMap().get(id);
    }

    public VertexBuffer<?> getBufferById(RenderAttributePointer id) {
        return this.getBufferMap().get(id.getIndex());
    }

    public Map<Integer, VertexBuffer<Float>> getBufferMap() {
        return this.bufferMap;
    }

    @Override
    public @NotNull List<Integer> getVertexIndexes() {
        return this.getIndexesBuffer().getValues();
    }

    @Override
    public @NotNull List<Float> getVertexPositions() {
        return this.<Float>getBufferById(this.positionsIndex()).getValues();
    }

    @Override
    public void clearMesh() {
        for (VertexBuffer<?> buffer : this.getBufferMap().values()) {
            buffer.getValues().clear();
        }
    }

    public void setPositionsIdx(int idx) {
        this.positionsIdx = idx;
    }

    @Override
    public int positionsIndex() {
        return this.positionsIdx;
    }
}
