package ru.jgems3d.engine.system.resources.assets.models.mesh;

import org.joml.Vector3f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.JGemsHelper;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final List<Integer> indexes;
    private final List<Integer> attributePointers;
    private final List<Float> attributePositions;
    private final List<Float> attributeTextureCoordinates;
    private final List<Float> attributeNormals;
    private final List<Float> attributeTangents;
    private final List<Float> attributeBitangents;
    private boolean baked;
    private int totalVertices;
    private int vao;
    private int indexVbo;
    private int positionVbo;
    private int textureCoordinatesVbo;
    private int normalsVbo;
    private int avaragedNormalsVbo;
    private int tangentsVbo;
    private int bitangentsVbo;

    public Mesh() {
        this.indexes = new ArrayList<>();
        this.attributePositions = new ArrayList<>();
        this.attributeTextureCoordinates = new ArrayList<>();
        this.attributeNormals = new ArrayList<>();
        this.attributeTangents = new ArrayList<>();
        this.attributeBitangents = new ArrayList<>();
        this.attributePointers = new ArrayList<>();
        this.totalVertices = 0;
        this.vao = 0;
        this.avaragedNormalsVbo = 0;
        this.indexVbo = 0;
        this.positionVbo = 0;
        this.textureCoordinatesVbo = 0;
        this.normalsVbo = 0;
        this.tangentsVbo = 0;
        this.bitangentsVbo = 0;
        this.baked = false;
    }

    public void pushPosition(float position) {
        this.getAttributePositions().add(position);
    }

    public void pushTextureCoordinate(float texCoord) {
        this.getAttributeTextureCoordinates().add(texCoord);
    }

    public void pushNormal(float normal) {
        this.getAttributeNormals().add(normal);
    }

    public void pushTangent(float tangent) {
        this.getAttributeTangents().add(tangent);
    }

    public void pushBiTangent(float biTangent) {
        this.getAttributeBiTangents().add(biTangent);
    }

    public void pushIndex(int index) {
        this.getIndexes().add(index);
    }

    public void pushPositions(float[] positions) {
        for (float f : positions) {
            this.getAttributePositions().add(f);
        }
    }

    public void pushTextureCoordinates(float[] texCoordinates) {
        for (float f : texCoordinates) {
            this.getAttributeTextureCoordinates().add(f);
        }
    }

    public void pushNormals(float[] normals) {
        for (float f : normals) {
            this.getAttributeNormals().add(f);
        }
    }

    public void pushTangent(float[] tangents) {
        for (float f : tangents) {
            this.getAttributeTangents().add(f);
        }
    }

    public void pushBiTangent(float[] bitangents) {
        for (float f : bitangents) {
            this.getAttributeBiTangents().add(f);
        }
    }

    public void pushIndexes(int[] indexes) {
        for (int f : indexes) {
            this.getIndexes().add(f);
        }
    }

    public List<Integer> getAttributePointers() {
        return this.attributePointers;
    }

    public boolean isBaked() {
        return this.baked;
    }

    public void bakeMesh() {
        if (this.isBaked()) {
            throw new JGemsException("Tried to bake model, that is already had been baked!");
        }
        int[] index = JGemsHelper.convertIntsArray(this.indexes);

        float[] position = JGemsHelper.convertFloatsArray(this.attributePositions);
        float[] texCoord = JGemsHelper.convertFloatsArray(this.attributeTextureCoordinates);
        float[] normals = JGemsHelper.convertFloatsArray(this.attributeNormals);
        float[] tangent = JGemsHelper.convertFloatsArray(this.attributeTangents);
        float[] bitangent = JGemsHelper.convertFloatsArray(this.attributeBitangents);
        float[] avaragedNormals;

        this.totalVertices = index.length;

        FloatBuffer posBuffer = null;
        FloatBuffer texBuffer = null;
        FloatBuffer normalsBuffer = null;
        FloatBuffer tangentBuffer = null;
        FloatBuffer bitangentBuffer = null;
        FloatBuffer avaragedNormalsBuffer = null;

        IntBuffer inxBuffer = MemoryUtil.memAllocInt(index.length);
        inxBuffer.put(index).flip();

        if (position != null) {
            posBuffer = MemoryUtil.memAllocFloat(position.length);
            posBuffer.put(position).flip();
        }

        if (texCoord != null) {
            texBuffer = MemoryUtil.memAllocFloat(texCoord.length);
            texBuffer.put(texCoord).flip();
        }

        if (normals != null) {
            avaragedNormals = this.avaragedNormals(normals);
            avaragedNormalsBuffer = MemoryUtil.memAllocFloat(avaragedNormals.length);
            avaragedNormalsBuffer.put(avaragedNormals).flip();

            normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            normalsBuffer.put(normals).flip();
        }

        if (tangent != null) {
            tangentBuffer = MemoryUtil.memAllocFloat(tangent.length);
            tangentBuffer.put(tangent).flip();
        }

        if (bitangent != null) {
            bitangentBuffer = MemoryUtil.memAllocFloat(bitangent.length);
            bitangentBuffer.put(bitangent).flip();
        }

        this.vao = GL30.glGenVertexArrays();
        this.indexVbo = GL30.glGenBuffers();

        GL30.glBindVertexArray(this.getVao());
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, this.getIndexVbo());
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, inxBuffer, GL30.GL_STATIC_DRAW);

        if (posBuffer != null) {
            this.positionVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getPositionVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, posBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(0);
        }

        if (texCoord != null) {
            this.textureCoordinatesVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getTextureCoordinatesVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, texBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(1);
        }

        if (avaragedNormalsBuffer != null) {
            this.avaragedNormalsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getAvaragedNormalsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, avaragedNormalsBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(5, 3, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(5);
        }

        if (normals != null) {
            this.normalsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getNormalsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, normalsBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(2, 3, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(2);
        }

        if (tangent != null) {
            this.tangentsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getTangentsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, tangentBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(3, 3, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(3);
        }

        if (bitangent != null) {
            this.bitangentsVbo = GL30.glGenBuffers();
            GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, this.getBitangentsVbo());
            GL30.glBufferData(GL30.GL_ARRAY_BUFFER, bitangentBuffer, GL30.GL_STATIC_DRAW);
            GL30.glVertexAttribPointer(4, 3, GL30.GL_FLOAT, false, 0, 0);
            this.attributePointers.add(4);
        }

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        this.memFree(avaragedNormalsBuffer);
        this.memFree(inxBuffer);
        this.memFree(posBuffer);
        this.memFree(texBuffer);
        this.memFree(normalsBuffer);
        this.memFree(tangentBuffer);
        this.memFree(bitangentBuffer);
        this.baked = true;
    }

    public void clean() {
        this.indexes.clear();
        this.attributePositions.clear();
        this.attributeTextureCoordinates.clear();
        this.attributeNormals.clear();
        this.attributeTangents.clear();
        this.attributeBitangents.clear();
        this.attributePointers.clear();

        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
        GL30.glDeleteBuffers(this.getIndexVbo());
        GL30.glDeleteBuffers(this.getPositionVbo());
        GL30.glDeleteBuffers(this.getAvaragedNormalsVbo());
        GL30.glDeleteBuffers(this.getTextureCoordinatesVbo());
        GL30.glDeleteBuffers(this.getNormalsVbo());
        GL30.glDeleteBuffers(this.getTangentsVbo());
        GL30.glDeleteBuffers(this.getBitangentsVbo());
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(this.getVao());
    }

    @SuppressWarnings("all")
    private float[] avaragedNormals(float[] normals) {
        int vertexCount = this.getAttributePositions().size() / 3;
        float[] newArray = new float[vertexCount * 3];
        List<Vector3f> averagedNormals = new ArrayList<>(vertexCount);

        for (int i = 0; i < vertexCount; i++) {
            float posX1 = this.getAttributePositions().get(i * 3);
            float posY1 = this.getAttributePositions().get(i * 3 + 1);
            float posZ1 = this.getAttributePositions().get(i * 3 + 2);
            Vector3f vector3f = new Vector3f(0.0f);
            int normalCount = 0;

            for (int j = 0; j < vertexCount; j++) {
                float posX2 = this.getAttributePositions().get(j * 3);
                float posY2 = this.getAttributePositions().get(j * 3 + 1);
                float posZ2 = this.getAttributePositions().get(j * 3 + 2);
                float epsilon = 0.0001f;
                if (Math.abs(posX1 - posX2) < epsilon && Math.abs(posY1 - posY2) < epsilon && Math.abs(posZ1 - posZ2) < epsilon) {
                    vector3f.add(normals[j * 3], normals[j * 3 + 1], normals[j * 3 + 2]);
                    normalCount++;
                }
            }

            if (normalCount > 0) {
                vector3f.div(normalCount);
                vector3f.normalize();
                averagedNormals.add(vector3f);
            }
        }

        for (int i = 0; i < vertexCount; i++) {
            Vector3f vector3f = averagedNormals.get(i);
            newArray[i * 3] = vector3f.x;
            newArray[i * 3 + 1] = vector3f.y;
            newArray[i * 3 + 2] = vector3f.z;
        }

        return newArray;
    }

    private void memFree(Buffer buffer) {
        if (buffer != null) {
            MemoryUtil.memFree(buffer);
        }
    }

    public int getVao() {
        return this.vao;
    }

    public int getIndexVbo() {
        return this.indexVbo;
    }

    public int getPositionVbo() {
        return this.positionVbo;
    }

    public int getTextureCoordinatesVbo() {
        return this.textureCoordinatesVbo;
    }

    public int getAvaragedNormalsVbo() {
        return this.avaragedNormalsVbo;
    }

    public int getNormalsVbo() {
        return this.normalsVbo;
    }

    public int getTangentsVbo() {
        return this.tangentsVbo;
    }

    public int getBitangentsVbo() {
        return this.bitangentsVbo;
    }

    public int getTotalVertices() {
        return this.totalVertices;
    }

    public List<Float> getAttributePositions() {
        return this.attributePositions;
    }

    public List<Integer> getIndexes() {
        return this.indexes;
    }

    public List<Float> getAttributeTextureCoordinates() {
        return this.attributeTextureCoordinates;
    }

    public List<Float> getAttributeBiTangents() {
        return this.attributeBitangents;
    }

    public List<Float> getAttributeTangents() {
        return this.attributeTangents;
    }

    public List<Float> getAttributeNormals() {
        return this.attributeNormals;
    }
}