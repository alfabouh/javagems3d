package javagems3d.system.resources.assets.models.animation.components;

import org.joml.Matrix4f;

public final class Bone {
    private final int boneId;
    private final String boneName;
    private final Matrix4f offset;

    public Bone(int boneId, String boneName, Matrix4f offset) {
        this.boneId = boneId;
        this.boneName = boneName;
        this.offset = offset;
    }

    public int getBoneId() {
        return this.boneId;
    }

    public String getBoneName() {
        return this.boneName;
    }

    public Matrix4f getOffset() {
        return this.offset;
    }
}
