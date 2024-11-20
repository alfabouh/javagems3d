package javagems3d.system.resources.assets.models.mesh;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndirectRenderMesh implements IMesh {
    private int positionsIdx;

    private final Map<RenderAttributePointer, VertexBuffer<Float>> bufferMap;
    private VertexBuffer<Integer> indexes;

    public IndirectRenderMesh() {
        this.positionsIdx = IMesh.DEFAULT_POS_IDX;
        this.bufferMap = new HashMap<>();
    }

    public void putIndexes(List<Integer> indexes) {
        IntBuffer buffer = MemoryUtil.memAllocInt(indexes.size());
        for (int i : indexes) {
            buffer.put(i);
        }
        buffer.flip();
        this.indexes = new VertexBuffer<>(indexes);
    }

    @SuppressWarnings("all")
    public <T> IndirectRenderMesh putBufferInMeshData(RenderAttributePointer attributePointer, List<T> array) {
        this.getBufferMap().put(attributePointer, new VertexBuffer(array));
        return this;
    }

    public VertexBuffer<Integer> getIndexes() {
        return this.indexes;
    }

    @SuppressWarnings("all")
    public VertexBuffer getBufferById(int id) {
        return this.getBufferMap().get(id);
    }

    public VertexBuffer<?> getBufferById(RenderAttributePointer id) {
        return this.getBufferMap().get(id);
    }

    public Map<RenderAttributePointer, VertexBuffer<Float>> getBufferMap() {
        return this.bufferMap;
    }

    @Override
    public void cleanMesh() {
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
