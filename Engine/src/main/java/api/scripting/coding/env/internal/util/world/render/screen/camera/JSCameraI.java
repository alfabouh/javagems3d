package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.ICamera;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSCameraI", description = "Interface for all camera types, providing access to position, rotation, and underlying Java camera object.")
public interface JSCameraI {
    @JSCodingFunctionOrMethod(description = "Get the current world position of the camera.") JSVector3f getCamPosition();
    @JSCodingFunctionOrMethod(description = "Get the current world rotation of the camera (Euler angles).") JSVector3f getCamRotation();
    @JSCodingFunctionOrMethod(description = "Get underlying Java camera object (for internal use, unsafe).") ICamera getJavaCamera();
}