package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.world.instances.JSSceneEntity;
import javagems3d.graphics.camera.AttachedCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.graphics.objects.entities.SceneEntity;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSAttachedCamera", description = "Wrapper for AttachedCamera allowing camera attachment to scene entities with automatic position and rotation updates.")
public class JSAttachedCamera implements JSCameraI {
    @JSHideFromDoc
    private final AttachedCamera attachedCamera;

    @JSCodingConstructor(description = "Create a camera attached to a scene entity", paramNames = {"sceneEntity"})
    public JSAttachedCamera(@NotNull JSSceneEntity sceneEntity) {
        this.attachedCamera = new AttachedCamera(sceneEntity.getJavaSceneEntity());
    }

    @JSHideFromDoc
    public JSAttachedCamera(AttachedCamera attachedCamera) {
        this.attachedCamera = attachedCamera;
    }

    @JSCodingFunctionOrMethod(description = "Attach this camera to a different scene entity", paramNames = {"sceneEntity"})
    public void attachCameraOnItem(@NotNull JSSceneEntity sceneEntity) {
        this.attachedCamera.attachCameraOnItem(sceneEntity.getJavaSceneEntity());
    }

    @JSCodingFunctionOrMethod(description = "Get the currently attached scene entity")
    public JSSceneEntity getAttachedObject() {
        SceneEntity entity = this.attachedCamera.getAttachedObject();
        if (entity == null) {
            return null;
        }
        return new JSSceneEntity(entity);
    }

    @JSCodingFunctionOrMethod(description = "Get the camera's current world position")
    @Override
    public JSVector3f getCamPosition() {
        return new JSVector3f(this.attachedCamera.getCamPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get the camera's current rotation in world space")
    @Override
    public JSVector3f getCamRotation() {
        return new JSVector3f(this.attachedCamera.getCamRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java AttachedCamera object (unsafe, internal use)")
    public AttachedCamera getJavaAttachedCamera() {
        return this.attachedCamera;
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java Camera object (internal use, unsafe).")
    @Override
    public ICamera getJavaCamera() {
        return this.attachedCamera;
    }
}