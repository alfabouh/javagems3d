package javagems3d.graphics.rendering.scene.culling.bounds;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CullingAABB {
    private Vector3f center;
    private final Vector3f aabbMin;
    private final Vector3f aabbMax;

    public CullingAABB(Vector3f aabbMin, Vector3f aabbMax) {
        this.aabbMin = aabbMin;
        this.aabbMax = aabbMax;
        this.check();
    }

    private void check() {
        if (this.aabbMax.x - this.aabbMin.x <= 1.0e-4f) {
            aabbMin.x += -0.001f;
            aabbMax.x += 0.001f;
        }
        if (this.aabbMax.y - this.aabbMin.y <= 1.0e-4f) {
            aabbMin.y += -0.001f;
            aabbMax.y += 0.001f;
        }
        if (this.aabbMax.z - this.aabbMin.z <= 1.0e-4f) {
            aabbMin.z += -0.001f;
            aabbMax.z += 0.001f;
        }
    }

    public Matrix4f createAABBTransformMatrix() {
        Vector3f center = new Vector3f(this.aabbMin).add(this.aabbMax).mul(0.5f);
        Vector3f scale = new Vector3f(this.aabbMax).sub(this.aabbMin).mul(0.5f);
        return new Matrix4f().translate(center).scale(scale);
    }

    public Vector3f getAabbMin() {
        return new Vector3f(this.aabbMin);
    }

    public Vector3f getAabbMax() {
        return new Vector3f(this.aabbMax);
    }

    public Vector3f getCenter() {
        return new Vector3f(this.center);
    }
}
