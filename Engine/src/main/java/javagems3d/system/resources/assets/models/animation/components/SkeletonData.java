package javagems3d.system.resources.assets.models.animation.components;

import java.util.List;

public final class SkeletonData {
    private final List<Float> weights;
    private final List<Integer> boneIds;

    public SkeletonData(List<Float> weights, List<Integer> boneIds) {
        this.weights = weights;
        this.boneIds = boneIds;
    }

    public List<Float> getWeights() {
        return this.weights;
    }

    public List<Integer> getBoneIds() {
        return this.boneIds;
    }
}
