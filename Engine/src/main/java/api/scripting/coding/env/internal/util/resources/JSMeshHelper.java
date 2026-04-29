package api.scripting.coding.env.internal.util.resources;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel2D;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import api.scripting.coding.env.internal.util.resources.instances.models.poly.JSMesh2D;
import javagems3d.system.resources.assets.models.helper.MeshHelper;

@JSCodingClass(binding = "JSMeshHelper", description = "Helper for generating 2D and 3D meshes and models.")
public class JSMeshHelper implements JSGlobalVarFactory<JSMeshHelper> {
    @JSCodingFunctionOrMethod(description = "Generate a 2D plane model from two points at a given z-level.", paramNames = {"v1", "v2", "zLevel"})
    public JSModel2D generatePlane2DModel(JSVector2f v1, JSVector2f v2, float zLevel) {
        return new JSModel2D(MeshHelper.generatePlane2DModel(v1.getJavaVector2f(), v2.getJavaVector2f(), zLevel));
    }

    @JSCodingFunctionOrMethod(description = "Generate a 2D plane model with inverted vertices.", paramNames = {"v1", "v2", "zLevel"})
    public JSModel2D generatePlane2DModelInverted(JSVector2f v1, JSVector2f v2, float zLevel) {
        return new JSModel2D(MeshHelper.generatePlane2DModelInverted(v1.getJavaVector2f(), v2.getJavaVector2f(), zLevel));
    }

    @JSCodingFunctionOrMethod(description = "Generate a 2D vector mesh from two points.", paramNames = {"v1", "v2"})
    public JSMesh2D generateVector2DMesh(JSVector2f v1, JSVector2f v2) {
        return new JSMesh2D(MeshHelper.generateVector2fMesh(v1.getJavaVector2f(), v2.getJavaVector2f()));
    }

    @JSCodingFunctionOrMethod(description = "Generate a simple 3D plane model from four points.", paramNames = {"v1", "v2", "v3", "v4"})
    public JSModel3D generateSimplePlane3DModel(JSVector3f v1, JSVector3f v2, JSVector3f v3, JSVector3f v4) {
        return new JSModel3D(MeshHelper.generateSimplePlane3DModel(null, v1.getJavaVector3f(), v2.getJavaVector3f(), v3.getJavaVector3f(), v4.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Generate a 3D plane model from four points.", paramNames = {"v1", "v2", "v3", "v4"})
    public JSModel3D generatePlane3DModel(JSVector3f v1, JSVector3f v2, JSVector3f v3, JSVector3f v4) {
        return new JSModel3D(MeshHelper.generatePlane3DModel(null, v1.getJavaVector3f(), v2.getJavaVector3f(), v3.getJavaVector3f(), v4.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Generate a 3D vector model between two points.", paramNames = {"v1", "v2"})
    public JSModel3D generateVector3DModel(JSVector3f v1, JSVector3f v2) {
        return new JSModel3D(MeshHelper.generateVector3DModel3f(v1.getJavaVector3f(), v2.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Generate a wireframe box in 3D from min and max points.", paramNames = {"min", "max"})
    public JSModel3D generateWirebox3DModel(JSVector3f min, JSVector3f max) {
        return new JSModel3D(MeshHelper.generateWirebox3DModel(min.getJavaVector3f(), max.getJavaVector3f()));
    }

    @JSHideFromDoc
    @Override
    public JSMeshHelper newGlobalVar() {
        return new JSMeshHelper();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_MeshHelper";
    }
}