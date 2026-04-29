package api.scripting.coding.env.internal.util.resources.instances.models.poly.bound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.pose.JSModelPose3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;

@JSCodingClass(binding = "JSMeshBoundingBox", description = "Wrapper for MeshBoundingBoxData providing AABB transformations and normalization.")
public class JSMeshBoundingBox {
    @JSHideFromDoc
    private final MeshBoundingBoxData meshBoundingBoxData;

    @JSHideFromDoc
    public JSMeshBoundingBox(MeshBoundingBoxData meshBoundingBoxData) {
        this.meshBoundingBoxData = meshBoundingBoxData;
    }

    @JSCodingConstructor(description = "Create mesh bounding box from JSBoxAABB", paramNames = {"jsBoxAABB"})
    public JSMeshBoundingBox(JSBoxAABB jsBoxAABB) {
        this(new MeshBoundingBoxData(jsBoxAABB.getJavaCullingAABB()));
    }

    @JSCodingFunctionOrMethod(description = "Transform an AABB by the given model pose", paramNames = {"aabb", "transform"})
    public JSBoxAABB transformAABB(JSBoxAABB aabb, JSModelPose3D transform) {
        return new JSBoxAABB(this.meshBoundingBoxData.transformAABB(aabb.getJavaCullingAABB(), transform.getJavaPose3D()));
    }

    @JSCodingFunctionOrMethod(description = "Get a normalized AABB for a given model pose", paramNames = {"pose"})
    public JSBoxAABB getNormalizedAABB(JSModelPose3D pose) {
        return new JSBoxAABB(this.meshBoundingBoxData.getNormalizedAABB(pose.getJavaPose3D()));
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java MeshBoundingBoxData object (unsafe)")
    @JSHideFromDoc
    public MeshBoundingBoxData getJavaMeshBoundingBoxData() {
        return this.meshBoundingBoxData;
    }
}