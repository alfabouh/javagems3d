package javagems3d.graphics.rendering.scene.culling.bounds;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CullingAABB {
    private Vector3f aabbMin;
    private Vector3f aabbMax;

    public CullingAABB(Vector3f aabbMin, Vector3f aabbMax) {
        this.aabbMin = aabbMin;
        this.aabbMax = aabbMax;
    }

    public Matrix4f createAABBTransformMatrix() {
        Vector3f center = new Vector3f(this.getAabbMin()).add(this.getAabbMax()).mul(0.5f);
        Vector3f scale = new Vector3f(this.getAabbMax()).sub(this.getAabbMin()).mul(0.5f);
        return new Matrix4f().translate(center).scale(scale);
    }

    public Vector3f getAabbMin() {
        return new Vector3f(this.aabbMin);
    }

    public void setAabbMin(Vector3f aabbMin) {
        this.aabbMin = aabbMin;
    }

    public Vector3f getAabbMax() {
        return new Vector3f(this.aabbMax);
    }

    public void setAabbMax(Vector3f aabbMax) {
        this.aabbMax = aabbMax;
    }
}
