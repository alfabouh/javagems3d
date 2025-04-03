package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class CPUFrustumCulling implements ICullingAlgorithm {
    private final List<Vector4f> planes;
    private final FrustumIntersection frustumIntersection;

    public CPUFrustumCulling() {
        this.planes = new ArrayList<>();
        this.frustumIntersection = new FrustumIntersection();
        for (int i = 0; i < 6; i++) {
            this.planes.add(i, new Vector4f());
        }
    }

    public void rebuildFrustum(Matrix4f projection, Matrix4f view) {
        Matrix4f projectionViewMatrix = new Matrix4f();
        projectionViewMatrix.mul(projection);
        projectionViewMatrix.mul(view);
        for (int i = 0; i < 6; i++) {
            projectionViewMatrix.frustumPlane(i, this.planes.get(i));
        }
        this.frustumIntersection.set(new Matrix4f(projectionViewMatrix));
    }

    public boolean isInFrustum(CullingAABB cullingAABB) {
        return this.frustumIntersection.testAab(cullingAABB.getAabbMin().x, cullingAABB.getAabbMin().y, cullingAABB.getAabbMin().z, cullingAABB.getAabbMax().x, cullingAABB.getAabbMax().y, cullingAABB.getAabbMax().z);
    }

    public boolean test(ICulled culled) {
        if (!culled.isCanBeCulled() || culled.getCullingRules().isIgnoreFrustumCulling()) {
            return true;
        }
        return this.isInFrustum(culled.getCullingData());
    }

    @Override
    public void filter(Collection<SceneObject> sceneObjects) {
        sceneObjects.removeIf(e -> !this.test(e));
    }
}