package ru.BouH.engine.render.frustum;

import org.joml.FrustumIntersection;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector4d;

import java.util.ArrayList;
import java.util.List;

public class FrustumCulling {
    private final List<Vector4d> planes;
    private final FrustumIntersection frustumIntersection;
    private Matrix4d projectionViewMatrix;
    private boolean enabled;

    public FrustumCulling() {
        this.projectionViewMatrix = new Matrix4d();
        this.planes = new ArrayList<>();
        this.frustumIntersection = new FrustumIntersection();
        for (int i = 0; i < 6; i++) {
            this.planes.add(i, new Vector4d());
        }
        this.enabled = true;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void refreshFrustumCullingState(Matrix4d projection, Matrix4d view) {
        this.projectionViewMatrix = new Matrix4d();
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
