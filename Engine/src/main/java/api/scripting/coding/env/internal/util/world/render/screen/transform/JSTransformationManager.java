package api.scripting.coding.env.internal.util.world.render.screen.transform;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSMatrix4f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.instances.models.JSModel3D;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.transformation.JGemsTransformManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSTransformationManager", description = "Manager for handling model, camera, and projection transformations.")
public class JSTransformationManager implements JSGlobalVarFactory<JSTransformationManager> {

    @JSCodingFunctionOrMethod(description = "Get Model-View matrix for a 3D model", paramNames = {"model"})
    public JSMatrix4f getModelViewMatrix(JSModel3D model) {
        return new JSMatrix4f(JGemsTransformManager.getModelViewMatrix(model.getJavaModel3D()));
    }

    @JSCodingFunctionOrMethod(description = "Get view matrix from an abstract camera", paramNames = {"camera"})
    public static JSMatrix4f getAbstractCameraViewMatrix(ICamera camera) {
        return new JSMatrix4f(JGemsTransformManager.getAbstractCameraViewMatrix(camera));
    }

    @JSCodingFunctionOrMethod(description = "Get projection data", paramNames = {})
    public JSVector3f getProjectionData() {
        return new JSVector3f(JGemsTransformManager.INSTANCE.getProjectionData());
    }

    @JSCodingFunctionOrMethod(description = "Get current camera view matrix", paramNames = {})
    public JSMatrix4f getCameraViewMatrix() {
        return new JSMatrix4f(JGemsTransformManager.INSTANCE.getCameraViewMatrix());
    }

    @JSCodingFunctionOrMethod(description = "Get orthographic projection matrix", paramNames = {})
    public JSMatrix4f getOrthographicMatrix() {
        return new JSMatrix4f(JGemsTransformManager.INSTANCE.getOrthographicMatrix());
    }

    @JSCodingFunctionOrMethod(description = "Get perspective projection matrix", paramNames = {})
    public JSMatrix4f getPerspectiveMatrix() {
        return new JSMatrix4f(JGemsTransformManager.INSTANCE.getPerspectiveMatrix());
    }

    @JSHideFromDoc
    @Override
    public JSTransformationManager newGlobalVar() {
        return new JSTransformationManager();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_TransformationManager";
    }
}