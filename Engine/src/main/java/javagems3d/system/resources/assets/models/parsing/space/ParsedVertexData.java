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
    private final int totalVertexes;

    public ParsedVertexData(List<Integer> indexes, List<Float> vertexes, List<Float> uv, List<Float> normals, List<Float> tangents) {
        this.indexes = indexes;
        this.vertexes = vertexes;
        this.uv = uv;
        this.normals = normals;
        this.tangents = tangents;
        this.biTangents = this.calcBiTangents(normals, tangents);

        this.totalVertexes = vertexes.size() / 3;
    }

    private List<Float> calcBiTangents(List<Float> normals, List<Float> tangents) {
        List<Float> biTangents = new ArrayList<>();
        for (int i = 0; i < normals.size(); i += 3) {
            float nx = normals.get(i);
            float ny = normals.get(i + 1);
            float nz = normals.get(i + 2);

            float tx = tangents.get(i);
            float ty = tangents.get(i + 1);
            float tz = tangents.get(i + 2);

            float bx = ny * tz - nz * ty;
            float by = nz * tx - nx * tz;
            float bz = nx * ty - ny * tx;

            biTangents.add(bx);
            biTangents.add(by);
            biTangents.add(bz);
        }
        return biTangents;
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
}
