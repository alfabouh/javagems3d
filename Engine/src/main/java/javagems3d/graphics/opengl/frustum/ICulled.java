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

import javagems3d.system.resources.assets.models.mesh.data.IMeshUserData;
import org.joml.Vector3f;

public interface ICulled {
    boolean canBeCulled();

    RenderAABB getRenderAABB();

    class RenderAABB implements IMeshUserData {
        private final Vector3f min;
        private final Vector3f max;

        public RenderAABB(Vector3f min, Vector3f max) {
            this.min = min;
            this.max = max;
        }

        public Vector3f getMax() {
            return this.max;
        }

        public Vector3f getMin() {
            return this.min;
        }
    }
}
