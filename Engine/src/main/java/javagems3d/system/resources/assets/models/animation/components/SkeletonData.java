package javagems3d.system.resources.assets.models.animation.components;

public final class SkeletonData {
    private final float[] weights;
    private final int[] boneIds;

    public SkeletonData(float[] weights, int[] boneIds) {
        this.weights = weights;
        this.boneIds = boneIds;
    }

    public float[] getWeights() {
        return this.weights;
    }

    public int[] getBoneIds() {
        return this.boneIds;
    }
}
