package javagems3d.system.resources.assets.models.mesh.data;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.transformation.TransformUtils;
import javagems3d.system.resources.assets.models.pose.Pose3D;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class MeshBoundingBoxData {
    private final CullingAABB cullingAABB;

    public MeshBoundingBoxData(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    public CullingAABB transformAABB(CullingAABB aabb, Pose3D transform) {
        Matrix4f modelMatrix = TransformUtils.getModelMatrix(transform);

        Vector3f[] corners = new Vector3f[]{
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

        CullingAABB cullingAABB1 = new CullingAABB(newMin, newMax);
        if (cullingAABB1.getAabbMax().y - cullingAABB1.getAabbMin().y <= 1.0e-4f) {
            newMin.y += -0.001f;
            newMax.y += 0.001f;
        }
        return new CullingAABB(newMin, newMax);
    }
    
    public CullingAABB getNormalizedAABB(Pose3D pose) {
        return this.transformAABB(this.cullingAABB, pose);
    }
}
