package api.scripting.coding.env.internal.util.world.render.screen.transform;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.transformation.JGemsTransformManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSTransformationManager", description = "...")
public class JSTransformationManager implements JSGlobalVarFactory<JSTransformationManager> {
    @JSCodingFunctionOrMethod(description = "...")
    public JSMatrix4f getModelViewMatrix(JSModel3D model) {
        return new JSMatrix4f(JGemsTransformManager.getModelViewMatrix(model.getJavaModel3D()));
    }

    @JSCodingFunctionOrMethod(description = "...")
    public static JSMatrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return new JSMatrix4f(JGemsTransformManager.getAbstractCameraViewMatrix(camera));
    }

    public JSVector3f getProjectionData() {
        return new Vector3f(this.projectionData);
    }

    public JSMatrix4f getCameraViewMatrix() {
        return this.getCameraTransformation().getViewMatrix();
    }

    public JSMatrix4f getOrthographicMatrix() {
        return new Matrix4f(this.orthographicMatrix);
    }

    public JSMatrix4f getPerspectiveMatrix() {
        return new Matrix4f(this.perspectiveMatrix);
    }

    @Override
    public JSTransformationManager newGlobalVar() {
        return new JSTransformationManager();
    }

    @Override
    public String getVarName() {
        return "JSTransformationManager";
    }
}
