package api.scripting.coding.env.internal.util.screen.render.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.ICamera;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSCameraI", description = "...")
public interface JSCameraI {
    @JSCodingFunctionOrMethod(description = "...") JSVector3f getCamPosition();
    @JSCodingFunctionOrMethod(description = "...") JSVector3f getCamRotation();

    @JSCodingFunctionOrMethod(description = "...") ICamera getJavaCamera();
}
