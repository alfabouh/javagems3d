package javagems3d.system.resources.assets.models.mesh;

import javagems3d.JGemsHelper;
import javagems3d.system.resources.assets.models.mesh.attributes.VertexAttribute;
import javagems3d.system.service.exceptions.JGemsRuntimeException;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;
import java.util.*;

public class Mesh implements IMesh {
    private int vao;
    private int totalVertices;

    private int vertexIndexesIBO;
    private final List<Integer> vertexIndexes;

    private final Map<Integer, Integer> vboMap;
    private final Map<Integer, VertexAttribute<?>> vertexAttributesMap;

    private boolean baked;

    public Mesh() {
        this.vertexIndexes = new ArrayList<>();

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

    public void putVertexIndexes(int[] indexes) {
        for (int o : indexes) {
            this.getVertexIndexes().add(o);
        }
    }

    public void putVertexIndex(int index) {
        this.getVertexIndexes().add(index);
    }

    public void addVertexAttributeInMesh(VertexAttribute<?> vertexAttribute) {
        this.vertexAttributesMap.put(vertexAttribute.getIndex(), vertexAttribute);
    }

    public VertexAttribute<?> getVertexAttributeByIndex(int index) {
        return this.vertexAttributesMap.get(index);
    }

    public int getVBOByAttributeIndex(int index) {
        return this.vboMap.get(index);
    }

    public int getVBOByVertexAttribute(VertexAttribute<?> vertexAttribute) {
        return this.getVBOByAttributeIndex(vertexAttribute.getIndex());
    }


    public void disableMeshAttributes(int... a) {
        for (int vertexAttribute : a) {
            GL30.glDisableVertexAttribArray(vertexAttribute);
        }
    }

    public void enableMeshAttributes(int... a) {
        for (int vertexAttribute : a) {
            GL30.glEnableVertexAttribArray(vertexAttribute);
        }
    }

    public void disableAllMeshAttributes() {
        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            GL30.glDisableVertexAttribArray(vertexAttribute.getIndex());
        }
    }

    public void enableAllMeshAttributes() {
        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            GL30.glEnableVertexAttribArray(vertexAttribute.getIndex());
        }
    }

    @Override
    public void bakeMesh() {
        if (this.isBaked()) {
            throw new JGemsRuntimeException("Tried to bake model, that is already had been baked!");
        }

        int[] index = JGemsHelper.UTILS.convertIntsArray(this.getVertexIndexes());
        this.totalVertices = index.length;
        IntBuffer inxBuffer =  MemoryUtil.memAllocInt(index.length);
        inxBuffer.put(index).flip();

        this.vao = GL30.glGenVertexArrays();
        this.vertexIndexesIBO = GL30.glGenBuffers();

        GL30.glBindVertexArray(this.getVao());
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.getVertexIndexesIBO());
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, inxBuffer, GL30.GL_STATIC_DRAW);

        for (VertexAttribute<?> vertexAttribute : this.vertexAttributesMap.values()) {
            vertexAttribute.bake();
            int vbo = GL30.glGenBuffers();
            this.vboMap.put(vertexAttribute.getIndex(), vbo);
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
            vertexAttribute.pushGLBuffer();
            GL30.glVertexAttribPointer(vertexAttribute.getIndex(), vertexAttribute.getAttributePointer().getSize(), vertexAttribute.attributeType(), vertexAttribute.getAttributePointer().isNormalized(), vertexAttribute.getAttributePointer().getStride(), vertexAttribute.getAttributePointer().getPointer());
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        this.baked = true;
    }

    @Override
    public void cleanMesh() {
        this.vboMap.clear();
        this.vertexAttributesMap.clear();
        this.getVertexIndexes().clear();

        for (VertexAttribute<?> v : this.vertexAttributesMap.values()) {
            v.clearData();
        }

        GL30.glDeleteBuffers(this.getVertexIndexesIBO());
        for (int a : this.vboMap.values()) {
            GL30.glDeleteBuffers(a);
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.getVao());
    }

    public int getVertexIndexesIBO() {
        return this.vertexIndexesIBO;
    }

    public int getTotalVertices() {
        return this.totalVertices;
    }

    public List<Integer> getVertexIndexes() {
        return this.vertexIndexes;
    }

    public int getVao() {
        return this.vao;
    }

    public boolean isBaked() {
        return this.baked;
    }

}