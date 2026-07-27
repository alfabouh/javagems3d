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

package javagems3d.graphics.rendering.scene.culling.stages;

import javagems3d.graphics.environment.decals.fx.DecalFX;
import javagems3d.graphics.objects.ICulled;
import javagems3d.graphics.objects.SceneObject;
import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.rendering.scene.renderer.JGemsOpenGLRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
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
        this.frustumIntersection.set(projectionViewMatrix);
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
    public void filter(@NotNull Collection<? extends ICulled> sceneObjects) {
        sceneObjects.removeIf(e -> {
            if (!this.test(e)) {
                JGemsOpenGLRenderer.DEBUG_CULLED_OBJECTS++;
                return true;
            }
            return false;
        });
    }
}