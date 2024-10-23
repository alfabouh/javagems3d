/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.frustum;

import org.joml.FrustumIntersection;
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

    public boolean isInFrustum(ICulled.RenderAABB renderSphere) {
        if (renderSphere == null) {
            return true;
        }
        return this.frustumIntersection.testAab(renderSphere.getMin(), renderSphere.getMax());
    }
}
