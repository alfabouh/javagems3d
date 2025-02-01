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

package javagems3d.system.resources.assets.models.mesh.udata;

import javagems3d.graphics.rendering.scene.culling.bounds.CullingAABB;
import javagems3d.graphics.transformation.TransformationUtils;
import javagems3d.system.resources.assets.models.formats.Format3D;
import javagems3d.system.resources.assets.models.mesh.IMesh;
import javagems3d.system.resources.assets.models.mesh.structures.MeshStructure;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;

public class MeshAABBData implements IMeshUserData {
    private final CullingAABB cullingAABB;

    public MeshAABBData(CullingAABB cullingAABB) {
        this.cullingAABB = cullingAABB;
    }

    public static MeshAABBData create(MeshStructure<? extends MeshStructure.Node<?>> meshStructure) {
        List<? extends MeshStructure.Node<?>> list = meshStructure.getMeshNodes();
        if (list.isEmpty()) {
            return null;
        }

        Vector3f min = new Vector3f(Float.POSITIVE_INFINITY);
        Vector3f max = new Vector3f(Float.NEGATIVE_INFINITY);

        for (MeshStructure.Node<?> node : list) {
            IMesh mesh = node.getMesh();
            List<Float> positions = mesh.getVertexPositions();

            for (int i = 0; i < positions.size(); i += 3) {
                Vector3f vertex = new Vector3f(positions.get(i), positions.get(i + 1), positions.get(i + 2));
                min.min(vertex);
                max.max(vertex);
            }
        }

        return new MeshAABBData(new CullingAABB(min, max));
    }

    public CullingAABB transformAABB(CullingAABB aabb, Format3D transform) {
        Matrix4f modelMatrix = TransformationUtils.getModelMatrix(transform);

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

        return new CullingAABB(newMin, newMax);
    }
    
    public CullingAABB getCullingAABB(Format3D format3D) {
        return this.transformAABB(this.cullingAABB, format3D);
    }
}
