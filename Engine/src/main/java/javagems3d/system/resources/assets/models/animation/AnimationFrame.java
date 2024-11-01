package javagems3d.system.resources.assets.models.animation;

import org.joml.Matrix4f;

public final class AnimationFrame {
    private final Matrix4f[] boneMatrices;

    public AnimationFrame(Matrix4f[] boneMatrices) {
        this.boneMatrices = boneMatrices;
    }

    public Matrix4f[] getBoneMatrices() {
        return this.boneMatrices;
    }
}
