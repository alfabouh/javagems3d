package api.scripting.coding.env.internal.util.screen.render.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneEntity;
import logger.Log;

@JSCodingClass(binding = "JSAttachedCamera", description = "...")
public class JSAttachedCamera implements JSCameraI {
    @JSHideFromDoc
    private final AttachedCamera attachedCamera;

    @JSHideFromDoc
    public JSAttachedCamera(AttachedCamera attachedCamera) {
        this.attachedCamera = attachedCamera;
    }

    @JSCodingFunctionOrMethod(description = "...")
    public void attachCameraOnItem(JSSceneEntity sceneEntity) {
        this.attachedCamera.attachCameraOnItem(sceneEntity.getJavaSceneEntity());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public JSSceneEntity getAttachedObject() {
        return new JSSceneEntity(this.attachedCamera.getAttachedObject());
    }

    @JSCodingFunctionOrMethod(description = "...")
    public AttachedCamera getJavaAttachedCamera() {
        return this.attachedCamera;
    }

    @Override
    public JSVector3f getCamPosition() {
        return new JSVector3f(this.attachedCamera.getCamPosition());
    }

    @Override
    public JSVector3f getCamRotation() {
        return new JSVector3f(this.attachedCamera.getCamRotation());
    }

    @JSHideFromDoc
    @Override
    public ICamera getJavaCamera() {
        return this.attachedCamera;
    }
}
