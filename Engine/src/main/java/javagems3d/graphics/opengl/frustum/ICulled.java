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

import org.joml.Vector3f;

public interface ICulled {
    boolean canBeCulled();

    RenderSphere calcRenderSphere();

    class RenderSphere {
        private final float radius;
        private final Vector3f center;

        public RenderSphere(float radius, Vector3f center) {
            this.radius = radius;
            this.center = center;
        }

        public float getRadius() {
            return this.radius;
        }

        public Vector3f getCenter() {
            return this.center;
        }
    }
}
