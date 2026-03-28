package api.scripting.coding.env.internal.util.controlling;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.controlling.bind.JSBindingManager;
import api.scripting.coding.env.internal.util.controlling.bind.keys.JSKey;
import api.scripting.coding.env.internal.util.math.JSVector2f;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.render.screen.JSWindow;
import javagems3d.system.controller.base.IController;
import javagems3d.system.controller.binding.BindingManager;
import org.joml.Vector2f;
import org.joml.Vector3f;

@JSCodingClass(binding = "JSController", description = "Wrapper for a player input controller, including movement, rotation, and key bindings.")
public class JSController {
    @JSHideFromDoc
    private final IController controller;

    @JSCodingConstructor(description = "Wraps an existing IController instance.", paramNames = {"controller"})
    public JSController(IController controller) {
        this.controller = controller;
    }

    @JSCodingFunctionOrMethod(description = "Returns normalized rotation input as a 2D vector (x = yaw, y = pitch).")
    public JSVector2f getNormalizedRotationInput() {
        Vector2f input = this.controller.getNormalizedRotationInput();
        return new JSVector2f(input.x, input.y);
    }

    @JSCodingFunctionOrMethod(description = "Returns normalized position input as a 3D vector (x = strafe, y = vertical, z = forward).")
    public JSVector3f getNormalizedPositionInput() {
        Vector3f input = this.controller.getNormalizedPositionInput();
        return new JSVector3f(input.x, input.y, input.z);
    }

    @JSCodingFunctionOrMethod(description = "Returns the BindingManager associated with this controller.")
    public JSBindingManager getBindingManager() {
        BindingManager manager = this.controller.getBindingManager();
        return new JSBindingManager(manager) {
            @Override
            public JSKey keyMoveLeft() {
                return new JSKey(manager.keyMoveLeft().getKeyCode());
            }

            @Override
            public JSKey keyMoveRight() {
                return new JSKey(manager.keyMoveRight().getKeyCode());
            }

            @Override
            public JSKey keyMoveForward() {
                return new JSKey(manager.keyMoveForward().getKeyCode());
            }

            @Override
            public JSKey keyMoveBackward() {
                return new JSKey(manager.keyMoveBackward().getKeyCode());
            }

            @Override
            public JSKey keyMoveUp() {
                return new JSKey(manager.keyMoveUp().getKeyCode());
            }

            @Override
            public JSKey keyMoveDown() {
                return new JSKey(manager.keyMoveDown().getKeyCode());
            }
        };
    }

    @JSCodingFunctionOrMethod(description = "Updates the controller state based on the provided window input.")
    public void updateControllerState(JSWindow window) {
        this.controller.updateControllerState(window.getJavaWindow());
    }

    @JSHideFromDoc
    public IController getJavaController() {
        return this.controller;
    }
}