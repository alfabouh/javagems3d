package javagems3d.system.resources.assets.models.parsing.animation;

import java.util.List;

public class SkeletonAttributes {
    private final List<Float> weights;
    private final List<Integer> boneIds;

    public SkeletonAttributes(List<Float> weights, List<Integer> boneIds) {
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