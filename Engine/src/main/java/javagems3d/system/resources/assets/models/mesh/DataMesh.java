package javagems3d.system.resources.assets.models.mesh;
import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.vertex.buffers.VertexBuffer;
import javagems3d.system.resources.assets.models.mesh.vertex.pointers.RenderAttributePointer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMesh implements IMesh {
    private int positionsIdx;

    private final Map<Integer, VertexBuffer<Float>> bufferMap;
    private VertexBuffer<Integer> indexes;
    private SkeletonData skeletonData;

    public DataMesh() {
        this.positionsIdx = IMesh.DEFAULT_POS_IDX;
        this.bufferMap = new HashMap<>();
        this.skeletonData = null;
    }

    public void putVertexIndexes(List<Integer> indexes) {
        this.indexes = new VertexBuffer<>(null, indexes);
    }

    @SuppressWarnings("all")
    public DataMesh putVertexBufferI(RenderAttributePointer attributePointer, List<Integer> array) {
        return this.putVertexBuffer(attributePointer, new VertexBuffer(attributePointer, array));
    }

    @SuppressWarnings("all")
    public DataMesh putVertexBufferF(RenderAttributePointer attributePointer, List<Float> array) {
        return this.putVertexBuffer(attributePointer, new VertexBuffer(attributePointer, array));
    }

    @SuppressWarnings("all")
    public DataMesh putVertexBuffer(RenderAttributePointer attributePointer, VertexBuffer vertexBuffer) {
        this.getBufferMap().put(attributePointer.getIndex(), vertexBuffer);
        return this;
    }

    public VertexBuffer<Integer> getIndexesBuffer() {
        return this.indexes;
    }

    @SuppressWarnings("all")
    public VertexBuffer<Float> getBufferById(int id) {
        return this.getBufferMap().getOrDefault(id, null);
    }

    public VertexBuffer<Float> getBufferById(RenderAttributePointer id) {
        return this.getBufferMap().get(id.getIndex());
    }

    public Map<Integer, VertexBuffer<Float>> getBufferMap() {
        return this.bufferMap;
    }

    @Override
    public @NotNull List<Integer> getVertexIndexes() {
        return this.getIndexesBuffer().values();
    }

    @Override
    public @NotNull List<Float> getVertexPositions() {
        return this.getBufferById(this.positionsIndex()).values();
    }

    @Override
    public void clearData() {
        this.setSkeletonData(null);
        this.getIndexesBuffer().values().clear();
        this.getBufferMap().values().forEach(e -> e.values().clear());
        this.getBufferMap().clear();
    }

    @Override
    public void clearMesh() {
        this.clearData();
    }

    public SkeletonData getSkeletonData() {
        return this.skeletonData;
    }

    public DataMesh setSkeletonData(SkeletonData skeletonData) {
        this.skeletonData = skeletonData;
        return this;
    }

    public void setPositionsIdx(int idx) {
        this.positionsIdx = idx;
    }

    @Override
    public int positionsIndex() {
        return this.positionsIdx;
    }
}
