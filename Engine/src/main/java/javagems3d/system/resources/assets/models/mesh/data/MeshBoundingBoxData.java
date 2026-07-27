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

package javagems3d.system.resources.assets.models.mesh.data;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshBoundingBoxData implements ICopyable<MeshBoundingBoxData> {
    private final CullingAABB cullingAABB;

    public MeshBoundingBoxData(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    public static CullingAABB transformAABB(CullingAABB aabb, Matrix4f modelMatrix) {
        Vector3f[] corners = new Vector3f[] {
                new Vector3f(aabb.getAabbMin().x, aabb.getAabbMin().y, aabb.getAabbMin().z),
                new Vector3f(aabb.getAabbMin().x, aabb.getAabbMin().y, aabb.getAabbMax().z),
                new Vector3f(aabb.getAabbMin().x, aabb.getAabbMax().y, aabb.getAabbMin().z),
                new Vector3f(aabb.getAabbMin().x, aabb.getAabbMax().y, aabb.getAabbMax().z),
                new Vector3f(aabb.getAabbMax().x, aabb.getAabbMin().y, aabb.getAabbMin().z),
                new Vector3f(aabb.getAabbMax().x, aabb.getAabbMin().y, aabb.getAabbMax().z),
                new Vector3f(aabb.getAabbMax().x, aabb.getAabbMax().y, aabb.getAabbMin().z),
                new Vector3f(aabb.getAabbMax().x, aabb.getAabbMax().y, aabb.getAabbMax().z)
        };

        Vector3f newMin = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f newMax = new Vector3f(Float.NEGATIVE_INFINITY);

        for (Vector3f corner : corners) {
            Vector4f transformed = new Vector4f(corner, 1.0f).mul(modelMatrix);
            Vector3f newPos = new Vector3f(transformed.x, transformed.y, transformed.z);

            newMin.min(newPos);
            newMax.max(newPos);
        }

        return new CullingAABB(newMin, newMax);
    }

    public static CullingAABB transformAABB(CullingAABB aabb, Pose3D transform) {
        return MeshBoundingBoxData.transformAABB(aabb, TransformUtils.getModelMatrix(transform));
    }
    
    public CullingAABB getNormalizedAABB(Pose3D pose) {
        return MeshBoundingBoxData.transformAABB(this.cullingAABB, pose);
    }

    public CullingAABB getNormalizedAABB(Matrix4f modelMatrix) {
        return MeshBoundingBoxData.transformAABB(this.cullingAABB, modelMatrix);
    }

    @Override
    public MeshBoundingBoxData copy() {
        return new MeshBoundingBoxData(this.cullingAABB.copy());
    }
}
