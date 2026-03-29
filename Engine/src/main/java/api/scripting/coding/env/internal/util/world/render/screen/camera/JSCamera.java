package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.camera.base.ICamera;

@JSCodingClass(binding = "JSCameraBasic", description = "Base JS wrapper for any camera implementation.")
public class JSCamera implements JSCameraI {
    @JSHideFromDoc
    protected final ICamera camera;

    @JSHideFromDoc
    public JSCamera(ICamera camera) {
        this.camera = camera;
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Returns camera position.")
    public JSVector3f getCamPosition() {
        return new JSVector3f(this.camera.getCamPosition());
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Returns camera rotation.")
    public JSVector3f getCamRotation() {
        return new JSVector3f(this.camera.getCamRotation());
    }
    @Override
    @JSCodingFunctionOrMethod(description = "Real java object.")
    public ICamera getJavaCamera() {
        return this.camera;
    }
}