package api.scripting.coding.env.internal.util.world.render.screen.camera;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.JSController;
import api.scripting.coding.env.internal.util.controlling.JSMouseKeyboardController;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import javagems3d.graphics.camera.ControlledCamera;
import javagems3d.graphics.camera.base.ICamera;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.base.MouseKeyboardController;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSControlledCamera", description = "Camera controlled by input controller (keyboard/mouse or other). Supports movement, rotation and speed control.")
public class JSControlledCamera implements JSCameraI {
    @JSHideFromDoc
    private final ControlledCamera camera;

    @JSCodingConstructor(description = "Creates controlled camera with controller, position and rotation.", paramNames = {"controller", "position", "rotation"})
    public JSControlledCamera(JSController controller, JSVector3f position, JSVector3f rotation) {
        this.camera = new ControlledCamera(controller != null ? controller.getJavaController() : null, position.getJavaVector3f(), rotation.getJavaVector3f());
    }

    @JSCodingConstructor(description = "Wraps existing controlled camera.", paramNames = {"camera"})
    public JSControlledCamera(ControlledCamera camera) {
        this.camera = camera;
    }

    @JSCodingFunctionOrMethod(description = "Returns current controller.")
    public JSMouseKeyboardController getController() {
        IController c = this.camera.getController();
        if (c instanceof MouseKeyboardController mk) {
            return new JSMouseKeyboardController(mk);
        }
        return null;
    }

    @JSCodingFunctionOrMethod(description = "Sets controller.")
    public void setController(JSMouseKeyboardController controller) {
        this.camera.setController(controller != null ? controller.getJavaController() : null);
    }

    @JSCodingFunctionOrMethod(description = "Sets camera movement speed.")
    public void setSpeed(float speed) {
        this.camera.setSpeed(speed);
    }

    @JSCodingFunctionOrMethod(description = "Returns camera movement speed.")
    public float getSpeed() {
        return this.camera.camSpeed();
    }

    @JSCodingFunctionOrMethod(description = "Updates camera logic (movement + rotation).")
    public void update(float deltaTime) {
        this.camera.updateCamera(deltaTime);
    }

    @JSCodingFunctionOrMethod(description = "Moves camera manually using direction vector.")
    public void move(JSVector3f direction) {
        this.camera.moveCamera(direction.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Rotates camera manually using XY input.")
    public void rotate(JSVector2f rotation) {
        this.camera.addCameraRot(new Vector3f(rotation.getJavaVector2f(), 0));
    }

    @JSHideFromDoc
    public ControlledCamera getJavaControlledCamera() {
        return this.camera;
    }

    @JSCodingFunctionOrMethod(description = "Get current camera position.")
    @Override
    public JSVector3f getCamPosition() {
        return new JSVector3f(this.camera.getCamPosition());
    }

    @JSCodingFunctionOrMethod(description = "Get current camera rotation.")
    @Override
    public JSVector3f getCamRotation() {
        return new JSVector3f(this.camera.getCamRotation());
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java Camera object (internal use, unsafe).")
    @Override
    public ICamera getJavaCamera() {
        return this.camera;
    }
}