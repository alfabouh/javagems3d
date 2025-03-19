package javagems3d.system.resources.assets.models.mesh;

import javagems3d.system.resources.assets.models.animation.components.SkeletonData;
import javagems3d.system.resources.assets.models.mesh.vertex.attributes.VertexAttribute;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.*;

public class RenderMesh implements IMesh, AutoCloseable {
    private int positionsIdx;

    private int vao;
    private int totalVertices;

    private int vertexIndexesIBO;
    private final List<Integer> vertexIndexes;

    private final Map<Integer, Integer> vboMap;
    private final Map<Integer, VertexAttribute<?>> vertexAttributesMap;

    private boolean baked;
    private SkeletonData skeletonData;

    public RenderMesh() {
        this.positionsIdx = IMesh.DEFAULT_POS_IDX;
        this.vertexIndexes = new ArrayList<>();

        this.skeletonData = null;
        this.baked = false;
        this.vertexAttributesMap = new HashMap<>();
        this.vboMap = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> tryGetValuesFromAttributeByIndex(int index) {
        try {
            return (List<T>) this.getVertexAttributeByIndex(index).getValues();
        } catch (ClassCastException e) {
            return null;
        }
    }

    public void putVertexIndexes(List<Integer> indexes) {
        this.getVertexIndexes().addAll(indexes);
    }

    public void putVertexIndex(int index) {
        this.getVertexIndexes().add(index);
    }

    public void putVertexAttribute(VertexAttribute<?> vertexAttribute) {
        this.vertexAttributesMap.put(vertexAttribute.getIndex(), vertexAttribute);
    }

    @SuppressWarnings("all")
    public <T> VertexAttribute<T> getVertexAttributeByIndex(int index) {
        return (VertexAttribute<T>) this.vertexAttributesMap.get(index);
    }

    public int getVBOByAttributeIndex(int index) {
        return this.vboMap.get(index);
    }

    public int getVBOByVertexAttribute(VertexAttribute<?> vertexAttribute) {
        return this.getVBOByAttributeIndex(vertexAttribute.getIndex());
    }

    public void disableMeshAttributes(int... a) {
        for (int vertexAttribute : a) {
            GL46.glDisableVertexAttribArray(vertexAttribute);
        }
    }

    public void enableMeshAttributes(int... a) {
        for (int vertexAttribute : a) {
            GL46.glEnableVertexAttribArray(vertexAttribute);
        }
    }

    public void disableAllMeshAttributes() {
        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            GL46.glDisableVertexAttribArray(vertexAttribute.getIndex());
        }
    }

    public void enableAllMeshAttributes() {
        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            GL46.glEnableVertexAttribArray(vertexAttribute.getIndex());
        }
    }

    public void bakeMesh() {
        if (this.isBaked()) {
            throw new JGemsRuntimeException("Tried to bake model, that is already had been baked");
        }
        this.totalVertices = this.getVertexIndexes().size();
        IntBuffer inxBuffer =  MemoryUtil.memAllocInt(this.totalVertices);
        for (int i : this.getVertexIndexes()) {
            inxBuffer.put(i);
        }
        inxBuffer.flip();

        this.vao = GL46.glGenVertexArrays();
        this.vertexIndexesIBO = GL46.glGenBuffers();

        GL46.glBindVertexArray(this.getVao());
        GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, this.getVertexIndexesIBO());
        GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, inxBuffer, GL46.GL_STATIC_DRAW);
        MemoryUtil.memFree(inxBuffer);

        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            int vbo = GL46.glGenBuffers();
            this.vboMap.put(vertexAttribute.getIndex(), vbo);
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, vbo);
            vertexAttribute.pushGLBuffer();
            GL46.glVertexAttribPointer(vertexAttribute.getIndex(), vertexAttribute.getAttributePointer().getLengthInMemory(), vertexAttribute.attributeType(), vertexAttribute.getAttributePointer().isNormalized(), vertexAttribute.getAttributePointer().getStride(), vertexAttribute.getAttributePointer().getPointer());
        }

        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
        this.baked = true;
    }

    public void clearData() {
        this.setSkeletonData(null);
        this.getVertexIndexes().clear();
        this.vertexAttributesMap.values().forEach(VertexAttribute::clearData);
        this.vertexAttributesMap.clear();
    }

    @Override
    public void clearMesh() {
        this.clearData();
        for (int a : this.vboMap.values()) {
            GL46.glDeleteBuffers(a);
        }
        this.vboMap.clear();
        GL46.glDeleteBuffers(this.getVertexIndexesIBO());
        GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
        GL46.glBindVertexArray(0);
        GL46.glDeleteVertexArrays(this.getVao());
    }

    public SkeletonData getSkeletonData() {
        return this.skeletonData;
    }

    public RenderMesh setSkeletonData(SkeletonData skeletonData) {
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

    public int getVertexIndexesIBO() {
        return this.vertexIndexesIBO;
    }

    public int getTotalVertices() {
        return this.totalVertices;
    }

    public @NotNull List<Integer> getVertexIndexes() {
        return this.vertexIndexes;
    }

    @Override
    public @NotNull List<Float> getVertexPositions() {
        return this.<Float>getVertexAttributeByIndex(this.positionsIndex()).getValues();
    }

    public int getVao() {
        return this.vao;
    }

    public boolean isBaked() {
        return this.baked;
    }

    @Override
    public void close() {
        this.bakeMesh();
    }
}