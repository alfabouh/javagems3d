package ru.alfabouh.jgems3d.engine.render.opengl.frustum;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class FrustumCulling {
    private final List<Vector4f> planes;
    private final FrustumIntersection frustumIntersection;
    private Matrix4f projectionViewMatrix;

    public FrustumCulling() {
        this.projectionViewMatrix = new Matrix4f();
        this.planes = new ArrayList<>();
        this.frustumIntersection = new FrustumIntersection();
        for (int i = 0; i < 6; i++) {
            this.planes.add(i, new Vector4f());
        }
    }

    public void refreshFrustumCullingState(Matrix4f projection, Matrix4f view) {
        this.projectionViewMatrix = new Matrix4f();
        this.projectionViewMatrix.mul(projection);
        this.projectionViewMatrix.mul(view);
        for (int i = 0; i < 6; i++) {
            this.projectionViewMatrix.frustumPlane(i, this.planes.get(i));
        }
        this.frustumIntersection.set(new Matrix4f(this.projectionViewMatrix));
    }

    public boolean isInFrustum(RenderABB renderABB) {
        if (renderABB == null) {
            return true;
        }
        return this.frustumIntersection.testAab(renderABB.getAbbMin(), renderABB.getAbbMax());
    }
}
