package ru.jgems3d.toolbox.controller.objects;

import org.joml.Vector2f;
import org.joml.Vector3f;
import ru.jgems3d.engine.graphics.opengl.screen.window.IWindow;
import ru.jgems3d.engine.system.controller.binding.BindingManager;
import ru.jgems3d.engine.system.controller.objects.MouseKeyboardController;
import ru.jgems3d.toolbox.controller.TBoxControllerDispatcher;
import ru.jgems3d.toolbox.render.scene.dear_imgui.content.EditorContent;

public class AlternateMouseKeyboardController extends MouseKeyboardController {
    private final Vector2f prevMouseCoord;

    public AlternateMouseKeyboardController(IWindow window, BindingManager bindingManager) {
        super(window, bindingManager);
        this.prevMouseCoord = new Vector2f();
    }

    @Override
    public void updateControllerState(IWindow window) {
        this.getMouseAndKeyboard().update(this.getBindingManager());
        this.getPositionInput().set(0.0d);
        this.getRotationInput().set(0.0d);
        this.normalizedPositionInput.set(0.0d);
        this.normalizedRotationInput.set(0.0d);

        double[] xy = this.getMouseAndKeyboard().getCursorCoordinates();
        float d1 = (float) (xy[0] - this.prevMouseCoord.x);
        float d2 = (float) (xy[1] - this.prevMouseCoord.y);

        if (!EditorContent.isFocusedOnSceneFrame) {
            this.prevMouseCoord.set(xy[0], xy[1]);
            return;
        }

        if (this.getMouseAndKeyboard().isLeftKeyPressed()) {
            this.getRotationInput().set(new Vector2f(d2, d1));
        }

        this.prevMouseCoord.set(xy[0], xy[1]);

        if (TBoxControllerDispatcher.bindingManager().keyA.isPressed()) {
            this.getPositionInput().add(-1.0f, 0.0f, 0.0f);
        }
        if (TBoxControllerDispatcher.bindingManager().keyD.isPressed()) {
            this.getPositionInput().add(1.0f, 0.0f, 0.0f);
        }
        if (TBoxControllerDispatcher.bindingManager().keyW.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, -1.0f);
        }
        if (TBoxControllerDispatcher.bindingManager().keyS.isPressed()) {
            this.getPositionInput().add(0.0f, 0.0f, 1.0f);
        }
        if (TBoxControllerDispatcher.bindingManager().keyUp.isPressed()) {
            this.getPositionInput().add(0.0f, 1.0f, 0.0f);
        }
        if (TBoxControllerDispatcher.bindingManager().keyShift.isPressed()) {
            this.getPositionInput().add(0.0f, -1.0f, 0.0f);
        }

        this.normalizedPositionInput.set(new Vector3f(this.getPositionInput().x == 0 ? 0 : this.getPositionInput().x > 0 ? 1 : -1, this.getPositionInput().y == 0 ? 0 : this.getPositionInput().y > 0 ? 1 : -1, this.getPositionInput().z == 0 ? 0 : this.getPositionInput().z > 0 ? 1 : -1));
        this.normalizedRotationInput.set(new Vector2f(this.getRotationInput()).mul(TBoxControllerDispatcher.CAM_SENS));
    }
}
