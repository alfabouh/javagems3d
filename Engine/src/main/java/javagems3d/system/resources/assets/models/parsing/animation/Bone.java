package javagems3d.system.resources.assets.models.parsing.animation;

import org.joml.Matrix4f;

public final class Bone {
    private final int boneId;
    private final Matrix4f boneMatrix;

    public Bone(Matrix4f boneMatrix, int boneId) {
        this.boneMatrix = boneMatrix;
        this.boneId = boneId;
    }

    public int getBoneId() {
        return this.boneId;
    }

    public Matrix4f getBoneMatrix() {
        return this.boneMatrix;
    }
}
