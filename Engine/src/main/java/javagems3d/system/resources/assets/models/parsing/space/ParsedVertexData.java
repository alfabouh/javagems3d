package javagems3d.system.resources.assets.models.parsing.space;

import java.util.ArrayList;
import java.util.List;

public final class ParsedVertexData {
    private final List<Integer> indexes;
    private final List<Float> vertexes;
    private final List<Float> uv;
    private final List<Float> normals;
    private final List<Float> tangents;
    private final List<Float> biTangents;
    private final List<Integer> joints;
    private final List<Float> weights;
    private final int totalVertexes;
    private final int materialId;

    public ParsedVertexData(List<Integer> indexes, List<Float> vertexes, List<Float> uv, List<Float> normals, List<Float> tangents, List<Float> biTangents, List<Integer> joints, List<Float> weights, int materialId) {
        this.materialId = materialId;
        this.indexes = indexes;
        this.vertexes = vertexes;
        this.uv = uv;
        this.normals = normals;
        this.tangents = tangents;
        this.biTangents = biTangents;
        this.joints = joints;
        this.weights = weights;

        this.totalVertexes = vertexes.size() / 3;
    }

    public int getMaterialId() {
        return this.materialId;
    }

    public int getTotalVertexes() {
        return this.totalVertexes;
    }

    public List<Integer> getIndexes() {
        return this.indexes;
    }

    public List<Float> getVertexes() {
        return this.vertexes;
    }

    public List<Float> getUvPositions() {
        return this.uv;
    }

    public List<Float> getNormals() {
        return this.normals;
    }

    public List<Float> getTangents() {
        return this.tangents;
    }

    public List<Float> getBiTangents() {
        return this.biTangents;
    }

    public List<Integer> getJoints() {
        return this.joints;
    }

    public List<Float> getWeights() {
        return this.weights;
    }
}
