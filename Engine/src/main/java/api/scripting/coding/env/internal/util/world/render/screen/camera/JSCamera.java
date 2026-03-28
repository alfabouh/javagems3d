package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.base.CameraBase;
import javagems3d.graphics.camera.base.ICamera;

@JSCodingClass(binding = "JSCamera", description = "Base JS wrapper for any camera implementation.")
public abstract class JSCamera implements JSCameraI {
    @JSHideFromDoc
    protected final CameraBase camera;

    @JSHideFromDoc
    public JSCamera(CameraBase camera) {
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

    @JSCodingFunctionOrMethod(description = "Sets camera position.")
    public void setCamPosition(JSVector3f pos) {
        this.camera.setCameraPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Sets camera rotation.")
    public void setCamRotation(JSVector3f rot) {
        this.camera.setCameraRotation(rot.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Moves camera by given offset.")
    public void addPosition(JSVector3f delta) {
        this.camera.setCameraPosition(this.camera.getCamPosition().add(delta.getJavaVector3f()));
    }

    @JSCodingFunctionOrMethod(description = "Rotates camera by given offset.")
    public void addRotation(JSVector3f delta) {
        this.camera.setCameraRotation(this.camera.getCamRotation().add(delta.getJavaVector3f()));
    }

    @Override
    @JSHideFromDoc
    public ICamera getJavaCamera() {
        return this.camera;
    }

    @JSHideFromDoc
    public CameraBase getJavaCameraBase() {
        return this.camera;
    }
}