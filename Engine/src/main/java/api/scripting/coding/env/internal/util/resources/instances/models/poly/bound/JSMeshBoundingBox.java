package api.scripting.coding.env.internal.util.resources.instances.models.poly.bound;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.resources.instances.models.pose.JSModelPose3D;
import javagems3d.system.resources.assets.models.mesh.data.MeshBoundingBoxData;

@JSCodingClass(binding = "JSMeshBoundingBox", description = "...")
public class JSMeshBoundingBox {
    @JSHideFromDoc private final MeshBoundingBoxData meshBoundingBoxData;

    @JSHideFromDoc
    public JSMeshBoundingBox(MeshBoundingBoxData meshBoundingBoxData) {
        this.meshBoundingBoxData = meshBoundingBoxData;
    }

    public JSMeshBoundingBox(JSBoxAABB jsBoxAABB) {
        this(new MeshBoundingBoxData(jsBoxAABB.getJavaCullingAABB()));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSBoxAABB transformAABB(JSBoxAABB aabb, JSModelPose3D transform) {
        return new JSBoxAABB(this.meshBoundingBoxData.transformAABB(aabb.getJavaCullingAABB(), transform.getJavaPose3D()));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSBoxAABB getNormalizedAABB(JSModelPose3D pose) {
        return new JSBoxAABB(this.meshBoundingBoxData.getNormalizedAABB(pose.getJavaPose3D()));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public MeshBoundingBoxData getJavaMeshBoundingBoxData() {
        return this.meshBoundingBoxData;
    }
}
