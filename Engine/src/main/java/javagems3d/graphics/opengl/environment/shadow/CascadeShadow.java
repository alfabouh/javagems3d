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

package javagems3d.graphics.opengl.environment.shadow;

import org.joml.Matrix4f;

public class CascadeShadow {
    private final Matrix4f lightProjectionViewMatrix;
    private float splitDistance;

    public CascadeShadow() {
        this.lightProjectionViewMatrix = new Matrix4f();
    }

    public float getSplitDistance() {
        return this.splitDistance;
    }

    public void setSplitDistance(float splitDistance) {
        this.splitDistance = splitDistance;
    }

    public Matrix4f getLightProjectionViewMatrix() {
        return new Matrix4f(this.lightProjectionViewMatrix);
    }

    public void setLightProjectionViewMatrix(Matrix4f Matrix4f) {
        this.lightProjectionViewMatrix.set(Matrix4f);
    }
}
