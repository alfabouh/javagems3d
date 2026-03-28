package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.FixedCamera;
import javagems3d.graphics.camera.base.ICamera;

@JSCodingClass(binding = "JSFixedCamera", description = "Wrapper for a fixed camera that can be positioned and rotated manually.")
public class JSFixedCamera implements JSCameraI {
    @JSHideFromDoc
    private final FixedCamera fixedCamera;

    @JSHideFromDoc
    public JSFixedCamera(FixedCamera fixedCamera) {
        this.fixedCamera = fixedCamera;
    }

    @JSCodingConstructor(description = "Create a fixed camera using an existing Java camera instance.", paramNames = {"camera"})
    public JSFixedCamera(JSCameraI camera) {
        this.fixedCamera = new FixedCamera(camera.getJavaCamera());
    }

    @JSCodingConstructor(description = "Create a fixed camera at a specific position and rotation.", paramNames = {"position", "rotation"})
    public JSFixedCamera(JSVector3f position, JSVector3f rotation) {
        this.fixedCamera = new FixedCamera(position.getJavaVector3f(), rotation.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set absolute camera position.", paramNames = {"position"})
    public void setPosition(JSVector3f position) {
        this.fixedCamera.setCameraPosition(position.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set absolute camera rotation.", paramNames = {"rotation"})
    public void setRotation(JSVector3f rotation) {
        this.fixedCamera.setCameraRotation(rotation.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Add offset to current camera position.", paramNames = {"offset"})
    public void addPosition(JSVector3f offset) {
        this.fixedCamera.addCameraPos(offset.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Add offset to current camera rotation.", paramNames = {"offset"})
    public void addRotation(JSVector3f offset) {
        this.fixedCamera.addCameraRot(offset.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Get current camera position.")
    @Override
    public JSVector3f getCamPosition() {
        return new JSVector3f(this.fixedCamera.getCamPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get current camera rotation.")
    @Override
    public JSVector3f getCamRotation() {
        return new JSVector3f(this.fixedCamera.getCamRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java Camera object (internal use, unsafe).")
    @Override
    public ICamera getJavaCamera() {
        return this.fixedCamera;
    }
}