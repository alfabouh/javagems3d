package javagems3d.system.resources.assets.models.animation.components;

public final class VertexWeight {
    private final int boneId;
    private final int vertexId;
    private final float weight;

    public VertexWeight(int boneId, int vertexId, float weight) {
        this.boneId = boneId;
        this.vertexId = vertexId;
        this.weight = weight;
    }

    public int getBoneId() {
        return this.boneId;
    }

    public int getVertexId() {
        return this.vertexId;
    }

    public float getWeight() {
        return this.weight;
    }
}
