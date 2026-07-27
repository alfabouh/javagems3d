/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package javagems3d.graphics.rendering.scene.culling.bounds;

import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CullingAABB implements ICopyable<CullingAABB> {
    private Vector3f aabbMin;
    private Vector3f aabbMax;

    public CullingAABB(Vector3f aabbMin, Vector3f aabbMax) {
        this.aabbMin = aabbMin;
        this.aabbMax = aabbMax;
        this.check();
    }

    public static CullingAABB getCubeCullingAABB(Vector3f pos, Vector3f scale) {
        Vector3f sc = new Vector3f(scale).div(2.0f);
        return new CullingAABB(new Vector3f(pos).sub(sc), new Vector3f(pos).add(sc));
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

    public CullingAABB setAabbMin(Vector3f aabbMin) {
        this.aabbMin = aabbMin;
        return this;
    }

    public CullingAABB setAabbMax(Vector3f aabbMax) {
        this.aabbMax = aabbMax;
        return this;
    }

    public Vector3f getAabbMin() {
        return new Vector3f(this.aabbMin);
    }

    public Vector3f getAabbMax() {
        return new Vector3f(this.aabbMax);
    }

    @Override
    public CullingAABB copy() {
        return new CullingAABB(this.getAabbMin(), this.getAabbMax());
    }
}
