package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.camera.base.ICamera;

@JSCodingClass(binding = "JSCameraBasic", description = "Base JS wrapper for any camera implementation.")
public class JSCameraBasic extends JSCamera {
    @JSHideFromDoc
    public JSCameraBasic(CameraBase camera) {
        super(camera);
    }

    @JSCodingFunctionOrMethod(description = "Sets camera position.")
    public void setCamPosition(JSVector3f pos) {
        this.getJavaCameraBase().setCameraPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Sets camera rotation.")
    public void setCamRotation(JSVector3f rot) {
        this.getJavaCameraBase().setCameraRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Moves camera by given offset.")
    public void addPosition(JSVector3f delta) {
        this.getJavaCameraBase().setCameraPosition(this.camera.getCamPosition().add(delta.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Rotates camera by given offset.")
    public void addRotation(JSVector3f delta) {
        this.getJavaCameraBase().setCameraRotation(this.camera.getCamRotation().add(delta.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Real java object.")
    public CameraBase getJavaCameraBase() {
        return (CameraBase) this.camera;
    }
}