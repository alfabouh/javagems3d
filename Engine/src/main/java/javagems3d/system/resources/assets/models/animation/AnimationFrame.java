package javagems3d.system.resources.assets.models.animation;

import org.joml.Matrix4f;

public final class AnimationFrame {
    private Matrix4f[] boneMatrices;
    private int offset;

    public AnimationFrame(Matrix4f[] boneMatrices) {
        this.boneMatrices = boneMatrices;
    }

    public void clear() {
        this.boneMatrices = null;
    }

    public Matrix4f[] getBoneMatrices() {
        return this.boneMatrices;
    }

    public void setBoneMatrices(Matrix4f[] boneMatrices) {
        this.boneMatrices = boneMatrices;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
